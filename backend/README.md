# SF Guild Analytics - Backend

## Setup & Running

### 1. Prerequisites
- Java 17
- Maven
- Docker (for PostgreSQL)

### 2. Start Database
Run the following command in the root directory:
```bash
docker-compose up -d
```

### 3. Build & Run Backend
Navigate to the `backend` directory and run:
```bash
mvn spring-boot:run
```

The server will start at `http://localhost:8080`.

### 4. API Documentation
Once running, you can test the login endpoint:
- **POST** `/api/auth/login`
- **Body**: `{"username": "admin", "password": "yourpassword"}`

> [!NOTE]
> You'll need to manually insert a user with a BCrypt hashed password into the `users` table first, as a registration endpoint has not been implemented in this phase.
