#  AnnaSetu - Food Redistribution System

AnnaSetu is a Java based application designed to reduce food waste by connecting restaurants with NGOs and individuals in need. The system enables efficient food distribution through a structured workflow and user friendly interface.

---

##  Features

*  User Authentication (Restaurant / NGO)
*  Restaurant Module

* Add and manage food listings
* Track available surplus food
* NGO Module

* Browse available food
* Accept and manage food requests
* Food Redistribution Workflow
* Desktop UI built using Java Swing

---

## 🛠️ Tech Stack

* **Java** (Core + OOP Concepts)
* **Java Swing** (GUI Development)
* **JDBC** (Database Connectivity)
* **Oracle Database (XE)**
* **MVC Architecture**

---

##  Project Structure

```
AnnaSetu/
├── src/
│   ├── db/         # Database connection & setup
│   ├── models/     # Data models
│   ├── services/   # Business logic
│   └── ui/         # User interface (Swing)
├── lib/
│   └── ojdbc17.jar # Oracle JDBC driver
├── .gitignore
└── README.md
```

---

## ⚙️ Setup Instructions

### 1. Clone the repository

```
git clone https://github.com/your-username/AnnaSetu-Food-Redistribution-System.git
cd AnnaSetu-Food-Redistribution-System
```

### 2. Open the project

* Open in **VS Code** or **IntelliJ IDEA**
* Ensure Java (JDK 8 or above) is installed

### 3. Run the application

* Navigate to `src/Main.java`
* Run the file

---

##  Database Setup (Optional)

The application can run without a database (for UI demo), but full functionality requires Oracle DB.

1. Install **Oracle XE**
2. Start Oracle services
3. Update credentials in:

   ```
   db/DatabaseConnection.java
   ```
4. Run:

   ```
   DatabaseSetup.java
   ```

---

##  Note

* If the database is not configured, the app will show a connection error popup.
* This does **not affect the UI demonstration** of the system.

---

##  Objective

To minimize food wastage by creating a bridge between food providers (restaurants) and receivers (NGOs), ensuring surplus food reaches those in need efficiently.

---

##  Future Enhancements

*  Web / Mobile version
*  Location-based matching
*  Notification system
*  Analytics dashboard

---

##  Author

* Purabhi Patil *

---

##  If you like this project

Give it a ⭐ on GitHub and feel free to contribute!

---
