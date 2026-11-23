package com.uniminuto.clinica.apicontroller;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthApiController {
    private final JdbcTemplate jdbc;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final SecureRandom random = new SecureRandom();

    public AuthApiController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @PostMapping("/bootstrap")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String,Object> bootstrap(@RequestHeader("X-Setup-Key") String setupKey,
                                        @RequestBody Map<String,Object> body) {
        String expected = System.getenv("CLINICA_BOOTSTRAP_KEY");
        if (expected == null || expected.isBlank() || !MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8), setupKey.getBytes(StandardCharsets.UTF_8)))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Clave inicial no válida.");
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM usuario", Integer.class);
        if (count != null && count > 0)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El administrador inicial ya fue creado.");
        String nombre = required(body,"nombre"), email = required(body,"email").toLowerCase();
        String password = required(body,"password");
        validatePassword(password);
        jdbc.update("INSERT INTO usuario(nombre,email,password_hash,rol) VALUES(?,?,?,'ADMINISTRADOR')",
                nombre,email,encoder.encode(password));
        return Map.of("mensaje","Administrador inicial creado. Ya puedes iniciar sesión.");
    }

    @PostMapping("/login")
    public Map<String,Object> login(@RequestBody Map<String,Object> body) {
        String email = required(body,"email").toLowerCase();
        String password = required(body,"password");
        List<Map<String,Object>> rows = jdbc.queryForList(
                "SELECT id,nombre,email,password_hash,rol,activo FROM usuario WHERE email=?",email);
        if (rows.isEmpty() || !Boolean.TRUE.equals(((Number)rows.get(0).get("activo")).intValue()==1))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Credenciales incorrectas.");
        Map<String,Object> user = rows.get(0);
        if (!encoder.matches(password,String.valueOf(user.get("password_hash"))))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Credenciales incorrectas.");
        byte[] bytes = new byte[32]; random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        jdbc.update("INSERT INTO sesion_usuario(usuario_id,token_hash,expira_en) VALUES(?,?,?)",
                user.get("id"),hash(token),LocalDateTime.now().plusHours(12));
        return Map.of("token",token,"tipo","Bearer","expiraEn",LocalDateTime.now().plusHours(12).toString(),
                "usuario",Map.of("id",user.get("id"),"nombre",user.get("nombre"),
                        "email",user.get("email"),"rol",user.get("rol")));
    }

    @GetMapping("/me")
    public Map<String,Object> me(@RequestHeader("Authorization") String authorization) {
        return currentUser(token(authorization));
    }

    @PostMapping("/logout")
    public Map<String,String> logout(@RequestHeader("Authorization") String authorization) {
        jdbc.update("UPDATE sesion_usuario SET revocada=1 WHERE token_hash=?",hash(token(authorization)));
        return Map.of("mensaje","Sesión cerrada.");
    }

    @GetMapping("/usuarios")
    public List<Map<String,Object>> users() {
        return jdbc.queryForList("SELECT id,nombre,email,rol,veterinario_id,activo,created_at FROM usuario ORDER BY nombre");
    }

    @PostMapping("/usuarios")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String,Object> createUser(@RequestBody Map<String,Object> body) {
        String nombre=required(body,"nombre"), email=required(body,"email").toLowerCase();
        String password=required(body,"password"), rol=required(body,"rol").toUpperCase();
        if (!List.of("ADMINISTRADOR","RECEPCION","MEDICO").contains(rol))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Rol no válido.");
        validatePassword(password);
        Object vet=body.get("veterinarioId");
        jdbc.update("INSERT INTO usuario(nombre,email,password_hash,rol,veterinario_id) VALUES(?,?,?,?,?)",
                nombre,email,encoder.encode(password),rol,vet==null?null:Long.valueOf(vet.toString()));
        Long id=jdbc.queryForObject("SELECT LAST_INSERT_ID()",Long.class);
        return jdbc.queryForMap("SELECT id,nombre,email,rol,veterinario_id,activo,created_at FROM usuario WHERE id=?",id);
    }

    @PatchMapping("/usuarios/{id}/estado")
    public Map<String,Object> setUserState(@PathVariable long id,@RequestBody Map<String,Object> body) {
        Object activo=body.get("activo");
        if (!(activo instanceof Boolean)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"activo debe ser booleano.");
        jdbc.update("UPDATE usuario SET activo=? WHERE id=?",activo,id);
        return jdbc.queryForMap("SELECT id,nombre,email,rol,veterinario_id,activo FROM usuario WHERE id=?",id);
    }

    public Map<String,Object> currentUser(String token) {
        List<Map<String,Object>> rows=jdbc.queryForList("""
            SELECT u.id,u.nombre,u.email,u.rol,u.veterinario_id,u.activo
            FROM sesion_usuario s JOIN usuario u ON u.id=s.usuario_id
            WHERE s.token_hash=? AND s.revocada=0 AND s.expira_en>NOW() AND u.activo=1
            """,hash(token));
        if(rows.isEmpty()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Sesión inválida o vencida.");
        return rows.get(0);
    }

    private String token(String header) {
        if(header==null || !header.startsWith("Bearer "))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Falta el token Bearer.");
        return header.substring(7).trim();
    }
    private String hash(String token) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8))); }
        catch(Exception e){ throw new IllegalStateException(e); }
    }
    private String required(Map<String,Object> body,String key) {
        Object value=body.get(key);
        if(value==null || value.toString().isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,key+" es obligatorio.");
        return value.toString().trim();
    }
    private void validatePassword(String password) {
        if(password.length()<10) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"La contraseña debe tener al menos 10 caracteres.");
    }
}
