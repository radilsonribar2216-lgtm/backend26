# Clínica Veterinaria - integración funcional

## Orden de instalación de la base de datos

1. Crear la base de datos ejecutando primero `sql/clinica.sql` (solo para una instalación nueva: este script elimina y recrea las tablas base).
2. Ejecutar `sql/clinica_integral.sql` para inventario, lotes, facturación y usuarios.
3. Ejecutar `sql/seguridad_inventario.sql` para sesiones y vínculo entre líneas de factura y lotes.

No ejecutar `sql/clinica.sql` sobre una base de datos que ya tenga información que se quiera conservar.

## Configurar primer administrador

Antes de iniciar Spring Boot, definir una variable de entorno llamada `CLINICA_BOOTSTRAP_KEY` con una clave larga y aleatoria. No guardar esa clave en el repositorio.

Ejemplo Linux/macOS:
```bash
export CLINICA_BOOTSTRAP_KEY='una-clave-larga-y-aleatoria'
```

Ejemplo PowerShell:
```powershell
$env:CLINICA_BOOTSTRAP_KEY = "una-clave-larga-y-aleatoria"
```

Con el backend iniciado, enviar una sola vez un POST a `/clinica/v1/auth/bootstrap`, incluyendo el encabezado `X-Setup-Key` con el mismo valor y este JSON:

```json
{"nombre":"Administrador","email":"admin@clinica.local","password":"UnaClaveSeguraDeMasDe10"}
```

El endpoint solo funciona mientras no exista ningún usuario y requiere la clave de entorno. Luego iniciar sesión desde la pantalla del frontend. Crear desde allí los usuarios de Recepción y Médico.

## Acceso por perfiles

- Administrador: todos los módulos y administración de usuarios.
- Recepción: citas, inventario, facturación y reportes.
- Médico: citas, historias clínicas y fórmulas médicas.

El backend valida el token y el rol; ocultar enlaces en el frontend es solo una ayuda visual, no el control de seguridad principal.

## Inventario y facturación

Al crear una línea de factura, indicar el ID de producto si corresponde a un artículo inventariable. La prefactura no descuenta existencias. Al emitirla, el backend descuenta unidades de lotes vigentes (primero los que vencen antes) y registra movimientos de salida. Si no hay stock/lotes suficientes, la emisión se revierte.

## Configuración local

El perfil de desarrollo existente usa MySQL en `localhost:3306`, base `clinica`. Ajustar usuario y contraseña de conexión mediante variables/propiedades locales antes de ejecutar en otro equipo. No publicar contraseñas reales.

## Validación automática

GitHub Actions ejecuta `mvn verify` y `npm ci && npm run build` al subir cambios a esta rama y en pull requests hacia main/master.
