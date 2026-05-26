package game.managers;

import java.util.HashMap;
import java.util.Map;

public class ResourceManager {

    private final Map<RESOURCE_TYPE, Double> resourceCount;


    public ResourceManager() {
        resourceCount = new HashMap<>();
        for (RESOURCE_TYPE resouce : RESOURCE_TYPE.values()) {
            resourceCount.put(resouce, 0.0);
        }
    }

    public void gatherResource(RESOURCE_TYPE resource, Double amount) {

        double newAmount = resourceCount.get(resource) + amount;
        resourceCount.put(resource, newAmount);
    }

    public enum RESOURCE_TYPE {
        WATER,
        IRON,
        GOLD
    }


}

