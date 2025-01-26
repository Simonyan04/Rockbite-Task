package core;

import exceptions.InsufficientItemsException;
import exceptions.InvalidItemException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Represents an inventory system that manages items of various rarities.
 * Allows adding, removing, and upgrading items based on their rarity.
 */
public class Inventory {
    private final Map<Item.Rarity, Map<Item, Integer>> itemsMap = new HashMap<>();

    /**
     * Constructs a core.Inventory with the specified initial items.
     *
     * @param items the items to be added to the inventory
     * @throws InvalidItemException if any of the items are invalid
     */
    public Inventory(Item... items) throws InvalidItemException {
        this();
        for (Item item : items) addItem(item);
    }
    public Inventory(){
        for(Item.Rarity rarity : Item.Rarity.values()){
            itemsMap.put(rarity, new HashMap<Item, Integer>());
        }
    }
    /**
     * Adds a specified number of the given item to the inventory.
     *
     * @param item  the item to be added
     * @param count the number of items to add
     * @throws InvalidItemException if the item is null or the count is non-positive
     */
    public void addItem(Item item, int count)
            throws InvalidItemException {
        if (item == null) throw new InvalidItemException("Cannot add a null item to inventory.");
        if (count <= 0) throw new InvalidItemException("Cannot add a non-positive count of items.");

        Map<Item, Integer> map = mapFromItem(item);
        map.merge(item, count, Integer::sum);
    }

    /**
     * Adds a single instance of the given item to the inventory.
     *
     * @param item the item to be added
     * @throws InvalidItemException if the item is null
     */
    public void addItem(Item item) throws InvalidItemException {
        addItem(item, 1);
    }

    /**
     * Removes a specified number of the given item from the inventory.
     *
     * @param item  the item to be removed
     * @param count the number of items to remove
     * @return true if the removal was successful; false otherwise
     * @throws InvalidItemException if the item is null or the count is non-positive
     */
    public boolean removeItem(Item item, int count)
            throws InvalidItemException {
        if (item == null) throw new InvalidItemException("Cannot remove a null item.");

        if (count <= 0) throw new InvalidItemException("Cannot remove a non-positive count of items.");

        Map<Item, Integer> map = mapFromItem(item);
        if (!map.containsKey(item)) return false;

        int currentCount = map.get(item);
        if (currentCount < count) return false;


        if (currentCount == count) map.remove(item);
        else map.put(item, currentCount - count);

        return true;
    }

    /**
     * Removes a single instance of the given item from the inventory.
     *
     * @param item the item to be removed
     * @return true if the removal was successful; false otherwise
     * @throws InvalidItemException if the item is null
     */
    public boolean removeItem(Item item) throws InvalidItemException {
        return removeItem(item, 1);
    }

    /**
     * Upgrades the specified item if the inventory has enough items to fulfill the upgrade requirements.
     *
     * @param item the item to be upgraded
     * @return true if the upgrade was successful; false otherwise
     * @throws InvalidItemException       if the item is invalid
     * @throws InsufficientItemsException if there are not enough items to perform the upgrade
     */
    public boolean upgradeItem(Item item)
            throws InvalidItemException, InsufficientItemsException {

        Map<Item, Integer> map = mapFromItem(item);
        if (!map.containsKey(item) || map.get(item) < 1) throw new InsufficientItemsException("Cannot upgrade. " + item.getName() + " is not available in sufficient quantity.");


        Item.Rarity currentRarity = item.getRarity();
        int epicCount = item.getUpgradeCount();

        switch (currentRarity) {
            case COMMON:
                return upgradeCommonToGreat(item);

            case GREAT:
                return upgradeGreatToRare(item);

            case RARE:
                return upgradeRareToEpic(item);

            case EPIC:
                if (epicCount == 0) return upgradeEpicToEpic1(item);
                else if (epicCount == 1) return upgradeEpic1ToEpic2(item);
                else if (epicCount == 2) return upgradeEpic2ToLegendary(item);
                break;

            case LEGENDARY:
                return false;

            default:
                throw new InvalidItemException("Unknown rarity: " + currentRarity);
        }
        return false;
    }

