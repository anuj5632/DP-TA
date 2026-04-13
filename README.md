# 🍕 Online Food Delivery System

A full-stack **food ordering** web application that pairs a **Spring Boot** REST backend with a **vanilla JavaScript** frontend. Customers browse a live menu, customize items with add-ons, place orders through a typed API, and follow a **simulated order-tracking** timeline—while the backend enforces classic **Gang of Four** design patterns for maintainable, interview-ready architecture.

---

## ✨ Features

- **Dynamic menu** loaded from the backend (`GET /api/menu`)
- **Category cards** for Pizza, Burger, and Fries with optional add-ons
- **Decorator-driven customization** (extras composed at runtime on the server)
- **Real-time order summary** in the sidebar (only selected lines)
- **Backend-authoritative pricing** and line totals returned from `POST /api/order`
- **Strategy-based payments** (e.g. UPI, card, COD) selected per request
- **REST order placement** with JSON request/response
- **Order tracking UI** with progressive status steps (client-side simulation)
- **CORS-enabled** API for local split-origin dev (frontend port ≠ backend port)

---

## 🛠 Tech Stack

| Layer | Technology |
|--------|------------|
| **Backend** | Java **17+** (tested with **21**), **Spring Boot 3**, Maven |
| **Frontend** | HTML5, CSS3, **Vanilla JavaScript** (`fetch`) |
| **API** | REST / JSON |
| **Build** | Maven (`pom.xml`) |

**Prerequisites:** Java 17+ (or 21), Maven, a modern browser, Python 3 *(optional, for static file server)*.

---

## 🏗 Architecture (text diagram)

```
┌─────────────────────────────────────────────────────────────────┐
│                        BROWSER                                   │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  web-order-ui/  (HTML + CSS + app.js)                     │  │
│  │  • Served at http://localhost:5500                        │  │
│  │  • fetch("http://localhost:8080/api/...")                 │  │
│  └───────────────────────────┬───────────────────────────────┘  │
└──────────────────────────────│────────────────────────────────┘
                                 │ HTTP / JSON
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│              SPRING BOOT — http://localhost:8080               │
│  ┌─────────────┐   ┌──────────────┐   ┌─────────────────────┐ │
│  │ REST        │   │ OrderService │   │ OrderController     │ │
│  │ Controllers │──▶│ (flow)       │──▶│ + Services          │ │
│  └─────────────┘   └──────────────┘   └──────────┬──────────┘ │
│                                                     │           │
│     Patterns: Singleton │ Strategy │ Decorator ◀──┘           │
└─────────────────────────────────────────────────────────────────┘
```

**Flow:** UI loads menu → user selects items & extras → UI `POST`s payload → backend builds food (factory), wraps **decorators**, totals order, runs **payment strategy**, returns **order id + lines + total** → UI shows confirmation & tracking.

---

## 📁 Folder Structure

```
DP-TA/
├── pom.xml                          # Maven / Spring Boot project
├── start-backend-frontend.sh        # Optional: run UI + API together
├── README.md                        # This file
├── src/
│   ├── main/resources/
│   │   └── application.properties   # Server port, app name
│   └── com/fooddelivery/
│       ├── main/
│       │   └── FoodDeliveryApplication.java
│       ├── api/                   # REST controllers (+ CORS)
│       ├── controllers/           # Domain order orchestration
│       ├── dto/                   # Request / response DTOs
│       ├── models/                # Order, menu, status, etc.
│       ├── patterns/
│       │   ├── creational/        # OrderManager (Singleton)
│       │   ├── structural/        # FoodItem + Decorators
│       │   └── behavioral/        # PaymentStrategy + implementations
│       └── services/              # Menu, factory, customization, payment, …
└── web-order-ui/
    ├── index.html
    ├── styles.css
    └── app.js                     # fetch() to backend BASE_URL
```

---

## 🚀 Installation & Run

### 1. Clone the repository

```bash
git clone https://github.com/anuj5632/DP-TA
cd DP-TA
```

### 2. Run the backend (Spring Boot)

```bash
mvn spring-boot:run
```

Wait until the log shows the embedded server is up (default: **http://localhost:8080**).

**Sanity check:** open [http://localhost:8080/api/menu](http://localhost:8080/api/menu) — you should see JSON.

### 3. Run the frontend (static server)

From the project root:

```bash
python3 -m http.server 5500 --directory web-order-ui
```

Then open **http://localhost:5500** (or `index.html`) in your browser.

> The UI calls **`http://localhost:8080`** for APIs. Keep the backend running while using the site.

### Optional: one script (macOS / Linux)

```bash
chmod +x ./start-backend-frontend.sh
./start-backend-frontend.sh
```

Requires **Java**, **Maven**, and **Python 3**. Press `Ctrl+C` to stop both processes.

---

## 🔌 API Endpoints

### `GET /api/menu`

Returns all menu rows (code, type, display name, base price).

**Example:** `GET http://localhost:8080/api/menu`

---

### `POST /api/order`

Creates an order: resolves each line by **type + name**, applies **extras** as server-side customization, runs **payment**, returns totals and line descriptions.

**Example request body:**

```json
{
  "customerName": "Alex",
  "items": [
    {
      "type": "PIZZA",
      "name": "Farmhouse Pizza",
      "extras": ["Corn", "Olives"]
    }
  ],
  "paymentMethod": "UPI",
  "paymentReference": "alex@upi"
}
```

**Example response (shape):**

```json
{
  "orderId": "3f50b0ce-34ae-4bb9-ae42-5bd8088d335d",
  "items": [
    {
      "description": "Pizza + Corn + Olives",
      "quantity": 1,
      "lineTotal": 290.0
    }
  ],
  "totalAmount": 290.0,
  "status": "PLACED"
}
```

**Supported `paymentMethod` values (examples):** `UPI`, `CREDIT_CARD`, `COD` (see `PaymentStrategyFactory` in code for exact aliases).

---

## 🧩 Design Patterns (short)

| Pattern | Where / Why |
|---------|----------------|
| **Singleton** | `OrderManager` — single registry of active orders for the app lifecycle. |
| **Strategy** | `PaymentStrategy` + concrete strategies (UPI, card, COD) — payment algorithm chosen at checkout without changing order code. |
| **Decorator** | `FoodItem` + `FoodItemDecorator` / toppings / sauces / drinks — compose price & description at runtime from `CustomizationService`. |

---

## 📸 Screenshots

> Add your own images under `docs/screenshots/` (or similar) and link them here.

| Screen | Placeholder |
|--------|-------------|
| Menu & order panel | ![Menu](docs/screenshots/menu.png) *(Add screenshot here)* |
| Order confirmation & tracking | ![Tracking](docs/screenshots/tracking.png) *(Add screenshot here)* |

---

## 🔮 Future Improvements

- JWT / session-based auth and user profiles  
- Persist orders in a database (JPA / PostgreSQL)  
- Admin dashboard for menu CRUD  
- WebSocket or SSE for **live** order status from kitchen/delivery  
- Containerized deploy (Docker Compose: UI + API + DB)  
- Automated tests (JUnit + REST Assured; frontend smoke tests)  
- i18n and accessibility (ARIA) polish  

---

## 👤 Author

**Akshat, Anuj, Devesh**  

 

---

<p align="center">
  <b>Built with ☕ Java, 🌱 Spring Boot, and plain JS — patterns first, APIs that scale.</b>
</p>
