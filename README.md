## Week 5 Reflection

**Which approach (Factory vs Manual Chaining) would you expose to developers using your system, and why?**

I would expose the Factory construction approach to application developers because it provides a way to build complex decorated products using user-friendly input strings instead of nested constructor calls. This approach hides the internal wrapping logic and makes product creation more intuitive and less error-prone. It also makes resulting code easier to scale and maintain, since adding a new modifier or a new size is nothing more than a matter of modifying the factory logic instead of client code.

## Week 6 Refactoring Summary

**Smells Removed:**
- God Class: Split OrderManagerGod into cohesive classes
- Long Method: Extracted pricing, tax, receipt, payment steps
- Primitive Obsession: Replaced string discount codes with DiscountPolicy objects and string payment types with existing PaymentStrategy pattern
- Global State: Eliminated static TAX_PERCENT and LAST_DISCOUNT_CODE
- Feature Envy: Moved discount/tax calculations to dedicated policy classes
- Shotgun Surgery: Centralized pricing logic in policy classes

**Refactorings Applied:**
- Extract Class (DiscountPolicy, TaxPolicy, PricingService, ReceiptPrinter)
- Replace Conditional with Polymorphism (discount types)
- Dependency Injection (constructor injection for all dependencies)
- Remove Global State (eliminated all static variables)

**SOLID Principles Satisfied:**
- Single Responsibility: Each class has one clear purpose
- Open/Closed: Can add new discount types without modifying existing code
- Dependency Inversion: Depends on abstractions (DiscountPolicy, TaxPolicy) not concretions
- Liskov Substitution Principle (LSP): All DiscountPolicy types can substitute safely
- Interface Segregation Principle (ISP): Simple, focused interfaces for pricing/tax

## Testing Strategy
- Characterization tests locked OrderManagerGod outputs
- Unit tests for policies and PricingService validated correctness
- Demo verified new system prints identical receipts

## Extensibility
To add a new discount type (e.g., SeasonalDiscount):
```java
public final class SeasonalDiscount implements DiscountPolicy { 
    // Implementation here
}
```
Create class implementing DiscountPolicy, inject into PricingService constructor. Zero existing code modification required.

## Architecture Summary

CheckoutService orchestrates → ProductFactory builds → PricingService uses DiscountPolicy and TaxPolicy → ReceiptPrinter formats → PaymentStrategy handles payment I/O. No globals, all dependencies injected.
![Architectu re Diagram](./diagram-week6.jpg)

**CheckoutService** → orchestrates the checkout flow
- Coordinates all collaborators: product creation, pricing, receipt formatting, and payment
- No internal logic for tax, discount, or printing, just delegates

**ProductFactory** → builds the Product
- Uses factory & decorator pattern to assemble drinks and add-ons

**PricingService** → uses DiscountPolicy and TaxPolicy
- Computes subtotal, discount, tax, and total
- Returns a PricingResult record containing all computed values

**DiscountPolicy** → defines how discounts are computed
- Implementations: NoDiscount, LoyaltyPercentDiscount, FixedCouponDiscount

**TaxPolicy** → defines how tax is applied
- Implementation: FixedRateTaxPolicy

**ReceiptPrinter** → formats the receipt text for display/printing

**PaymentStrategy** → handles payment I/O
- Implementations: CashPayment, CardPayment, WalletPayment