# Reto Onboarding Reactivo

Aplicación reactiva para gestión de usuarios con integración a APIs externas, cache Redis y base de datos NoSQL.

## Requisitos Previos

- Java 17+
- Docker/Podman
- PostgreSQL
- Redis
- LocalStack (AWS SQS y DynamoDB)

## Configuración

### 1. Base de Datos PostgreSQL
```sql
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY,
    email VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    avatar VARCHAR(500)
);
```

### 2. Redis
```bash
podman run -d --name redis -p 6379:6379 redis:alpine
```

### 3. LocalStack (SQS y DynamoDB)
```bash
podman run -d --name localstack -p 4566:4566 localstack/localstack
```

#### Crear cola SQS:
```bash
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name user-events
```

#### Crear tabla DynamoDB:
```bash
aws --endpoint-url=http://localhost:4566 dynamodb create-table \
  --table-name users_uppercase \
  --attribute-definitions AttributeName=id,AttributeType=N \
  --key-schema AttributeName=id,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST
```

## Variables de Entorno

```properties
# PostgreSQL
spring.r2dbc.url=r2dbc:postgresql://localhost:5432/nequi_db
spring.r2dbc.username=postgres
spring.r2dbc.password=password

# Redis
spring.redis.host=localhost
spring.redis.port=6379

# AWS LocalStack
cloud.aws.sqs.queue-url=http://localhost:4566/000000000000/user-events
cloud.aws.region.static=us-east-1
cloud.aws.credentials.access-key=test
cloud.aws.credentials.secret-key=test
cloud.aws.sqs.endpoint=http://localhost:4566
cloud.aws.dynamodb.endpoint=http://localhost:4566
```

## Endpoints

### Crear Usuario
```
POST /api/v1/user/{id}
```
Obtiene usuario de https://reqres.in/api/users/{id} y lo guarda en PostgreSQL.

### Consultar Usuario por ID
```
GET /api/v1/user/{id}
```
Busca primero en cache Redis, luego en PostgreSQL.

### Consultar Todos los Usuarios
```
GET /api/v1/users
```

### Buscar Usuarios por Nombre
```
GET /api/v1/users/search?name={nombre}
```

## Flujo de la Aplicación

1. **Crear Usuario**: API externa → PostgreSQL → Cache Redis → Evento SQS
2. **Consultar Usuario**: Cache Redis → PostgreSQL (si no está en cache)
3. **Procesamiento Asíncrono**: SQS → Transformar a mayúsculas → DynamoDB

## Ejecución

```bash
./gradlew bootRun
```

La aplicación estará disponible en `http://localhost:8080`