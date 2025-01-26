import core.Inventory;
import core.Item;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        try {
            Inventory inventory = new Inventory();
            Item item = new Item("BloodHound's fangs", Item.Rarity.COMMON);
            inventory.addItem(item, 3);
            inventory.addItem(new Item("Dead's poker", Item.Rarity.RARE), 2);
            inventory.addItem(new Item("Guts greatsword", Item.Rarity.EPIC, 0), 1);

            Path savePath = Path.of("src/inventory/inventory.csv");
            inventory.saveToFile(savePath);
            System.out.println("Inventory saved to " + savePath);

            inventory = new Inventory();

            inventory.loadFromFile(savePath);
            System.out.println("Inventory loaded from " + savePath);

            inventory.upgradeItem(item);

            inventory.display();

            inventory.display();

        } catch (Exception ignored) {
            System.out.println(ignored);
        }
    }
}