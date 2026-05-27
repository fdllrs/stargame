package game.managers;

import java.util.EnumMap;
import java.util.Map;

public class ResourceManager {

    private final Map<ResourceType, Integer> resourceCount;

    public ResourceManager() {
        this.resourceCount = new EnumMap<>(ResourceType.class);
        for (ResourceType resource : ResourceType.values()) {
            resourceCount.put(resource, 0);
        }
    }

    public void gatherResource(ResourceType resource, int amount) {
        resourceCount.merge(resource, amount, Integer::sum);
    }

    public void setAmount(ResourceType resource, int amount) {
        resourceCount.put(resource, amount);
    }

    public boolean spendResource(ResourceType resource, int amount) {
        int current = amountOf(resource);
        if (current < amount) {
            return false;
        }
        resourceCount.put(resource, current - amount);
        return true;
    }

    public int amountOf(ResourceType resource) {
        return resourceCount.getOrDefault(resource, 0);
    }

    public enum ResourceType {
        WATER, IRON, GOLD
    }
}