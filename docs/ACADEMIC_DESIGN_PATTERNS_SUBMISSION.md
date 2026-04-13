# Online Food Delivery System — Design Patterns (Academic Submission Content)

*Formal sections for inclusion in a course assessment document (e.g., Teacher’s Assessment template). Copy sections below into your word processor or PDF.*

---

## 1. List of Design Patterns Used

| Sr. No. | Category     | Pattern Name | Purpose |
|--------:|--------------|--------------|---------|
| 1       | Creational   | Singleton    | Ensures a single global `OrderManager` instance for registering and retrieving active orders, avoiding duplicate registries and providing a consistent coordination point across the application. |
| 2       | Structural   | Decorator    | Composes runtime food customization (e.g., toppings, sauces) by wrapping a `FoodItem` with decorator objects that extend cost and description without modifying base component classes. |
| 3       | Behavioral   | Strategy     | Encapsulates interchangeable payment algorithms (`PaymentStrategy` implementations) so checkout can delegate to the selected method (e.g., UPI, card, cash on delivery) without conditional branching throughout the order pipeline. |

---

## 2. Application Abstract

The **Online Food Delivery System** is a full-stack software artefact consisting of a **Spring Boot** REST backend and a **standards-based web client** (HTML, CSS, and JavaScript). The system allows users to browse a menu, choose food categories such as pizza and burgers, attach textual add-ons, and submit an order through a **JSON REST API**. On the server, menu definitions are resolved into concrete food components; **Decorator** objects wrap the base item to reflect add-on pricing and human-readable descriptions dynamically. Checkout delegates payment to a **Strategy** implementation chosen at runtime, preserving separation between order orchestration and payment mechanics. A **Singleton** order manager maintains one shared registry of active orders for consistency across services and controllers. The browser client retrieves menu data and posts order payloads using `fetch`, displays an order summary aligned with server responses, and presents a post-checkout tracking sequence for demonstration. Collectively, the selected patterns illustrate disciplined object-oriented design: centralized lifecycle management (Singleton), flexible composition of behaviour (Decorator), and polymorphic variation of algorithms (Strategy), integrated with a modern HTTP API suitable for coursework evaluation.

*(Word count: approximately 190 words.)*

---

## 3. Representative Code Snippets

### 3.1 Singleton — `OrderManager` (excerpt)

Thread-safe lazy initialization and global access:

```java
public class OrderManager {
    private static volatile OrderManager instance;
    private static final Object LOCK = new Object();

    private OrderManager() {
        this.orders = new ConcurrentHashMap<>();
    }

    public static OrderManager getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new OrderManager();
                }
            }
        }
        return instance;
    }

    public void addOrder(Order order) {
        orders.put(order.getOrderId(), order);
    }
}
```

### 3.2 Decorator — `ToppingDecorator` (excerpt)

Extends wrapped `FoodItem` cost and description:

```java
public class ToppingDecorator extends FoodItemDecorator {
    private final String toppingName;
    private final double toppingCost;

    public ToppingDecorator(FoodItem foodItem, String toppingName, double toppingCost) {
        super(foodItem);
        this.toppingName = toppingName;
        this.toppingCost = toppingCost;
    }

    @Override
    public double getCost() {
        return super.getCost() + toppingCost;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + " + toppingName;
    }
}
```

### 3.3 Strategy — `PaymentStrategy` interface and concrete strategy (excerpt)

**Interface:**

```java
public interface PaymentStrategy {
    String getPaymentMethod();
    PaymentResult pay(double amount);
}
```

**Concrete strategy (UPI):**

```java
public class UpiPaymentStrategy implements PaymentStrategy {
    private final String upiId;

    @Override
    public String getPaymentMethod() {
        return "UPI";
    }

    @Override
    public PaymentResult pay(double amount) {
        return new PaymentResult(true,
                String.format("UPI payment of Rs. %.2f successful using UPI ID: %s", amount, upiId));
    }
}
```

At checkout, the application passes a `PaymentStrategy` instance to the payment service; the service invokes `pay(orderTotal)` without branching on concrete payment types in the core flow.

---

## 4. Screenshots of Sample Run *(placeholders)*

Insert captured figures from your own execution environment (browser + API responses). Suggested captions:

1. **[Screenshot 1: Menu UI]**  
   *Placeholder — capture the web interface showing menu cards (e.g., Pizza, Burger, Fries), add-on controls, and empty or populated “Your Order” panel.*

2. **[Screenshot 2: Order Summary]**  
   *Placeholder — capture the sidebar or confirmation view listing selected lines, currency display, and total amount returned from the backend.*

3. **[Screenshot 3: Order Tracking]**  
   *Placeholder — capture the post-order dialog showing order identifier, summary, and progressive tracking steps (e.g., Placed → Preparing → …).*

4. ***(Optional) [Screenshot 4: API menu response]***  
   *Placeholder — browser or REST client showing JSON from `GET /api/menu`.*

5. ***(Optional) [Screenshot 5: API order response]***  
   *Placeholder — response body from `POST /api/order` including `orderId`, `items`, and `totalAmount`.*

---

## Suggested Document Metadata *(fill in your course fields)*

| Field | Value |
|-------|--------|
| **Project title** | Online Food Delivery System (Full Stack) |
| **Course / assessment** | Design Patterns — *(insert course code and title)* |
| **Student name / ID** | *(insert)* |
| **Submission date** | *(insert)* |
| **Declaration** | I certify that this submission is my own work, except where acknowledged. *(adapt to institutional wording)* |

---

*End of submission-ready content.*
