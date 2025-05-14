# ⏱️ TimeToTrack

**TimeToTrack** is a reactive time-tracking application built with **pure Vert.x**, PostgreSQL, and JDBC. It's designed
to help users log their working hours per project, track productivity, and generate meaningful insights.

## 🛠️ Technologies

- Java 11
- Vert.x 4.5.14 (core, web, jdbc)
- PostgreSQL
- Maven
- JUnit 5 + Mockito
- DBeaver (optional, for DB management)

## 📂 Project Structure

```text
src/
└── main/
    └── java/com/timetotrack/timetotrack/
        ├── api/
        ├── constant/
        ├── dao/
        ├── database/
        ├── dependencyInjection
        ├── model/
        ├── service/
        └── MainVerticle
```

## ⚙️ Database Setup

Run the initialization script:

```bash
  ./init_db.sh
```

This will:

Create the database goldentimer

Create the goldentimer user

Set up all required tables: user, customer, projects, time_entry

## ▶️ Run the App

```bash
  mvn clean package
  java -jar target/timetotrack-1.0-SNAPSHOT-fat.jar
```

Access the default endpoint: ``GET http://localhost:8888/api/users``

## 🧪 Run Tests

```bash
  mvn test
```

## 👤 Author

Developed by Alan Kalbermatter, 2025\
Built with clean architecture and love.
