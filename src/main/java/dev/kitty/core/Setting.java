package dev.kitty.core;

import java.util.Objects;

public abstract class Setting<T> {
    private final String name;
    private final T defaultValue;
    private T value;

    protected Setting(String name, T defaultValue) {
        this.name = Objects.requireNonNull(name);
        this.defaultValue = Objects.requireNonNull(defaultValue);
        this.value = defaultValue;
    }

    public String name() { return name; }
    public T get() { return value; }
    public T defaultValue() { return defaultValue; }

    public void set(T newValue) { this.value = sanitize(Objects.requireNonNull(newValue)); }

    protected abstract T sanitize(T candidate);
    public abstract String serialize();
    public abstract void deserialize(String text);

    public static final class Bool extends Setting<Boolean> {
        public Bool(String name, boolean def) { super(name, def); }
        @Override protected Boolean sanitize(Boolean c) { return c; }
        @Override public String serialize() { return Boolean.toString(get()); }
        @Override public void deserialize(String text) { set(Boolean.parseBoolean(text.trim())); }
    }

    public static final class Number extends Setting<Double> {
        private final double min, max;
        public Number(String name, double def, double min, double max) {
            super(name, clamp(def, min, max));
            if (!(min <= max)) throw new IllegalArgumentException("min > max");
            this.min = min; this.max = max;
        }
        private static double clamp(double v, double lo, double hi) {
            if (Double.isNaN(v)) return lo;
            return Math.max(lo, Math.min(hi, v));
        }
        @Override protected Double sanitize(Double c) { return clamp(c, min, max); }
        @Override public String serialize() { return Double.toString(get()); }
        @Override public void deserialize(String text) {
            try { set(Double.parseDouble(text.trim())); } catch (NumberFormatException ignored) { /* keep current */ }
        }
        public double min() { return min; }
        public double max() { return max; }
    }
}
