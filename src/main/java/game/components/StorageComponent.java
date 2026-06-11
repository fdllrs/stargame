package game.components;

import game.items.ItemType;

import java.util.HashMap;
import java.util.Map;

public class StorageComponent {
    Map<ItemType, Integer> inventory;
    int capacity;

    public StorageComponent(int capacity) {
        inventory = new HashMap<>();
        this.capacity = capacity;
    }

    public void addCapacity(int capacity) {
        this.capacity += capacity;
    }

    public boolean attemptMoveItemsTo(StorageComponent otherStorage,
                                      ItemType item,
                                      int amount) {

        if (!canMoveItemsTo(otherStorage, item, amount))
            return false;
        withdraw(item, amount);
        otherStorage.deposit(item, amount);
        return true;
    }

    public boolean canDeposit(int amount) {
        return getOccupancy() + amount <= getCapacity();
    }

    private boolean canMoveItemsTo(StorageComponent otherStorage,
                                   ItemType item,
                                   int amount) {
        return canWithdraw(item, amount) & otherStorage.canDeposit(amount);
    }

    public boolean canWithdraw(ItemType item, int amount) {
        return getAmount(item) >= amount;
    }

    public void deposit(ItemType item, int amount) {
        if (getOccupancy() >= capacity) {
            return;
        }
        inventory.merge(item, amount, Integer::sum);
    }

    public int getAmount(ItemType item) {
        return inventory.getOrDefault(item, 0);
    }

    public int getCapacity() {
        return capacity;
    }

    public String getFillForDisplay() {
        return getOccupancy() + "/" + getCapacity();
    }

    public int getOccupancy() {
        return inventory.values().stream().mapToInt(Integer::intValue).sum();
    }

    public void withdraw(ItemType item, int amount) {
        if (inventory.getOrDefault(item, 0) < amount) {
            return;
        }
        inventory.merge(item, -amount, Integer::sum);
    }
}
