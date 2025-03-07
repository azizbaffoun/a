package tn.esprit.pidev.models;

import java.util.HashMap;
import java.util.Map;

public class Event {
    private String name;
    private Map<String, String> params;

    private Event(Builder builder) {
        this.name = builder.name;
        this.params = builder.params;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getParamsMap() {
        return params;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private Map<String, String> params = new HashMap<>();

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder putParams(String key, String value) {
            this.params.put(key, value);
            return this;
        }

        public Event build() {
            return new Event(this);
        }
    }
} 