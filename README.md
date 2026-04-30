# HoldIt Store – Product Catalog

A **zero-dependency** standalone Java web app powered by an embedded Jetty server.  
No Maven, no Docker, no build tools — just Java and a one-time JAR download.

---

## Prerequisites (Local)

| Tool | Minimum Version |
|------|----------------|
| Java (JDK or JRE) | 8+ |
| javac (JDK compiler) | 8+ |

```
java -version
javac -version
```

---

## Quick Start (Windows – Local)

### 1. Download dependencies (one-time only)
```powershell
powershell -ExecutionPolicy Bypass -File setup.ps1
```

### 2. Compile & Run
```bat
run.bat
```
Open **http://localhost:8080** in your browser.

---

## Deploying to Render (Free Tier)

### Step 1 — Push to GitHub

Make sure all these files are committed and pushed:

```
java-kumar/
├── ProductCatalog.java
├── setup.sh          ← Linux dependency downloader (required by Render)
├── setup.ps1         ← Windows only (not used by Render)
└── run.bat           ← Windows only (not used by Render)
```

```bash
git init
git add ProductCatalog.java setup.sh setup.ps1 run.bat README.md
git commit -m "Initial commit"
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO.git
git push -u origin main
```

> ⚠️ Do **NOT** commit the `lib/` or `out/` folders — Render builds them fresh.

Add a `.gitignore`:
```
lib/
out/
*.class
```

---

### Step 2 — Create a Web Service on Render

1. Go to **[https://render.com](https://render.com)** → Sign in (free account)
2. Click **"New +"** → **"Web Service"**
3. Connect your GitHub account and select your repository

---

### Step 3 — Configure the Web Service

Fill in the following fields:

| Field | Value |
|-------|-------|
| **Name** | `holdit-store` (or any name) |
| **Region** | Singapore (closest to India) or any |
| **Branch** | `main` |
| **Runtime** | `Docker` → change to **`Native`** |
| **Build Command** | `bash setup.sh && mkdir -p out && javac --release 8 -cp "lib/*" ProductCatalog.java -d out` |
| **Start Command** | `java -cp "out:lib/*" ProductCatalog` |
| **Instance Type** | Free |

> ⚠️ On Linux the classpath separator is `:` not `;`. The start command above uses `:` — this is correct for Render.

---

### Step 4 — Environment Variables

Render automatically injects `PORT`. No extra config needed.

Your `main()` already reads it:
```java
int port = System.getenv("PORT") != null
    ? Integer.parseInt(System.getenv("PORT"))
    : 8080;
```

You can optionally add it manually in the Render dashboard → **Environment** tab:

| Key | Value |
|-----|-------|
| `PORT` | `10000` |

---

### Step 5 — Deploy

Click **"Create Web Service"**. Render will:

1. Clone your repo
2. Run the **Build Command** → downloads JARs → compiles `.java`
3. Run the **Start Command** → starts Jetty
4. Assign you a URL like `https://holdit-store.onrender.com`

✅ Your app is live!

---

## API Endpoints

| Endpoint | Description |
|----------|-------------|
| `GET /` | Product catalog UI |
| `GET /api/products` | All 13 products (JSON) |
| `GET /api/products?sort=price` | Sorted by price ↑ |
| `GET /api/products?sort=name` | Sorted A → Z |
| `GET /api/products?category=Electronics` | Filter by category |

---

## Project Structure

```
java-kumar/
├── ProductCatalog.java   ← Server + UI + REST API (single file)
├── run.bat               ← Windows: compile + run
├── setup.ps1             ← Windows: download JARs
├── setup.sh              ← Linux/Render: download JARs
├── README.md
├── .gitignore
├── lib/                  ← Created by setup script (gitignored)
└── out/                  ← Created at compile time (gitignored)
```

---

## Free Tier Notes

- Render free tier **spins down** after 15 minutes of inactivity
- First request after spin-down takes ~30 seconds (cold start)
- To keep it awake, use [UptimeRobot](https://uptimerobot.com) to ping your URL every 14 minutes
