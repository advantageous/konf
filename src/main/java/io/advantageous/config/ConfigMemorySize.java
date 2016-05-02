package io.advantageous.config;

/**
 * An immutable class representing an amount of memory.
 */
public class ConfigMemorySize {

    private final MemorySizeUnit memorySizeUnit;
    private final long amount;

    ConfigMemorySize(MemorySizeUnit memorySizeUnit, long amount) {
        this.memorySizeUnit = memorySizeUnit;
        this.amount = amount;
    }

    /**
     * Parse the value and returns a ConfigMemorySize
     *
     * @param value value of string
     * @return value of ConfigMemorySize
     */
    public static ConfigMemorySize valueOf(final String value) {
        return MemorySizeUnit.parseToConfigMemorySize(value);
    }

    public long toBytes() {
        return memorySizeUnit.toLong(amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigMemorySize that = (ConfigMemorySize) o;

        if (amount != that.amount) return false;
        return memorySizeUnit == that.memorySizeUnit;

    }

    @Override
    public int hashCode() {
        int result = memorySizeUnit != null ? memorySizeUnit.hashCode() : 0;
        result = 31 * result + (int) (amount ^ (amount >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "ConfigMemorySize{" +
                "memorySizeUnit=" + memorySizeUnit +
                ", amount=" + amount +
                '}';
    }
}
