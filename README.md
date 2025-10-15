This will be our shared repository

# Eco Route Planner

A sustainable travel app built with:
- Frontend: React.js + Tailwind
- Backend: Spring Boot (Java)
- Database: MySQL
- APIs: Google Maps / Directions / Distance Matrix

## Folder Structure
- `/frontend` → React source
- `/backend` → Java backend
- `/database` → SQL schema hosted on Railway for easy access 

## How to Run
# 🌱 EcoRoute Planner – VS Code Setup Guide (Railway Database Version)

This guide provides setup instructions for running and editing the **EcoRoute Planner** project in **Visual Studio Code**, connected to the shared **Railway MySQL database**.

---

## 1. Prerequisites

- Windows 10/11 (64-bit) with administrator access  
- Internet connection  
- [Git](https://git-scm.com/downloads) installed  

---

## 2. Install Java Development Kit (JDK 17) and Maven

### JDK
Open PowerShell **as Administrator**:
```bash
winget install Oracle.JDK.17
java -version
````

### Maven

Install via **Chocolatey**:

```bash
Set-ExecutionPolicy Bypass -Scope Process -Force
iwr https://community.chocolatey.org/install.ps1 -UseBasicParsing | iex
choco install maven -y
mvn -v
```

## 3. Install Visual Studio Code and Extensions

1. Download from [https://code.visualstudio.com](https://code.visualstudio.com)
2. Open VS Code → Extensions tab → install:

   * **Extension Pack for Java** (Microsoft)
   * **Maven for Java**
   * **SQLTools**
   * **SQLTools MySQL/MariaDB Driver**
   * **Prettier** (optional)

---

## 4. Clone the Project Repository

```bash
git clone https://github.com/BorseHaloney/EcoRoutePlanner.git
cd EcoRoutePlanner
```

Then open this folder in **VS Code**.

---

## 5. Backend Setup (Spring Boot)

The backend is already configured for the shared Railway database.

**Run the backend:**

```bash
cd backend
mvn spring-boot:run
```

Runs on [http://localhost:8080](http://localhost:8080)

---

## 6. Access the Railway Database via SQLTools

1. In SQL tools, connect the Railway SQL database which should already be configured.
---

## 7. Frontend Setup (React)

1. Install [Node.js LTS](https://nodejs.org)
2. In terminal:

   ```bash
   cd (insert your path to EcoRoutePlanner folder)
   npm install
   ```

4. Start the frontend:

   ```bash
   npm start
   ```

   Opens [http://localhost:3000](http://localhost:3000)

---

## 8. Verification Checklist

✅ Visit [http://localhost:3000](http://localhost:3000) → Homepage loads
✅ Backend logs confirm Railway MySQL connection
✅ SQLTools shows live database content
✅ Both backend and frontend run concurrently

---

### Notes for Contributors

* Always create a new branch before committing:

  ```bash
  git checkout -b feature-yourname
  ```
* Push changes:

  ```bash
  git add .
  git commit -m "Implemented new feature"
  git push origin feature-yourname
  ```
* Submit a pull request on GitHub for review.


```

