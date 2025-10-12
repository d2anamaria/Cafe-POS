[week 5]

**Which approach (Factory vs Manual Chaining) would you expose to developers using your system, and why?**

I would expose the Factory construction approach to application developers because it provides a way to build complex decorated products using user-friendly input strings instead of nested constructor calls. This approach hides the internal wrapping logic and makes product creation more intuitive and less error-prone. It also makes resulting code easier to scale and maintain, since adding a new modifier or a new size is nothing more than a matter of modifying the factory logic instead of client code.
