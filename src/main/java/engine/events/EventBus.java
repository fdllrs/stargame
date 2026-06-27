package engine.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventBus {
	private static final Map<Class<? extends Event>, List<Consumer<Event>>> listeners =
			new HashMap<>();

	public static void clear() {
		listeners.clear();
	}

	public static void publish(Event event) {
		List<Consumer<Event>> handlers = listeners.get(event.getClass());
		if (handlers != null) {
			for (Consumer<Event> handler : handlers) {
				handler.accept(event);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Event> void subscribe(Class<T> eventType, Consumer<T> listener) {
		listeners.computeIfAbsent(eventType, _ -> new ArrayList<>())
				 .add(e -> listener.accept((T) e));
	}
}
