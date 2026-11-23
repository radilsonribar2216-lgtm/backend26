package com.uniminuto.clinica.apicontroller;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

/** Exportación de informes de inventario y facturación a PDF y Excel. */
@RestController
@RequestMapping("/reportes")
public class ReportesApiController {
    private final JdbcTemplate jdbc;

    public ReportesApiController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/inventario")
    public List<Map<String,Object>> inventario(
            @RequestParam(required=false) Boolean bajoMinimo) {
        String sql = """
            SELECT codigo,nombre,categoria,precio_venta,existencia,existencia_minima
            FROM producto WHERE activo=1
            """ + (Boolean.TRUE.equals(bajoMinimo) ? " AND existencia<=existencia_minima " : "")
            + " ORDER BY nombre";
        return jdbc.queryForList(sql);
    }

    @GetMapping("/facturacion")
    public List<Map<String,Object>> facturacion(@RequestParam String desde, @RequestParam String hasta) {
        return jdbc.queryForList("""
            SELECT f.numero, f.fecha_emision, CONCAT(c.nombres,' ',c.apellidos) AS cliente,
                   f.subtotal,f.descuento,f.impuesto,f.total,f.estado,
                   COALESCE((SELECT SUM(p.valor) FROM pago p WHERE p.factura_id=f.id),0) AS pagado
            FROM factura f JOIN cliente c ON c.id=f.cliente_id
            WHERE DATE(f.fecha_emision) BETWEEN ? AND ? AND f.estado<>'ANULADA'
            ORDER BY f.fecha_emision DESC
            """, desde, hasta);
    }

    @GetMapping(value="/inventario.xlsx", produces="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> inventarioExcel(@RequestParam(required=false) Boolean bajoMinimo) throws Exception {
        List<Map<String,Object>> rows = inventario(bajoMinimo);
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            var sheet = wb.createSheet("Inventario");
            Row header = sheet.createRow(0);
            String[] cols = {"Código","Producto","Categoría","Precio","Existencia","Mínimo"};
            for (int i=0;i<cols.length;i++) header.createCell(i).setCellValue(cols[i]);
            int n=1;
            for (Map<String,Object> item : rows) {
                Row row=sheet.createRow(n++);
                row.createCell(0).setCellValue(String.valueOf(item.get("codigo")));
                row.createCell(1).setCellValue(String.valueOf(item.get("nombre")));
                row.createCell(2).setCellValue(String.valueOf(item.get("categoria")));
                row.createCell(3).setCellValue(((Number)item.get("precio_venta")).doubleValue());
                row.createCell(4).setCellValue(((Number)item.get("existencia")).doubleValue());
                row.createCell(5).setCellValue(((Number)item.get("existencia_minima")).doubleValue());
            }
            for(int i=0;i<cols.length;i++) sheet.autoSizeColumn(i);
            wb.write(out);
            return descarga(out.toByteArray(),"inventario.xlsx","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        }
    }

    @GetMapping(value="/facturacion.xlsx", produces="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> facturacionExcel(@RequestParam String desde,@RequestParam String hasta) throws Exception {
        List<Map<String,Object>> rows=facturacion(desde,hasta);
        try(XSSFWorkbook wb=new XSSFWorkbook();ByteArrayOutputStream out=new ByteArrayOutputStream()){
            var sheet=wb.createSheet("Facturación");
            String[] cols={"Número","Fecha","Cliente","Subtotal","Descuento","Impuesto","Total","Estado","Pagado"};
            Row header=sheet.createRow(0);
            for(int i=0;i<cols.length;i++)header.createCell(i).setCellValue(cols[i]);
            int n=1;
            for(Map<String,Object> item:rows){
                Row row=sheet.createRow(n++);
                row.createCell(0).setCellValue(String.valueOf(item.get("numero")));
                row.createCell(1).setCellValue(String.valueOf(item.get("fecha_emision")));
                row.createCell(2).setCellValue(String.valueOf(item.get("cliente")));
                String[] keys={"subtotal","descuento","impuesto","total"};
                for(int i=0;i<keys.length;i++)row.createCell(i+3).setCellValue(((Number)item.get(keys[i])).doubleValue());
                row.createCell(7).setCellValue(String.valueOf(item.get("estado")));
                row.createCell(8).setCellValue(((Number)item.get("pagado")).doubleValue());
            }
            for(int i=0;i<cols.length;i++)sheet.autoSizeColumn(i);
            wb.write(out);
            return descarga(out.toByteArray(),"facturacion.xlsx","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        }
    }

    @GetMapping(value="/inventario.pdf", produces=MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> inventarioPdf(@RequestParam(required=false) Boolean bajoMinimo) throws Exception {
        List<Map<String,Object>> rows=inventario(bajoMinimo);
        return pdfTabla("Reporte de inventario",new String[]{"Código","Producto","Categoría","Precio","Existencia","Mínimo"},rows,
            new String[]{"codigo","nombre","categoria","precio_venta","existencia","existencia_minima"},"inventario.pdf");
    }

    @GetMapping(value="/facturacion.pdf", produces=MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> facturacionPdf(@RequestParam String desde,@RequestParam String hasta) throws Exception {
        List<Map<String,Object>> rows=facturacion(desde,hasta);
        return pdfTabla("Reporte de facturación ("+desde+" a "+hasta+")",
            new String[]{"Número","Fecha","Cliente","Subtotal","Descuento","Impuesto","Total","Estado","Pagado"},rows,
            new String[]{"numero","fecha_emision","cliente","subtotal","descuento","impuesto","total","estado","pagado"},"facturacion.pdf");
    }

    private ResponseEntity<byte[]> pdfTabla(String titulo,String[] headers,List<Map<String,Object>> rows,String[] keys,String filename) throws Exception {
        try(ByteArrayOutputStream out=new ByteArrayOutputStream()){
            Document doc=new Document();
            PdfWriter.getInstance(doc,out);
            doc.open();
            doc.add(new Paragraph(titulo,new Font(Font.HELVETICA,16,Font.BOLD)));
            doc.add(new Paragraph("Generado por el sistema de Clínica Veterinaria"));
            PdfPTable table=new PdfPTable(headers.length);
            table.setWidthPercentage(100);
            for(String h:headers){PdfPCell cell=new PdfPCell(new Phrase(h));table.addCell(cell);}
            for(Map<String,Object> item:rows)for(String key:keys)table.addCell(String.valueOf(item.get(key)));
            doc.add(table);
            doc.close();
            return descarga(out.toByteArray(),filename,MediaType.APPLICATION_PDF_VALUE);
        }
    }

    private ResponseEntity<byte[]> descarga(byte[] bytes,String filename,String type){
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+filename+"\"")
            .contentType(MediaType.parseMediaType(type))
            .body(bytes);
    }
}
