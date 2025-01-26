package core;

import java.util.Objects;
import java.util.Random;

/**
 * Represents an item within the game, characterized by its name, rarity, and upgrade count.
 * Items can be upgraded to higher rarities by combining multiple items of the same type
 * according to predefined upgrade mechanics.
 */
public class Item {

    /**
     * Enum representing the possible rarities of an item.
     * The order of declaration determines the upgrade path from COMMON to LEGENDARY.
     */
    public enum Rarity {
        COMMON,
        GREAT,
        RARE,
        EPIC,
        LEGENDARY
    }

    /** The name of the item (e.g., "Iron Sword"). */
    private String name;

    /** The rarity of the item, determining its upgrade path and attributes. */
    private Rarity rarity;

    /**
     * Tracks the number of upgrades for EPIC items.
     * For non-EPIC items, this count remains at 0.
     * EPIC items can have upgrade counts of 0 (base EPIC), 1 (EPIC 1), or 2 (EPIC 2).
     */
    private int upgradeCount;

    /**
     * Constructs a new {@code Item} with the specified name and rarity.
     * The {@code upgradeCount} is initialized to 0.
     *
     * @param name    the name of the item
     * @param rarity  the rarity of the item
     */
    public Item(String name, Rarity rarity) {
        this(name, rarity, 0);
    }

    /**
     * Constructs a new {@code Item} with the specified name, rarity, and upgrade count.
     *
     * @param name         the name of the item
     * @param rarity       the rarity of the item
     * @param upgradeCount the upgrade count for EPIC items; should be 0 for non-EPIC items
     */
    public Item(String name, Rarity rarity, int upgradeCount) {
        this.name = name;
        this.rarity = rarity;
        this.upgradeCount = upgradeCount;
    }

    /**
     * Retrieves the name of the item.
     *
     * @return the item's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the item.
     *
     * @param name the new name for the item
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the rarity of the item.
     *
     * @return the item's rarity
     */
    public Rarity getRarity() {
        return rarity;
    }

    /**
     * Sets the rarity of the item.
     *
     * @param rarity the new rarity for the item
     */
    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }

    /**
     * Retrieves the upgrade count of the item.
     * Applicable only for EPIC items.
     *
     * @return the item's upgrade count
     */
    public int getUpgradeCount() {
        return upgradeCount;
    }

    /**
     * Sets the upgrade count of the item.
     * Use this method to update the upgrade level of EPIC items.
     *
     * @param upgradeCount the new upgrade count
     */
    public void setCount(int upgradeCount) {
        this.upgradeCount = upgradeCount;
    }

    /**
     * Determines whether this item is equal to another object.
     * Two items are considered equal if they have the same name, rarity, and upgrade count.
     *
     * @param o the object to compare with
     * @return {@code true} if the items are equal; {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o.getClass() != getClass()) return false;
        Item item = (Item) o;
        return getUpgradeCount() == item.getUpgradeCount()
                && Objects.equals(getName(), item.getName())
                && getRarity() == item.getRarity();
    }

    /**
     * Generates a hash code for this item based on its name, rarity, and upgrade count.
     *
     * @return the hash code of the item
     */
    @Override
    public int hashCode() {
        return Objects.hash(getName() + getRarity() + getUpgradeCount());
    }

    /**
     * Returns a string representation of the item, including its rarity,
     * upgrade count (if applicable), and name.
     *
     * @return the string representation of the item
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(rarity);
        if (rarity == Rarity.EPIC && upgradeCount > 0) {
            sb.append(" ").append(upgradeCount);
        }
        sb.append(" ").append(name);
        return sb.toString();
    }

    /**
     * Generates a random {@code Item} with a predefined probability distribution
     * across different rarities. The probabilities are as follows:
     * <ul>
     *     <li>COMMON: 50%</li>
     *     <li>GREAT: 25%</li>
     *     <li>RARE: 15%</li>
     *     <li>EPIC: 8%</li>
     *     <li>LEGENDARY: 2%</li>
     * </ul>
     *
     * @return a randomly generated {@code Item}
     */
    public static Item randomItem() {
        Random random = new Random();
        double value = random.nextDouble();
        String[] possibleNames = {"Iron Sword", "Steel Shield", "Magic Wand", "Dragon Armor", "Silver Dagger"};
        String name = possibleNames[random.nextInt(possibleNames.length)];

        if (value < 0.5) {
            return new Item(name, Rarity.COMMON);
        } else if (value < 0.75) {
            return new Item(name, Rarity.GREAT);
        } else if (value < 0.9) {
            return new Item(name, Rarity.RARE);
        } else if (value < 0.98) {
            return new Item(name, Rarity.EPIC);
        } else {
            return new Item(name, Rarity.LEGENDARY);
        }
    }
}
