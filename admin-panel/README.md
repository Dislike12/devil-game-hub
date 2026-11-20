# Devil Game Hub - Web Admin Dashboard

A standalone, web-based single-page admin dashboard for Devil Game Hub.

---

## 🌟 Features & Overview
The **Devil Game Hub Admin Dashboard** is a separate web application designed for game platform managers:
- **Dashboard Overview**: Metrics displaying Total Games, Published Games, Draft Games, and Total Categories.
- **Professional Sidebar Navigation**:
  - **Overview**: High-level metrics and quick actions.
  - **All Games**: Full interactive catalog with live search, category filtering, and status filtering (*Published vs Draft*).
  - **Add New Game**: Modal form for registering 2D, 3D, and HTML5 games.
  - **Categories**: Genre management and custom category creation.
  - **Settings**: LocalStorage stats and JSON export options.
- **Local Storage Engine**: Runs completely client-side in your web browser using `localStorage`. No API keys or tokens required!
- **Pre-populated Sample Games**: Comes pre-loaded with rich sample games (*2048 Cyber, Viper Snake Cyber, Cyber Pacman, Neon Tower Stack, Laser Brick Breaker, Cyber Nitro Racer*).

---

## 🚀 How to Run Locally

### Method 1: Directly in Any Web Browser
1. Open your file explorer and navigate to:
   `DevilGameHub/admin-panel/`
2. Double-click [`index.html`](index.html) to open in Google Chrome, Microsoft Edge, Mozilla Firefox, or Safari.

### Method 2: Local Web Server (Optional)
- **VS Code**: Right-click `index.html` -> *Open with Live Server*.
- **Python**: Run `python -m http.server 8080` inside `admin-panel/` and open `http://localhost:8080`.

---

## ⚙️ How to Update Games in the Android App

1. Open the Admin Dashboard locally (`index.html`).
2. Add or Edit games, set statuses (*Published / Draft*), and configure local asset paths (*e.g., `games/snake/index.html`*).
3. Click **"📥 Export games.json"** in the top action bar to download the catalog.
4. Copy the downloaded `games.json` file into:
   `app/src/main/assets/games.json`
5. The Android app will load and display the updated games at next launch!
