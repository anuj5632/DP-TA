# Online Food Delivery System
## End-to-End Project Report

## 1. Executive Summary
This project is a modular, extensible backend simulation of an Online Food Delivery System built in Java using Object-Oriented Design and three mandatory design patterns:
1. Singleton (Creational)
2. Decorator (Structural)
3. Strategy (Behavioral)

The system models a realistic order lifecycle:
- Browse menu
- Create and manage orders
- Add and remove items
- Apply dynamic food customizations
- Select payment method at runtime
- Process checkout
- Generate final bill

The implementation is layered with clean separation of concerns using models, services, patterns, controllers, and main execution modules.

## 2. Problem Statement
Food delivery systems must handle customizable food combinations, multiple payment methods, and global order tracking in a scalable and maintainable way. A direct monolithic implementation quickly becomes rigid and difficult to extend.

This project addresses that by applying design patterns to isolate responsibilities:
- Global order registry is centralized through Singleton
- Food customizations are composed dynamically through Decorator
- Payment behavior is switched at runtime through Strategy

## 3. Objectives
1. Build a production-style OOP backend simulation, not a toy example.
2. Demonstrate strict use of Singleton, Decorator, and Strategy.
3. Ensure modular architecture with loose coupling and high cohesion.
4. Support extension with minimal change to existing code.
5. Provide interview and viva-ready clarity with class-level responsibilities.

## 4. Technology Stack
- Language: Java
- Programming style: OOP, interface-driven design
- Build: javac + java CLI (no external dependencies required)
- Runtime: Console simulation with structured logs

## 5. Project Structure
DP_TA/
- src/com/fooddelivery/controllers
  - OrderController.java
- src/com/fooddelivery/main
  - FoodDeliveryApplication.java
- src/com/fooddelivery/models
  - CustomizationRequest.java
  - MenuItemDefinition.java
  - MenuItemType.java
  - Order.java
  - OrderItem.java
  - OrderStatus.java
- src/com/fooddelivery/patterns/behavioral
  - PaymentStrategy.java
  - PaymentResult.java
  - UpiPaymentStrategy.java
  - CreditCardPaymentStrategy.java
  - CashOnDeliveryStrategy.java
- src/com/fooddelivery/patterns/creational
  - OrderManager.java
- src/com/fooddelivery/patterns/structural
  - FoodItem.java
  - BaseFoodItem.java
  - Pizza.java
  - Burger.java
  - Sandwich.java
  - FoodItemDecorator.java
  - ExtraCheeseDecorator.java
  - ExtraSauceDecorator.java
  - ToppingDecorator.java
  - DrinkAddonDecorator.java
- src/com/fooddelivery/services
  - MenuService.java
  - FoodFactoryService.java
  - CustomizationService.java
  - PaymentService.java
  - BillService.java
  - LoggingService.java

## 6. High-Level Architecture
### 6.1 Layered Responsibilities
1. Models Layer
Contains pure domain state and entities.
Examples: Order, OrderItem, MenuItemDefinition.

2. Patterns Layer
Contains pattern-specific abstractions and implementations.
- behavioral: payment strategies
- creational: singleton manager
- structural: food decorators

3. Services Layer
Contains reusable business logic.
Examples: menu operations, payment processing, bill generation, customization application.

4. Controller Layer
Coordinates use-cases and workflow steps.
Example: OrderController orchestrates create order, add item, checkout, bill generation.

5. Main Layer
Entry point that simulates complete user journeys.

### 6.2 Data and Control Flow
1. Main starts application.
2. Controller retrieves menu from MenuService.
3. User selection maps to MenuItemDefinition.
4. FoodFactoryService creates base FoodItem.
5. CustomizationService applies decorators.
6. Item added to Order.
7. Order stored in OrderManager singleton.
8. Checkout invokes PaymentService with chosen strategy.
9. BillService generates invoice.
10. Logs show full lifecycle.

## 7. Design Pattern Implementation Details

