package game.managers;

import java.util.HashMap;
import java.util.Map;

public class ResourceManager {

    private final Map<RESOURCE_TYPE, Integer> resourceCount;

    public ResourceManager() {
        resourceCount = new HashMap<>();
        for (RESOURCE_TYPE resource : RESOURCE_TYPE.values()) {
            resourceCount.put(resource, 0);
        }
    }

    public void gatherResource(RESOURCE_TYPE resource, Integer amount) {

        int newAmount = resourceCount.get(resource) + amount;
        resourceCount.put(resource, newAmount);
    }

    public Integer amountOf(RESOURCE_TYPE resource) {
        return resourceCount.get(resource);
    }

    public void setAmount(RESOURCE_TYPE resource, Integer amount) {
        resourceCount.put(resource, amount);
    }

    public enum RESOURCE_TYPE {
        WATER, IRON, GOLD
    }

}

