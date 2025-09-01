# API RESTful de Usuarios

Esta aplicación expone una **API RESTful** para la **gestión de usuarios**, cumpliendo con los requisitos de la prueba técnica.  
  
Los datos se almacenan en una base de datos **H2 en memoria** y se exponen en formato **JSON**.  

## Características

- Registro de usuarios con validaciones
- Base de datos H2 en memoria
- Tokens JWT para autenticación
- Validaciones personalizables mediante regex
- Documentación con Swagger
- Pruebas unitarias

## Tecnologías utilizadas

- Java 11+
- Spring Boot 2.7.10
- Spring Data JPA
- H2 Database (en memoria)
- JWT (JSON Web Tokens)
- Maven
- Swagger/OpenAPI
- JUnit 5 y Mockito
- Lombok

## Requisitos previos

- Java 11 o superior
- Maven 3.6+

## Instalación y ejecución

1. **Clonar el repositorio:**
```bash
git clone <url-del-repositorio>
cd user-api
```

2. **Compilar el proyecto:**
```bash
mvn clean compile
```

3. **Ejecutar las pruebas:**
```bash
mvn test
```

4. **Ejecutar la aplicación:**
```bash
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

## Endpoints

### POST /api/users
Crea un nuevo usuario en el sistema.

**Request Body:**
```json
{
  "n": "Juan Rodriguez",
  "email": "juan@rodriguez.org",
  "password": "Hunter123",
  "phones": [
    {
      "number": "1234567",
      "citycode": "1",
      "contrycode": "57"
    }
  ]
}
```

**Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "n": "Juan Rodriguez",
  "email": "juan@rodriguez.org",
  "phones": [
    {
      "number": "1234567",
      "citycode": "1",
      "contrycode": "57"
    }
  ],
  "created": "2023-10-15T10:30:00",
  "modified": "2023-10-15T10:30:00",
  "last_login": "2023-10-15T10:30:00",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "isactive": true
}
```

**Response (400 Bad Request):**
```json
{
  "mensaje": "El correo ya registrado"
}
```
### GET /api/users
Devuelve el listado de usuarios registrados.

**Response (200 OK)**
```json
[{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "n": "Juan Rodriguez",
  "email": "juan@rodriguez.org",
  "phones": [
    {
      "number": "1234567",
      "citycode": "1",
      "contrycode": "57"
    }
  ],
  "created": "2023-10-15T10:30:00",
  "modified": "2023-10-15T10:30:00",
  "last_login": "2023-10-15T10:30:00",
  "isactive": true
}]
```

## Validaciones

### Email
Por defecto: `^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}$`

Ejemplo válido: `usuario@dominio.cl`

### Password
Por defecto: `^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$`

Requisitos:
- Al menos 8 caracteres
- Al menos una minúscula
- Al menos una mayúscula  
- Al menos un dígito

Ejemplo válido: `MyPass123`

## Configuración

Las expresiones regulares son configurables en `application.properties`:

```properties
# Regex para validación de email
app.email.regex=^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}$

# Regex para validación de contraseña
app.password.regex=^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$

# Configuración JWT
jwt.secret=A2f4T9mX+8bV7jNpLkQhYzXoF3c1uG9eR6wT0pD8kJvS5rZlYq==
jwt.expiration.hours=24
jwt.issuer=user-registration-api
```

## Base de Datos

La aplicación utiliza H2 en memoria. Puedes acceder a la consola H2 en:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Usuario: `sa`
- Password: `password`

### Script de creación de BD

El esquema se crea automáticamente con JPA/Hibernate:

```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    created TIMESTAMP NOT NULL,
    modified TIMESTAMP NOT NULL,
    last_login TIMESTAMP NOT NULL,
    token TEXT NOT NULL,
    is_active BOOLEAN NOT NULL
);

CREATE TABLE phones (
    id UUID PRIMARY KEY,
    number VARCHAR(255) NOT NULL,
    cityCode VARCHAR(255) NOT NULL,
    contryCode VARCHAR(255) NOT NULL,
    user_id UUID,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

## Documentación API (Swagger)

Una vez que la aplicación esté ejecutándose, puedes acceder a la documentación interactiva:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- JSON API Docs: `http://localhost:8080/v3/api-docs`

## Pruebas

Ejecutar todas las pruebas:
```bash
mvn test
```

## Ejemplos de uso

### Crear usuario exitoso
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "n": "Juan Rodriguez",
    "email": "juan@rodriguez.org",
    "password": "Hunter123",
    "phones": [
      {
        "number": "1234567",
        "citycode": "1",
        "contrycode": "57"
      }
    ]
  }'
```

### Email ya registrado
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "n": "Pedro Martinez",
    "email": "juan@rodriguez.org",
    "password": "Password123",
    "phones": []
  }'
```

Respuesta:
```json
{
  "mensaje": "El correo ya registrado"
}
```

### Email con formato inválido
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "n": "Ana Lopez",
    "email": "email-invalido",
    "password": "Password123",
    "phones": []
  }'
```

Respuesta:
```json
{
  "mensaje": "El formato del correo es inválido"
}
```

## Estructura del proyecto

```
src/
├── main/
│   ├── java/
│   │   └── com/example/userapi/
│   │       ├── UserApiApplication.java
|   |       ├── config/
|   |       |   ├── OpenApiConfig.java
│   │       │   ├── SecurityConfig.java
│   │       ├── security/
|   |       |   └── JwtAuthenticationFilter.java
│   │       ├── exception/
│   │       │   ├── GlobalExceptionHandler.java
│   │       │   └── UserAlreadyExistsException.java
│   │       ├── controller/
│   │       │   └── UserController.java
│   │       ├── dto/
│   │       │   ├── CreateUserRequest.java
│   │       │   ├── CreateUserResponse.java
│   │       │   ├── PhoneDto.java
│   │       │   └── ErrorResponse.java
│   │       ├── entity/
│   │       │   ├── User.java
│   │       │   └── Phone.java
│   │       ├── repository/
│   │       │   └── UserRepository.java
│   │       ├── service/
│   │       │   └── UserService.java
│   │       └── util/
│   │           └── ValidationUtil.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/
        └── com/example/userapi/
            └── service/
                └── UserServiceTest.java
```

## Diagrama de la solución

![Diagrama](https://github.com/NatalyOC/bci-user-service-challenge/blob/feature/challenge/Diagrama.png)


## Autor

Desarrollado como prueba técnica para demostrar conocimientos en:
- Arquitectura REST
- Spring Boot y ecosystem
- Validaciones personalizadas
- JWT y seguridad
- Pruebas unitarias
- Documentación de APIs