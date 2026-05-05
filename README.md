Here’s an **updated version of your README** with **Student and Admin roles clearly defined and integrated**, without changing your existing structure too much. You can copy‑paste this directly into your `README.md`.

***

# 🎓 TUTHelpDesk-System

## 📌 Overview

The **TUTHelpDesk-System** is a web-based application designed to streamline support services for students. It provides a centralized help desk platform where **students** can log and track issues, while **administrators** can manage, assign, and resolve support requests efficiently.

***

## 🚀 Features

### 👩‍🎓 Student Features

*   📝 Submit support tickets
*   🔍 Track ticket status in real time
*   📂 View ticket history
*   ✏️ Update or add comments to tickets
*   🔐 Secure login and logout

### 👨‍💼 Admin Features

*   📊 View all submitted tickets
*   🗂️ Categorize and prioritize issues
*   ✅ Update ticket statuses (Open, In Progress, Resolved)
*   👥 Manage student requests
*   🔐 Secure admin authentication and authorization

***

## 🧑‍🤝‍🧑 User Roles

### 🎓 Student

Students can:

*   Register and log in to the system
*   Create new help desk tickets
*   View and track their own tickets
*   Receive updates on ticket progress
*   Log out securely

### 🛠️ Administrator

Administrators can:

*   Log in through the admin dashboard
*   View and manage all student tickets
*   Assign priorities and update ticket status
*   Monitor overall system activity
*   Log out securely

***

## 🛠️ Tech Stack

### Backend

*   **Spring Boot** – RESTful API development
*   **Java** – Core programming language

### Frontend

*   **Angular** – Interactive user interface

### Database

*   **PostgreSQL** – Relational database management

### API & Testing

*   **Postman** – API testing and development

### Architecture

*   **REST API** – Communication between frontend and backend

***

## 📂 Project Structure

    TUTHelpDesk-System/
    │── backend/        # Spring Boot application
    │── frontend/       # Angular application
    │── database/       # SQL scripts and configurations
    │── docs/           # Documentation (API, diagrams, etc.)

***

## ⚙️ Installation & Setup

### 1. Clone the repository

```bash
git clone https://github.com/your-username/TUTHelpDesk-System.git
cd TUTHelpDesk-System
```

***

### 2. Backend Setup (Spring Boot)

*   Open the `backend` folder in your IDE
*   Configure PostgreSQL in `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/helpdesk
spring.datasource.username=your_username
spring.datasource.password=your_password
```

*   Run the application

***

### 3. Frontend Setup (Angular)

```bash
cd frontend
npm install
ng serve
```

*   Open: `http://localhost:4200`

***

### 4. Database Setup

*   Create a PostgreSQL database named: `helpdesk`
*   Run SQL scripts from the `/database` folder

***

## 🔌 API Endpoints (Examples)

| Method | Endpoint          | Description                   |
| -----: | ----------------- | ----------------------------- |
|    GET | /api/tickets      | Get all tickets (Admin)       |
|   POST | /api/tickets      | Create a new ticket (Student) |
|    GET | /api/tickets/{id} | Get ticket by ID              |
|    PUT | /api/tickets/{id} | Update ticket                 |
| DELETE | /api/tickets/{id} | Delete ticket (Admin)         |

***

## 🧪 Testing

*   Use **Postman** to test API endpoints
*   Import API collection (if available in `/docs`)

***

## 👥 Contributors

*   Add your team members here

***

## 📄 License

This project is for educational purposes.

***

## 📬 Contact

For any inquiries or support, please contact the development team.

