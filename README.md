[![CI - Build and Test Spring Boot Application](https://github.com/pr4jwal-19/book-order-sb/actions/workflows/ci.yml/badge.svg)](https://github.com/pr4jwal-19/book-order-sb/actions/workflows/ci.yml)

# 🍽️ Table Booking App

A **Spring Boot** application that supports **OAuth2 login (Google/GitHub)** and **JWT-based stateless authentication**.  
After logging in with Google or GitHub, the app issues a **JWT token**, which can be used to access protected APIs like booking tables, viewing menus, ordering, etc.

---

## 🚀 Features
- ✅ User Registration/Login with Email, Password and Google and GitHub (OAuth2)
- ✅ Stateless authentication using **JWT**
- ✅ REST APIs secured with Spring Security
- ✅ Configurable database integration (PostgreSQL/MySQL/H2)
- ✅ Role-based access control (User/Admin)
- ✅ API endpoints for booking tables, viewing menus, and placing orders
---

## 🛠️ Tech Stack
- **Backend**: Spring Boot (Web, Security, OAuth2, JWT)
- **Database**: PostgreSQL (or MySQL/H2 for testing)
- **Build Tool**: Maven
- **Auth Providers**: Google, GitHub
- **Token**: JWT (JSON Web Token)

---

## ⚙️ Setup Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/table-booking-app.git
cd table-booking-app

🧑‍💻 Author

👤 Prajwal Nakure
💼 Passionate about Java | Spring Boot | Backend Development

🌟 Future Enhancements

User dashboard with booking history

Role-based authorization (Admin/User)

Frontend integration (React/Angular)

Email & Phone verification
