# 🎓 TUTHelpDesk-System

## 📌 Overview

The **TUTHelpDesk-System** is a web-based application designed to streamline support services for students. It allows users to log issues, track requests, and receive assistance efficiently through a centralized help desk platform.

---

## 🚀 Features

* 📝 Submit and manage support tickets
* 🔍 Track ticket status in real-time
* 👨‍💼 Admin dashboard for managing requests
* 📊 Organized issue categorization
* 🔐 Secure user authentication and authorization

---

## 🛠️ Tech Stack

### Backend

* **Spring Boot** – RESTful API development
* **Java** – Core programming language

### Frontend

* **Angular** – Interactive user interface

### Database

* **PostgreSQL** – Relational database management

### API & Testing

* **Postman** – API testing and development

### Architecture

* **REST API** – Communication between frontend and backend

---

## 📂 Project Structure

```
TUTHelpDesk-System/
│── backend/        # Spring Boot application
│── frontend/       # Angular application
│── database/       # SQL scripts and configurations
│── docs/           # Documentation (API, diagrams, etc.)
```

---

## ⚙️ Installation & Setup

### 1. Clone the repository

```bash
git clone https://github.com/your-username/TUTHelpDesk-System.git
cd TUTHelpDesk-System
```

### 2. Backend Setup (Spring Boot)

* Open the `backend` folder in your IDE
* Configure PostgreSQL in `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/helpdesk
spring.datasource.username=your_username
spring.datasource.password=your_password
```

* Run the application

---

### 3. Frontend Setup (Angular)

```bash
cd frontend
npm install
ng serve
```

* Open: `http://localhost:4200`

---

### 4. Database Setup

* Create a PostgreSQL database named: `helpdesk`
* Run SQL scripts from the `/database` folder

---

## 🔌 API Endpoints (Examples)

| Method | Endpoint          | Description         |
| ------ | ----------------- | ------------------- |
| GET    | /api/tickets      | Get all tickets     |
| POST   | /api/tickets      | Create a new ticket |
| GET    | /api/tickets/{id} | Get ticket by ID    |
| PUT    | /api/tickets/{id} | Update ticket       |
| DELETE | /api/tickets/{id} | Delete ticket       |

---

## 🧪 Testing

* Use **Postman** to test API endpoints
* Import API collection (if available in `/docs`)

---

## 👥 Contributors

* Add your team members here

---

## 📄 License

This project is for educational purposes.

---

## 📬 Contact

For any inquiries or support, please contact the development team.
