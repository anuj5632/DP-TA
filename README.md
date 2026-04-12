# Online Food Delivery System (Java, OOP, Design Patterns)

A production-style, modular console backend simulation for an Online Food Delivery System.

Implemented design patterns (strictly as requested):
- Creational: Singleton
- Structural: Decorator
- Behavioral: Strategy

## 1. Folder Structure

```text
DP_TA/
  src/
    com/fooddelivery/
      controllers/
        OrderController.java
      main/
        FoodDeliveryApplication.java
      models/
        CustomizationRequest.java
        MenuItemDefinition.java
        MenuItemType.java
        Order.java
        OrderItem.java
        OrderStatus.java
      patterns/
        behavioral/
          CashOnDeliveryStrategy.java
          CreditCardPaymentStrategy.java
          PaymentResult.java
          PaymentStrategy.java
          UpiPaymentStrategy.java
        creational/
          OrderManager.java
        structural/
          BaseFoodItem.java
          Burger.java
          DrinkAddonDecorator.java
          ExtraCheeseDecorator.java
          ExtraSauceDecorator.java
          FoodItem.java
          FoodItemDecorator.java
          Pizza.java
          Sandwich.java
          ToppingDecorator.java
      services/
        BillService.java
        CustomizationService.java
        FoodFactoryService.java
        LoggingService.java
        MenuService.java
        PaymentService.java
```

## 2. Class Diagram (Text)

```text
                        +-------------------------+
                        |      FoodItem           |
                        | +getCost(): double      |
                        | +getDescription():Str   |
                        +------------+------------+
                                     ^
                                     |
                    +----------------+----------------+
                    |                                 |
           +-------------------+              +----------------------+
           |    BaseFoodItem   |              |   FoodItemDecorator  |
           +-------------------+              +----------------------+
           | name, basePrice   |              | -foodItem: FoodItem  |
           +----+---------+----+              +-----+----------------+
                |         |                         |
              Pizza     Burger                    +--------------------------+
                              Sandwich            | ExtraCheeseDecorator     |
                                                  | ExtraSauceDecorator      |
                                                  | ToppingDecorator         |
                                                  | DrinkAddonDecorator      |
                                                  +--------------------------+

+-------------------------------+      +-------------------------------+
|       PaymentStrategy         |<-----|        PaymentService         |
| +getPaymentMethod(): String   |      +-------------------------------+
| +pay(amount): PaymentResult   |      Uses runtime-selected strategy
+---------------+---------------+
                |
   +------------+--------------+----------------+
   |                           |                |
UpiPaymentStrategy   CreditCardPaymentStrategy  CashOnDeliveryStrategy

+-------------------------------+
|         OrderManager          |   (Singleton - thread-safe)
| -instance (volatile static)   |
| +getInstance()                |
| +addOrder/find/list/remove    |
+---------------+---------------+
                |
                v
             manages
                |
              Order
```

## 3. Pattern Usage Explanation

### Singleton Pattern
- Implemented in `OrderManager` as a thread-safe singleton using double-checked locking (`volatile` + synchronized lock).
- Ensures exactly one global manager instance for all active orders.
- Global access via `OrderManager.getInstance()` from controller/main layers.

Bonus singleton:
- `LoggingService` is also implemented as a singleton to provide central logging.

### Decorator Pattern
- `FoodItem` is the component interface.
- Base concrete components: `Pizza`, `Burger`, `Sandwich`.
- Decorator base: `FoodItemDecorator`.
- Concrete decorators: `ExtraCheeseDecorator`, `ExtraSauceDecorator`, `ToppingDecorator`, `DrinkAddonDecorator`.
- Runtime chaining is supported in `CustomizationService`, which wraps decorators dynamically based on customization input.

### Strategy Pattern
- `PaymentStrategy` defines interchangeable payment behavior.
- Concrete strategies:
  - `UpiPaymentStrategy`
  - `CreditCardPaymentStrategy`
  - `CashOnDeliveryStrategy`
- Runtime strategy selection happens in checkout flow (`OrderController.checkout(...)` -> `PaymentService.processPayment(...)`).

## 4. Clean Architecture Notes

- High cohesion:
  - Models contain state.
  - Services contain reusable business logic.
  - Controller orchestrates use-cases.
  - Pattern packages isolate design pattern implementations.
- Loose coupling:
  - Strategy uses interface polymorphism.
  - Decorator composes behavior via common `FoodItem` contract.
  - Controller depends on service abstractions/roles.
- Extensibility:
  - Add new food item type by adding enum + class + factory branch.
  - Add new decorator without changing existing decorator chain behavior.
  - Add new payment method by implementing `PaymentStrategy`.

## 5. Build and Run

From project root:

```powershell
if (Test-Path out) { Remove-Item -Recurse -Force out }
New-Item -ItemType Directory -Path out | Out-Null
$sources = Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -d out $sources
java -cp out com.fooddelivery.main.FoodDeliveryApplication
```

## 6. Sample Run Output (Excerpt)

```text
[INFO  2026-04-11 17:56:18] ========== ONLINE FOOD DELIVERY SYSTEM STARTED ==========
[INFO  2026-04-11 17:56:18] User is browsing menu

=============== MENU ===============
M1 | Margherita Pizza          | Rs. 8.00
M2 | Crispy Veg Burger         | Rs. 6.50
M3 | Grilled Paneer Sandwich   | Rs. 5.75
====================================

[INFO  ...] Item selected: Margherita Pizza (Base price Rs. 8.00)
[INFO  ...] Decorators applied: Pizza + Extra Cheese + Extra Sauce + Olives + Jalapenos + Drink(Coke)
[INFO  ...] Strategy chosen: UPI
[INFO  ...] UPI payment of Rs. 30.15 successful using UPI ID: rahul@upi

================ FINAL BILL ================
Order ID : <uuid>
Customer : Rahul Sharma
Status   : PAID
--------------------------------------------
1. Pizza + Extra Cheese + Extra Sauce + Olives + Jalapenos + Drink(Coke) x1 => Rs. 14.05
2. Burger + Extra Sauce + Caramelized Onion x2 => Rs. 16.10
--------------------------------------------
TOTAL    : Rs. 30.15
============================================
```

## 7. Realistic Flow Implemented

1. User browses menu
2. Base food object created from menu selection
3. Decorators applied dynamically at runtime
4. Order registered in singleton `OrderManager`
5. Payment strategy selected at runtime
6. Order payment processed
7. Final bill generated and printed

This code is ready to compile and run as-is.
