package tn.esprit.jappa.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventManager {
    private static EventManager instance;
    private final List<Consumer<String>> subscribers;

    private EventManager() {
        subscribers = new ArrayList<>();
    }

    public static EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }

    public void subscribe(Consumer<String> subscriber) {
        subscribers.add(subscriber);
    }

    public void notify(String event) {
        subscribers.forEach(subscriber -> subscriber.accept(event));
    }

    public void unsubscribe(Consumer<String> subscriber) {
        subscribers.remove(subscriber);
    }
} 