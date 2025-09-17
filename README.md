[![CI - Build and Test Spring Boot Application](https://github.com/pr4jwal-19/book-order-sb/actions/workflows/ci.yml/badge.svg)](https://github.com/pr4jwal-19/book-order-sb/actions/workflows/ci.yml)

# 🍽️ Table Booking & Ordering App

A **Spring Boot** application that supports **OAuth2 login
(Google/GitHub)** and **JWT-Role-based stateless authentication**.  
After logging in with Google or normally with Email and Password, the app issues a **JWT token**,
which can be used to access protected APIs like booking tables, viewing menus, ordering, etc.
And for the admin side, it provides APIs to manage tables, menus, and orders.

---

## 🚀 Features
- ✅ User Registration/Login with Email, Password and Google (OAuth2)
- ✅ Stateless authentication using **JWT**
- ✅ REST APIs secured with Spring Security
- ✅ Configurable database integration (PostgreSQL)
- ✅ Role-based access control (GUEST/ADMIN)
- ✅ API endpoints for booking tables, viewing menus, and placing orders
---

## 🛠️ Tech Stack
- **Language**: Java 21
- **Backend**: Spring Boot (Web, Security, OAuth2, JWT, Data JPA, Validation, Lombok, Actuator, DevTools, Test)
- **Database**: PostgreSQL 
- **Build Tool**: Maven
- **Auth Providers**: Google
- **Token**: JWT (JSON Web Token)

---

## ⚙️ Setup Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/pr4jwal-19/book-order-sb.git
cd book-order-sb
```

🧑‍💻 Author

👤 Prajwal Nakure
💼 Passionate about Java | Spring Boot | Backend Development | Tech Enthusiast |
📧 prajwal.dvl.2025.19@gmail.com
🔗 [LinkedIn](https://www.linkedin.com/in/prajwal-nakure)


🌟 Future Enhancements

User dashboard with booking history

Frontend integration (React/Angular)

Email & Phone verification

Push notifications for booking confirmations

Payment gateway integration