## 7.1 Singleton Pattern (Creational)
### Class
OrderManager.java

### Why needed
Order state must be globally consistent. Multiple manager instances can cause split state and incorrect active order tracking.

### Implementation
- Private constructor
- Private static volatile instance
- Public static getInstance()
- Double-checked locking for thread safety

### Responsibility
- Add, fetch, remove, and list orders
- Maintain active order count
- Provide global access point across layers

### Bonus Singleton
LoggingService is also singleton to maintain centralized, consistent logging behavior.

## 7.2 Decorator Pattern (Structural)
### Core abstraction
FoodItem interface:
- getCost()
- getDescription()

### Concrete base components
- Pizza
- Burger
- Sandwich

### Decorator base
FoodItemDecorator wraps FoodItem and delegates by default.

### Concrete decorators
- ExtraCheeseDecorator
- ExtraSauceDecorator
- ToppingDecorator
- DrinkAddonDecorator

### Why needed
Food customizations are combinatorial in nature. Instead of creating many subclasses like CheesePizzaWithSauceAndDrink, decorators compose behavior dynamically at runtime.

### Runtime chaining example
Base: Pizza
After chaining:
Pizza + Extra Cheese + Extra Sauce + Olives + Jalapenos + Drink(Coke)

Each decorator contributes:
- Additional cost
- Additional description fragment

## 7.3 Strategy Pattern (Behavioral)
### Strategy interface
PaymentStrategy:
- getPaymentMethod()
- pay(amount)

### Concrete strategies
- UpiPaymentStrategy
- CreditCardPaymentStrategy
- CashOnDeliveryStrategy

### Why needed
Payment methods represent interchangeable algorithms. Strategy avoids conditional-heavy checkout code and supports easy addition of new payment methods.

### Runtime behavior
At checkout, user-selected strategy is injected into PaymentService.
PaymentService executes strategy polymorphically without knowing implementation details.

## 8. Core Module Walkthrough

## 8.1 models
Order:
- Holds order ID, customer, status, items, selected payment strategy
- Calculates total dynamically from item subtotals

OrderItem:
- Holds customized FoodItem and quantity
- Computes subtotal

CustomizationRequest:
- Encapsulates customization options (cheese, sauce, toppings, drinks)
- Keeps request payload clean and extensible

MenuItemDefinition:
- Menu code, display name, type, base price

## 8.2 services
MenuService:
- Provides menu list and item lookup by code

FoodFactoryService:
- Converts MenuItemDefinition into base component (Pizza/Burger/Sandwich)

CustomizationService:
- Applies decorator chain based on CustomizationRequest

PaymentService:
- Executes selected strategy
- Transitions order status
- Handles empty-order guard condition

BillService:
- Generates formatted final invoice

LoggingService:
- Central timestamped logs (singleton)

## 8.3 controllers
OrderController coordinates use-cases:
1. browseMenu()
2. createOrder(customer)
3. addItemToOrder(order, menuItem, customization, quantity)
4. removeItemFromOrder(order, index)
5. checkout(order, strategy)
6. generateBill(order)

This keeps main concise and makes business flows testable and reusable.

## 8.4 main
FoodDeliveryApplication simulates realistic scenarios:
- Order 1: multiple customized items + UPI
- Order 2: add/remove flow + Credit Card
- Order 3: simple order + Cash on Delivery

This demonstrates:
- Multiple orders handling
- Runtime customization
- Runtime strategy switching
- End-to-end lifecycle logs

## 9. UML-Style Text Diagram

### 9.1 Structural View
FoodItem (interface)
- getCost()
- getDescription()

BaseFoodItem (abstract) implements FoodItem
- Pizza
- Burger
- Sandwich

FoodItemDecorator (abstract) implements FoodItem
- has-a FoodItem
- ExtraCheeseDecorator
- ExtraSauceDecorator
- ToppingDecorator
- DrinkAddonDecorator

