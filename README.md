# Backend - Clínica veterinaria (Segundo parcial de Programación Web)

API REST en **Spring Boot 4 / Java 21 / Maven / MySQL**.

## Ejecutar
1. Cargar la base de datos: `mysql -u root -p < sql/clinica.sql` (crea la base `clinica` con datos de prueba).
2. Revisar usuario y contraseña en `src/main/resources/application-dev.properties` (por defecto `root` / `1234`).
3. Iniciar: abrir la carpeta en IntelliJ y ejecutar `ClinicaApplication`, o por terminal `./mvnw spring-boot:run`.
4. La API queda en `http://localhost:8080/clinica/v1` (por ejemplo `/citas`, `/formulas`, `/historias-clinicas`).
5. Probar con `http/clinica.http` (se ejecuta directamente desde IntelliJ).

## Estructura (`com.uniminuto.clinica`)
| Paquete | Responsabilidad |
|---|---|
| `api` | Interfaces con los mapeos REST |
| `apicontroller` | Controllers que implementan la interfaz y solo delegan |
| `service` / `serviceimpl` | Reglas de negocio y transacciones |
| `repository` | Acceso a datos (Spring Data JPA) |
| `entities` | Entidades JPA |
| `models/rq`, `models/rs` | DTOs de entrada y salida |
| `specification` | Filtros dinámicos (JPA Specifications) |
| `exception` | Excepciones propias y manejador global de errores |
| `util` | Mapeador de entidades a DTOs y utilidades de paginación |