    public void display(){
        System.out.println(this);
    }
    @Override
    public String toString() {
        if (isEmpty()) {
            return "Inventory is empty.";
        }

        StringBuilder sb = new StringBuilder();

        for (Item.Rarity rarity : Item.Rarity.values()) {
            Map<Item, Integer> subMap = itemsMap.get(rarity);
            if (subMap == null || subMap.isEmpty()) {
                continue;
            }
            sb.append("\n--- ").append(rarity).append(" ITEMS ---\n");

            List<Item> itemList = new ArrayList<>(subMap.keySet());

            for (Item item : itemList) {
                int count = subMap.get(item);
                sb.append(count).append("x ").append(item).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Saves the current inventory to a file in CSV format.
     * <p>
     * Each line of the file follows the pattern:
     * <pre>
     * RARITY,ITEM_NAME,EPIC_COUNT,QUANTITY
     * </pre>
     * For example:
     * <pre>
     * COMMON,Iron Sword,0,3
     * GREAT,Steel Shield,0,1
     * EPIC,Dragon Spear,2,2
     * </pre>
     *
     * @param filePath the path of the file to which the inventory will be saved
     * @throws IOException if an I/O error occurs writing to or creating the file
     */
    public void saveToFile(Path filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(filePath))) {
            // Iterate through each rarity in the itemsMap
            for (Map.Entry<Item.Rarity, Map<Item, Integer>> rarityEntry : itemsMap.entrySet()) {
                Item.Rarity rarity = rarityEntry.getKey();
                Map<Item, Integer> subMap = rarityEntry.getValue();

                // For each item in that rarity group, write a CSV line
                for (Map.Entry<Item, Integer> itemEntry : subMap.entrySet()) {
                    Item item = itemEntry.getKey();
                    int quantity = itemEntry.getValue();

                    writer.printf(
                            "%s,%s,%d,%d%n",
                            rarity,               // The rarity (e.g., COMMON)
                            item.getName(),       // The item's name
                            item.getUpgradeCount(), // The epic upgrade level
                            quantity              // How many of this item exist
                    );
                }
            }
        }
    }

    /**
     * Loads inventory data from a file in CSV format.
     * <p>
     * Each line is expected in the pattern:
     * <pre>
     * RARITY,ITEM_NAME,EPIC_COUNT,QUANTITY
     * </pre>
     * Missing or malformed lines are skipped.
     *
     * @param filePath the path of the file from which to load inventory
     * @throws IOException           if an I/O error occurs reading the file
     * @throws InvalidItemException  if an invalid item is detected while loading
     */
    public void loadFromFile(Path filePath) throws IOException, InvalidItemException {
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;


                String[] parts = line.split(",");
                if (parts.length != 4) continue;


                Item.Rarity rarity;
                try {
                    rarity = Item.Rarity.valueOf(parts[0].trim());
                } catch (IllegalArgumentException e) {
                    continue;
                }

                String itemName = parts[1].trim();

                int epicCount;
                try {
                    epicCount = Integer.parseInt(parts[2].trim());
                } catch (NumberFormatException e) {
                    continue;
                }

                int quantity;
                try {
                    quantity = Integer.parseInt(parts[3].trim());
                } catch (NumberFormatException e) {
                    continue;
                }
                Item item = new Item(itemName, rarity, epicCount);
                addItem(item, quantity);
            }
        }
    }


    /**
     * Upgrades a COMMON item to GREAT.
     *
     * @param commonItem the COMMON item to upgrade
     * @return true if the upgrade was successful
     * @throws InvalidItemException       if the item is invalid
     * @throws InsufficientItemsException if there are not enough COMMON items
     */
    private boolean upgradeCommonToGreat(Item commonItem)
            throws InvalidItemException, InsufficientItemsException {

        int needed = 2;
        if (!hasSufficientItems(commonItem, needed)) throw new InsufficientItemsException(
                "Not enough Common items to upgrade "
                        + commonItem.getName() + " to GREAT."
        );


        removeItem(commonItem, needed + 1);
        Item newGreatItem = new Item(commonItem.getName(), Item.Rarity.GREAT);
        addItem(newGreatItem, 1);
        return true;
    }

    public boolean isEmpty(){
        for (Map<Item, Integer> map : itemsMap.values()) if(!map.isEmpty()) return false;
        return true;
    }

    /**
     * Upgrades a GREAT item to RARE.
     *
     * @param greatItem the GREAT item to upgrade
     * @return true if the upgrade was successful
     * @throws InvalidItemException       if the item is invalid
     * @throws InsufficientItemsException if there are not enough GREAT items
     */
    private boolean upgradeGreatToRare(Item greatItem)
            throws InvalidItemException, InsufficientItemsException {

        int needed = 2;
        if (!hasSufficientItems(greatItem, needed)) throw new InsufficientItemsException(
                "Not enough GREAT items to upgrade "
                        + greatItem.getName() + " to RARE."
        );

        removeItem(greatItem, needed + 1);
        Item newRareItem = new Item(greatItem.getName(), Item.Rarity.RARE);
        addItem(newRareItem, 1);

        System.out.println("Upgraded " + greatItem.getName() + " from GREAT to RARE.");
        return true;
    }

    /**
     * Upgrades a RARE item to EPIC.
     *
     * @param rareItem the RARE item to upgrade
     * @return true if the upgrade was successful
     * @throws InvalidItemException       if the item is invalid
     * @throws InsufficientItemsException if there are not enough RARE items
     */
    private boolean upgradeRareToEpic(Item rareItem)
            throws InvalidItemException, InsufficientItemsException {

        int needed = 2;
        if (!hasSufficientItems(rareItem, needed)) throw new InsufficientItemsException(
                "Not enough RARE items to upgrade "
                        + rareItem.getName() + " to EPIC."
        );

        removeItem(rareItem, needed + 1);
        Item newEpicItem = new Item(rareItem.getName(), Item.Rarity.EPIC, 0);
        addItem(newEpicItem, 1);

        return true;
    }

    /**
     * Upgrades an EPIC item to EPIC 1.
     *
     * @param epicItem the EPIC item to upgrade
     * @return true if the upgrade was successful
     * @throws InvalidItemException       if the item is invalid
     * @throws InsufficientItemsException if there are not enough EPIC items
     */
    private boolean upgradeEpicToEpic1(Item epicItem)
            throws InvalidItemException, InsufficientItemsException {

        Item anyEpicItem = findAnyEpicItemExcluding(epicItem);
        if (anyEpicItem == null) {
            throw new InsufficientItemsException(
                    "Not enough EPIC items (any) to upgrade to EPIC 1."
            );
        }

        removeItem(epicItem, 1);
        removeItem(anyEpicItem, 1);

        Item newItem = new Item(epicItem.getName(), Item.Rarity.EPIC, 1);
        addItem(newItem, 1);

        System.out.println("Upgraded " + epicItem.getName() + " from EPIC to EPIC 1.");
        return true;
    }

    /**
     * Upgrades an EPIC 1 item to EPIC 2.
     *
     * @param epic1Item the EPIC 1 item to upgrade
     * @return true if the upgrade was successful
     * @throws InvalidItemException       if the item is invalid
     * @throws InsufficientItemsException if there are not enough EPIC items
     */
    private boolean upgradeEpic1ToEpic2(Item epic1Item)
            throws InvalidItemException, InsufficientItemsException {

        Item anyEpicItem = findAnyEpicItemExcluding(epic1Item);
        if (anyEpicItem == null) throw new InsufficientItemsException(
                "Not enough EPIC items to upgrade to EPIC 2."
        );


        removeItem(epic1Item, 1);
        removeItem(anyEpicItem, 1);

        Item newItem = new Item(epic1Item.getName(), Item.Rarity.EPIC, 2);
        addItem(newItem, 1);

        return true;
    }

    /**
     * Upgrades an EPIC 2 item to LEGENDARY.
     *
     * @param epic2Item the EPIC 2 item to upgrade
     * @return true if the upgrade was successful
     * @throws InvalidItemException       if the item is invalid
     * @throws InsufficientItemsException if there are not enough EPIC 2 items
     */
    private boolean upgradeEpic2ToLegendary(Item epic2Item)
            throws InvalidItemException, InsufficientItemsException {

        int needed = 2;
        if (!hasSufficientItems(epic2Item, needed)) throw new InsufficientItemsException(
                "Not enough EPIC 2 items to upgrade "
                        + epic2Item.getName() + " to LEGENDARY."
        );
        removeItem(epic2Item, needed + 1);
        Item newLegendary = new Item(epic2Item.getName(), Item.Rarity.LEGENDARY);
        addItem(newLegendary, 1);

        return true;
    }

    /**
     * Checks if the inventory has sufficient quantity of the specified item.
     *
     * @param item    the item to check
     * @param needed  the number of additional items needed
     * @return true if the inventory has at least (needed + 1) of the item; false otherwise
     * @throws InvalidItemException if the item is invalid
     */
    private boolean hasSufficientItems(Item item, int needed)
            throws InvalidItemException {
        Map<Item, Integer> map = mapFromItem(item);
        return map.getOrDefault(item, 0) >= (needed + 1);
    }

    /**
     * Finds any EPIC item in the inventory excluding the specified item.
     *
     * @param excludedEpic the EPIC item to exclude from the search
     * @return an EPIC item if found; null otherwise
     * @throws InvalidItemException if the excluded item is invalid
     */
    private Item findAnyEpicItemExcluding(Item excludedEpic)
            throws InvalidItemException {

        Map<Item, Integer> map = itemsMap.get(Item.Rarity.EPIC);

        for (Item it : map.keySet()) {
            int count = map.get(it);
            if (count > 0) {
                if (it.equals(excludedEpic)) {
                    if (count > 1) return it;
                    else continue;
                }
                return it;
            }
        }
        return null;
    }

    /**
     * Retrieves the map corresponding to the rarity of the specified item.
     *
     * @param item the item whose rarity determines the map to return
     * @return the map associated with the item's rarity
     * @throws InvalidItemException if the item is null or has an unknown rarity
     */
    private Map<Item, Integer> mapFromItem(Item item)
            throws InvalidItemException {
        if (item == null) throw new InvalidItemException();
        return itemsMap.get(item.getRarity());
    }
}