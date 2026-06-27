package game.events;

import engine.events.Event;

public record MapToggledEvent(boolean open) implements Event {

}
