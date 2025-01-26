# Inventory Upgrade System

**Description**  
This repository contains a simple **Inventory Upgrade System** for a hypothetical game. Items can be created, managed, upgraded, saved, and loaded from disk. The system demonstrates **OOP design principles**, **exception handling**, and **file I/O** in Java.

---

## Features

1. **Item Management**  
   - **Create Items**: Items have a `name`, a `rarity`, and an optional `upgradeCount` for Epic-tier items.  
   - **Store Items**: Items are held in an `Inventory`, which groups them by rarity.

2. **Upgrade Mechanics**  
   - **Common → Great → Rare → Epic → Legendary**  
   - Epic items have 3 internal states: `Epic (0)`, `Epic 1 (1)`, and `Epic 2 (2)`.  
   - Upgrading consumes multiple copies of the same item or different Epic items (as specified by the rules).

3. **Exception Handling**  
   - **`InvalidItemException`**: Thrown when attempting invalid operations (e.g., adding a null item, negative count).  
   - **`InsufficientItemsException`**: Thrown when not enough items are available for an upgrade.

4. **Persistence**  
   - **Save to File**: Inventory can be saved to a CSV file, with each line representing an item’s rarity, name, epic level, and quantity.  
   - **Load from File**: Inventory can be reloaded from a CSV file, reconstructing items and their quantities.

5. **Utility**  
   - **Random Item Generation**: Demonstrates how to generate an item with a weighted probability for each rarity.

---

## Project Structure

```
.
├── core
│   ├── Inventory.java        // The primary inventory management and upgrade logic
│   ├── Item.java             // Represents an item with a name, rarity, and epic upgrade count
│   └── ...
├── exceptions
│   ├── InvalidItemException.java         // Thrown for invalid item operations
│   └── InsufficientItemsException.java   // Thrown when there aren't enough items for an operation
│
├── inventory
│   └── inventory.csv          // File to store inventory data
│
├── Main.java                  // Example entry point (if applicable)
└── README.md                  // This file

```

---

## How It Works

1. **Creation**  
   ```java
   Item item = new Item("item", Item.Rarity.COMMON);
   ```
2. **Adding to Inventory**  
   ```java
   Inventory inventory = new Inventory();
   inventory.addItem(item, 5);
   ```
3. **Upgrading**  
   ```java
   inventory.upgradeItem(item); // Attempts to upgrade one of the swords
   ```
4. **Saving**  
   ```java
   Path savePath = Path.of("src/inventory/inventory.csv");
   inventory.saveToFile(savePath);
   ```
5. **Loading**  
   ```java
   Inventory loadedInventory = new Inventory();
   loadedInventory.loadFromFile(savePath);
   System.out.println(loadedInventory);
   ```

---

## Requirements

- **Java 8+** (Recommended: Java 11 or above)
- A basic understanding of Java I/O and object-oriented design  

---

## Installation & Setup

1. **Clone or Download** this repository.
2. **Open** the project in your preferred IDE or ensure your classpath is set up for compilation.
3. **Compile** the Java classes:
   ```bash
   javac ./core/*.java ./exceptions/*.java Main.java
   ```
4. **Run**:
   ```bash
   java Main
   ```


---

## Customizing

- **Rarity Upgrade Rules**: Modify logic in `upgradeItem(...)` or the helper methods (`upgradeCommonToGreat`, `upgradeEpicToEpic1`, etc.) to change upgrade requirements.
- **File Format**: Adjust CSV reading/writing if you want a different format (e.g., JSON, XML, or a database).
- **Random Names**: Enhance `Item.randomItem()` to generate more varied or thematically consistent names.

---
