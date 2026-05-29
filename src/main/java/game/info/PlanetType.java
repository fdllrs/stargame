package game.info;

import game.items.RawResource;

import java.util.List;

public enum PlanetType {
    ROCKY(List.of(RawResource.METAL)),
    GAS_GIANT(List.of(RawResource.HYDROGEN)),
    ICE_GIANT(List.of(RawResource.WATER, RawResource.HYDROGEN)),
    ORGANIC(List.of(RawResource.ORGANICS, RawResource.METAL));
    private final List<RawResource> harvestableResources;

    PlanetType(List<RawResource> harvestableResources) {
        this.harvestableResources = harvestableResources;
    }

    public List<RawResource> getHarvestableResources() {
        return harvestableResources;
    }
}