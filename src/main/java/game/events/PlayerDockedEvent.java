package game.events;

import engine.events.Event;
import game.objects.spaceBodies.Planet;

public record PlayerDockedEvent(Planet planet) implements Event {

}
