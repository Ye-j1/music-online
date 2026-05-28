# MusicOnline — Vinyl Marketplace

A dynamic, data-driven web application for buying and selling vinyl records (albums, EPs, singles).

## Tech Stack

| Layer    | Technology                                     |
|----------|------------------------------------------------|
| Backend  | Spring Boot 3.2.5 · Spring Security · JWT · JPA/Hibernate |
| Frontend | Vanilla HTML + CSS + JavaScript (no framework) |
| Database | MySQL 8.0                                      |
| Auth     | BCrypt + JWT (HS256, 24-hour token)            |

---

## Project Structure

```
musiconline/
├── backend/                        Spring Boot project
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/musiconline/
│       │   ├── MusicOnlineApplication.java
│       │   ├── bootstrap/DataInitializer.java   Auto-seeds DB on first run
│       │   ├── config/
│       │   │   ├── JwtProperties.java
│       │   │   └── SecurityConfig.java
│       │   ├── domain/
│       │   │   ├── AppUser.java
│       │   │   ├── AuditLogEntry.java
│       │   │   ├── Role.java           (USER / RETAILER / ADMIN)
│       │   │   ├── Vinyl.java
│       │   │   └── VinylType.java      (ALBUM / EP / SINGLE)
│       │   ├── repository/
│       │   ├── security/
│       │   │   ├── AppUserDetailsService.java
│       │   │   ├── JwtAuthenticationFilter.java
│       │   │   └── JwtService.java
│       │   ├── service/
│       │   │   ├── AuditService.java
│       │   │   ├── AuthService.java
│       │   │   └── VinylService.java
│       │   └── web/
│       │       ├── AdminApiController.java   /api/admin/**  (ADMIN only)
│       │       ├── AuthController.java        /api/auth/**
│       │       ├── ProfileApiController.java  /api/me
│       │       ├── PublicController.java      /api/public/**
│       │       ├── RestExceptionHandler.java
│       │       ├── RootController.java
│       │       ├── VinylApiController.java    /api/vinyls/**
│       │       └── dto/
│       └── resources/
│           └── application.yml
├── frontend/
│   ├── index.html              Redirect entry point
│   ├── css/
│   │   ├── variables.css       CSS custom properties (colour, spacing, etc.)
│   │   ├── base.css            Reset + typography
│   │   ├── components.css      Buttons, cards, badges, modals, toasts…
│   │   ├── layout.css          Sidebar + main content layout
│   │   └── login.css           Auth page styles
│   ├── js/
│   │   ├── api-base.js         moApiBaseUrl() + moApiFetch() helpers
│   │   ├── auth.js             Login / register form handlers
│   │   ├── app.js              Shared utilities (toast, vinyl card, guards)
│   │   └── three-bg.js         Three.js particle background
│   └── pages/
│       ├── home.html           Public landing page
│       ├── login.html          Sign-in + register
│       ├── search.html         Public vinyl search
│       ├── vinyl-detail.html   Vinyl detail (public)
│       ├── dashboard.html      User home (requires login)
│       ├── my-vinyls.html      My listings — add / edit / delete
│       ├── add-vinyl.html      Add new vinyl form
│       ├── profile.html        User profile
│       └── admin.html          Admin panel (ADMIN only)
└── database/
    └── init_mysql.sql          Full SQL schema + seed data
```

---

## Quick Start

### 1. Prerequisites

- Java 17+
- Maven 3.9+
- MySQL 8.0 running on `localhost:3306`

### 2. Database

Either let Spring Boot auto-create the schema on first run (DDL `update`), or run:

```sql
SOURCE database/init_mysql.sql;
```

### 3. Configure credentials

Edit `backend/src/main/resources/application.yml` — update `username` and `password` to match your MySQL setup.

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/music_online?createDatabaseIfNotExist=true&...
    username: root
    password: YOUR_PASSWORD
```

### 4. Run

```bash
cd backend
mvn spring-boot:run
```

Open **http://localhost:8080** in your browser.

---

## Demo Accounts

| Role     | Email                    | Password   |
|----------|--------------------------|------------|
| Admin    | admin@musiconline.com    | password   |
| Admin    | admin2@musiconline.com   | password   |
| User     | alice@example.com        | password   |
| Retailer | bob@example.com          | password   |
| User     | carol@example.com        | password   |
| Retailer | dave@example.com         | password   |

---

## API Endpoints

### Public (no authentication)

| Method | Path                     | Description                              |
|--------|--------------------------|------------------------------------------|
| GET    | /api/public/vinyls       | Search vinyls (?q=&type=&page=&size=)    |
| GET    | /api/public/vinyls/{id}  | Get vinyl by ID                          |

### Auth

| Method | Path                | Description         |
|--------|---------------------|---------------------|
| POST   | /api/auth/login     | Login → JWT token   |
| POST   | /api/auth/register  | Register new user   |

### Authenticated users

| Method | Path               | Description              |
|--------|--------------------|--------------------------|
| GET    | /api/me            | Current user profile     |
| GET    | /api/vinyls/mine   | My vinyl listings        |
| POST   | /api/vinyls        | Add vinyl listing        |
| PUT    | /api/vinyls/{id}   | Update listing           |
| DELETE | /api/vinyls/{id}   | Delete listing           |

### Admin only

| Method | Path                    | Description             |
|--------|-------------------------|-------------------------|
| GET    | /api/admin/vinyls       | All vinyls (paged)      |
| DELETE | /api/admin/vinyls/{id}  | Delete any vinyl        |
| GET    | /api/admin/users        | All users               |
| GET    | /api/admin/audit        | Audit log               |

---

## Features Implemented

- [x] New user registration (USER or RETAILER role)
- [x] User login with JWT session management
- [x] Administrator login (ADMIN role)
- [x] Search by artist, album, EP or single title
- [x] Vinyl detail view (release date, price, condition, genre, label…)
- [x] Registered users can add, edit and delete their own listings
- [x] Admin panel: monitor all users, vinyls, and audit log
- [x] Security: BCrypt password hashing, JWT auth, input validation, SQL injection protection via JPA
- [x] Responsive design (mobile + desktop)
- [x] 15+ seed vinyl records; 2+ admin accounts
