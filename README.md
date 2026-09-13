# 🚀 ClassAI

> **A modern school-management Android application built with Android, FastAPI, and MySQL — designed to bring administration, employees, authentication, profiles, and payments into one connected system.**

ClassAI is an internship project focused on building a **real-world, database-driven school management application** with a clean Android interface and a centralized FastAPI backend.

The application follows a simple principle:

**📱 Android UI → ⚡ FastAPI Backend → 🗄️ MySQL Database**

No fake local data. No business logic hidden inside the UI. The backend remains the authority for authentication, account status, school relationships, and database operations.

---

## ✨ What ClassAI Can Do

### 🔐 Secure Authentication

- Single login flow using:
  - User ID
  - Mobile
  - Password
- No manual role selection.
- Backend determines the user's role.
- Separate handling for Administrators and Employees.
- Inactive accounts cannot log in.
- Employees also cannot log in when their school is inactive.
- Passwords are securely hashed on the backend.

---

### 👨‍💼 Administrator Management

Administrators can be managed directly through the application.

**Add Administrator**
- Full Name
- Mobile
- User ID
- Password

**Administrator Directory**
- Full Name
- User ID
- Mobile
- Active / Inactive status

Administrators are backed by the real MySQL database rather than dummy data.

The system also enforces **global User ID uniqueness**, preventing the same User ID from being used across administrator and employee accounts.

---

### 👨‍🏫 Employee Management

The Employee section provides complete database-backed employee management.

**Employee Directory**

Each employee displays:

- Full Name
- User ID
- Mobile
- School
- Active / Inactive status

**Add Employee**
- Full Name
- Mobile
- User ID
- Password
- School

**Edit Employee**
- Full Name
- Mobile
- User ID
- School
- Optional new password

Changes are sent to the backend and only reflected in the UI after successful database operations.

---

### 🏫 School & Employee Status Control

School status directly controls the status of its employees.

When a school is **deactivated**:

```text
School → Inactive
        ↓
All employees → Inactive
```

When the school is activated again:

```text
School → Active
        ↓
Employees → Active
```

This relationship is enforced by the backend rather than relying only on Android UI state.

---

### 👤 Real User Profiles

The profile screen uses real backend data.

Employees can see:

- Full Name
- User ID
- Mobile
- School
- Role
- Account status

Profile information reflects changes made through the employee-management system.

Administrator profiles are also retrieved from the backend.

---

### 💳 Payment System

ClassAI includes an integrated payment flow backed by the application's existing architecture.

The payment flow follows:

```text
MySQL
  ↓
FastAPI
  ↓
Retrofit
  ↓
Repository
  ↓
PaymentViewModel
  ↓
Receipt
```

The backend remains responsible for authoritative financial/database values.

> **Note:** Payment history is intentionally not part of the current application.

---

## 🧠 Architecture

ClassAI uses a layered architecture so that the Android application does not directly communicate with the database.

```text
┌───────────────────────────┐
│       Android App         │
│                           │
│  Jetpack Compose UI       │
│  ViewModels               │
│  Repository Layer         │
│  Retrofit                 │
└─────────────┬─────────────┘
              │
              │ HTTP / REST
              ▼
┌───────────────────────────┐
│       FastAPI Backend     │
│                           │
│  Authentication           │
│  Validation               │
│  Business Logic           │
│  Account Management       │
│  Payment Operations       │
└─────────────┬─────────────┘
              │
              │ SQL
              ▼
┌───────────────────────────┐
│       MySQL Database      │
│                           │
│  user_admin               │
│  user_accountant          │
│  school_master            │
│  Payment-related data     │
└───────────────────────────┘
```

### 🔄 Example: Employee Login

```text
User enters credentials
        ↓
Android Login Screen
        ↓
Retrofit API request
        ↓
FastAPI /login
        ↓
MySQL account lookup
        ↓
Check credentials
        ↓
Check employee status
        ↓
Check school status
        ↓
Return authenticated role
        ↓
Android routes user
```

---

## 🛠️ Tech Stack

### 📱 Android
- Kotlin
- Jetpack Compose
- Retrofit
- ViewModel
- Repository pattern

### ⚡ Backend
- Python
- FastAPI
- REST APIs
- PBKDF2 password hashing

### 🗄️ Database
- MySQL
- Cloud-hosted database

### ☁️ Deployment
- FastAPI backend deployed on Render
- GitHub repository for source control

