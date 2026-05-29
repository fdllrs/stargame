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

    public boolean deposit(ItemType item, int amount) {
        if (getOccupancy() >= capacity) {
            return false;
        }
        inventory.merge(item, amount, Integer::sum);
        return true;
    }

    public float getOccupancy() {
        return inventory.values().stream().mapToInt(Integer::intValue).sum();
    }

    public boolean withdraw(ItemType item, int amount) {
        if (inventory.getOrDefault(item, 0) < amount) {
            return false;
        }
        inventory.merge(item, -amount, Integer::sum);
        return true;
    }

    public void addCapacity(int capacity) {
        this.capacity += capacity;
    }

    public boolean isFull(ItemType item) {
        return getOccupancy() == capacity;
    }

    public String getFillForDisplay() {
        return getOccupancy() + "/" + getCapacity();
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean canDeposit(int amount) {
        return getOccupancy() + amount <= getCapacity();
    }

    public boolean canWithdraw(ItemType item, int amount) {
        return getAmount(item) >= amount;
    }

    public int getAmount(ItemType item) {
        return inventory.getOrDefault(item, 0);
    }
}