PaymentStrategy (interface)
- pay(amount)

UpiPaymentStrategy implements PaymentStrategy
CreditCardPaymentStrategy implements PaymentStrategy
CashOnDeliveryStrategy implements PaymentStrategy

OrderManager (singleton)
- getInstance()
- addOrder()
- findOrder()
- listAllOrders()
- removeOrder()

### 9.2 Sequence View (Checkout)
1. User selects menu item
2. Controller asks factory for base FoodItem
3. Controller asks customization service to apply decorators
4. Controller adds customized item to order
5. Controller sends order to payment service with selected strategy
6. Payment service executes strategy
7. Order status updates
8. Bill service generates final bill

## 10. SOLID and Design Quality
1. Single Responsibility Principle
- Each class has focused responsibility.

2. Open/Closed Principle
- Add new decorator or payment strategy without modifying existing flow.

3. Liskov Substitution Principle
- Any PaymentStrategy is interchangeable at checkout.
- Any FoodItem implementation can be wrapped and used uniformly.

4. Interface Segregation Principle
- Small, focused interfaces like FoodItem and PaymentStrategy.

5. Dependency Inversion (pragmatic)
- High-level flow depends on abstractions (strategy interface, food component contracts) rather than concrete implementations.

## 11. Runtime Behavior and Logs
The application prints step-by-step logs for:
- Item selection
- Decorator application
- Strategy selection
- Payment processing outcome
- Bill generation details

This improves observability and makes debugging/demo presentation easier.

## 12. Validation and Execution
### 12.1 Compile
PowerShell from project root:
1. if (Test-Path out) { Remove-Item -Recurse -Force out }
2. New-Item -ItemType Directory -Path out | Out-Null
3. $sources = Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
4. javac -d out $sources

### 12.2 Run
java -cp out com.fooddelivery.main.FoodDeliveryApplication

### 12.3 Verified Result
- Application ran successfully
- Exit code: 0
- Complete menu, order flow, payment flow, and billing output generated

## 13. Sample Output Summary
- Menu with 3 items displayed
- Order 1 total: Rs. 30.15 via UPI
- Order 2 total: Rs. 18.90 via Credit Card
- Order 3 total: Rs. 6.50 via COD
- Active orders tracked by singleton manager: 3

## 14. Extensibility Guide
### 14.1 Add a new food item type
1. Add enum in MenuItemType
2. Add concrete class extending BaseFoodItem
3. Add factory branch in FoodFactoryService
4. Add menu row in MenuService

### 14.2 Add a new customization
1. Create new decorator extending FoodItemDecorator
2. Add request option to CustomizationRequest
3. Apply chain rule in CustomizationService

### 14.3 Add a new payment mode
1. Implement PaymentStrategy
2. Use new strategy at checkout

No major controller or service rewrites required.

## 15. Error Handling and Constraints
Implemented safeguards:
- Quantity must be greater than zero
- Cannot process payment for empty order
- Remove item validates index bounds

Potential production enhancements:
- Persistent storage integration
- User authentication and sessions
- Inventory and stock checks
- Retry and failure compensation for payment gateways
- API layer (Spring Boot REST)
- Unit and integration tests

## 16. Interview and Viva Talking Points
1. Why Singleton for OrderManager?
Centralized, globally consistent active-order state with thread-safe access.

2. Why Decorator for food customization?
Avoids subclass explosion and supports runtime composition of unlimited combinations.

3. Why Strategy for payment?
Payment algorithms are swappable at runtime and extension-friendly.

4. What makes architecture clean?
Layered structure, focused services, abstraction-first pattern contracts, and low coupling.

5. How is this project production-ready in design?
Clear boundaries, extensibility points, runtime logs, guarded transitions, and realistic flow simulation.

## 17. Conclusion
This project successfully demonstrates enterprise-grade use of three core design patterns in a practical food delivery domain. The system is modular, maintainable, extensible, and suitable for academic submission, interviews, and viva explanation.