---

## 🔒 Security & Validation

ClassAI puts important validation on the backend rather than trusting the Android application.

### User ID Security

User IDs are globally unique across:

```text
user_admin
      +
user_accountant
```

This prevents an administrator and employee from accidentally sharing the same User ID.

### Account Status

The backend validates account status before allowing access.

### School Status

Employee authentication also considers the status of the employee's school.

### Protected SuperAdmin

The protected `testadmin` SuperAdmin account cannot be:

- Recreated
- Modified
- Deactivated through normal administrator management

It is also hidden from the regular administrator directory.

---

## 📂 Core Database Structure

### `user_admin`

Stores administrator accounts.

```text
id
full_name
mobile
user_id
password
is_active
created_by
created_ts
```

### `user_accountant`

Stores employee accounts.

```text
id
school_id
full_name
mobile
user_id
password
is_active
created_by
created_ts
```

### `school_master`

Stores school information and school status.

```text
id
school_name
is_active
...
```

The `school_id` relationship connects employees to their respective schools.

---

## 🌐 Backend

The production FastAPI backend is deployed at:

**https://classai-backend-a69s.onrender.com**

The backend provides the API layer used by the Android application for:

- Authentication
- Administrator management
- Employee management
- School status management
- Profile information
- Payment operations

---

## 📱 Application Flow

```text
                    ┌──────────────┐
                    │    Login     │
                    └──────┬───────┘
                           │
                 ┌─────────┴─────────┐
                 ▼                   ▼
          ┌─────────────┐     ┌─────────────┐
          │    Admin    │     │   Employee  │
          └──────┬──────┘     └──────┬──────┘
                 │                   │
        ┌────────┴────────┐   ┌──────┴─────────┐
        ▼                 ▼   ▼                ▼
 Administrators       Payments Employees      Profile
                          │
                          ▼
                       Profile
```

---

## 🧪 Validation & Testing

The application has been tested across the major functional flows, including:

- ✅ Administrator creation
- ✅ Administrator directory
- ✅ Administrator activation/deactivation
- ✅ Protected SuperAdmin
- ✅ Employee creation
- ✅ Employee directory
- ✅ Employee activation/deactivation
- ✅ Employee editing
- ✅ Employee password changes
- ✅ Duplicate User ID prevention
- ✅ Admin/Employee User ID collision prevention
- ✅ School activation/deactivation
- ✅ School → Employee status cascade
- ✅ Employee login restrictions
- ✅ Real profile information
- ✅ Profile updates after employee edits
- ✅ Existing payment flow
- ✅ Android build

### 🟢 Current Status

**Core application: Complete and operational.**

Remaining work is primarily limited to minor UI/polish changes and project-specific finishing touches.

---

## 📁 Project Structure

At a high level:

```text
Internship-Project/
│
├── app/
│   └── src/
│       └── main/
│           └── java/
│               └── com/
│                   └── internship/
│                       └── classai/
│                           ├── backend/
│                           │   └── good/
│                           │       └── app/
│                           │           └── main.py
│                           │
│                           ├── data/
│                           │   └── model/
│                           │
│                           ├── repository/
│                           │
│                           ├── ui/
│                           │
│                           └── ...
│
└── README.md
```

---

## 🚀 Running the Project

### Android

1. Clone the repository.
2. Open the project in Android Studio.
3. Allow Gradle dependencies to sync.
4. Configure the required Android environment.
5. Build the project.
6. Run the application on an emulator or Android device.

### Backend

The backend is a FastAPI application.

The production backend is already deployed on Render, while the source code is maintained inside the project repository.

---

## 🔗 Repository

**GitHub:**  
https://github.com/Shopnojo/Internship-Project.git

---

## 🎯 Project Goal

ClassAI was developed as an internship project to demonstrate how a modern mobile application can connect a polished Android interface with a real backend and relational database.

The project focuses on:

> **Clean UI + Real Database + Backend Validation + Secure Authentication + Practical Business Logic**

Rather than keeping important information inside the Android application, ClassAI uses the backend and database as the source of truth.

---

## 🏆 Built With

**Kotlin • Jetpack Compose • FastAPI • Python • Retrofit • MySQL • Render**

---

### ⭐ ClassAI

**A school-management system built from the UI all the way down to the database.**

> *One app. One backend. One source of truth.* 🚀
