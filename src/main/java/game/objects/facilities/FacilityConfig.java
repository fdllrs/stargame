package game.objects.facilities;

import game.items.ItemType;
import game.items.RawResource;

import java.util.Map;

public class FacilityConfig {
    public static final Map<String, Map<ItemType, Integer>> COSTS = Map.of("Extractor",
                                                                           Map.of(RawResource.METAL,
                                                                                  50),
                                                                           "Storage Silo",
                                                                           Map.of(RawResource.METAL,
                                                                                  100),
                                                                           "Alloy " +
                                                                           "Smelter",
                                                                           Map.of(RawResource.METAL,
                                                                                  150)

                                                                          );
}