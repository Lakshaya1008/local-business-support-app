# Local Business Support App

A full-stack Spring Boot application to support local businesses with user authentication, product management, order processing, payments, and more.

## 🚀 Features
- User registration, login, and JWT authentication
- Role-based access (Admin, Seller, User)
- Product catalog: add, update, delete, search products
- Order management: place, view, and manage orders
- Payment integration (mocked)
- Email verification (configurable)
- RESTful API and web interface
- Swagger UI for API documentation
- Health check endpoint

## 🛠️ Tech Stack
- Java 17+
- Spring Boot 3.5+
- Maven
- MySQL 8.0+
- Thymeleaf (for web UI)
- Spring Security (JWT)

## 📦 Project Structure
```
local-business-support-app/
├── src/main/java/com/example/localbusiness/
│   ├── controller/      # Web & API controllers
│   ├── service/         # Business logic
│   ├── model/           # JPA entities
│   ├── repository/      # Spring Data JPA repos
│   ├── config/          # Security & app config
│   └── ...
├── src/main/resources/
│   ├── application.yml  # Main config
│   └── templates/       # Thymeleaf HTML
├── src/test/java/       # Unit & integration tests
├── pom.xml              # Maven build file
└── README.md
```

## ⚙️ Setup Instructions

### 1. Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+

### 2. Database Setup
1. Start MySQL server
2. Create the database:
   ```sql
   CREATE DATABASE localbusiness;
   ```
3. Update `src/main/resources/application.yml` if your DB user/password is different (default: `root`/`root@1008`).

### 3. Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

- App runs at: [http://localhost:8085](http://localhost:8085)
- Swagger UI: [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html)

### 4. Test
```bash
mvn test
```

## 🧑‍💻 Usage
- Register a new user (choose role: User, Seller, Admin)
- Login with your credentials
- Explore dashboard, add/view products, place orders, etc.
- Use Swagger UI for API testing

## 🔗 API Endpoints (Sample)
- `POST /api/auth/register` — Register new user
- `POST /api/auth/login` — Login and get JWT
- `GET /api/products` — List all products
- `GET /api/products/{id}` — Product details
- `POST /api/orders` — Place order
- `GET /health` — Health check

## 📝 Contribution Guidelines
1. Fork this repo
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes
4. Push to your fork and open a Pull Request

## 📄 License
MIT 