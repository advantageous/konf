package io.advantageous.config;

import java.util.*;

import static java.lang.Long.parseLong;

public enum MemorySizeUnit {

    BYTES(1, "B", "b", "byte", "bytes"),
    KILO_BYTES(1_000, "kB", "kilobyte", "kilobytes"),
    MEGA_BYTES(1_000_000, "MB", "megabyte", "megabytes"),
    GIGA_BYTES(1_000_000_000, "GB", "gigabyte", "gigabytes"),
    TERA_BYTES(1_000_000_000, "TB", "terabyte", "terabytes"),
    PETA_BYTES(1_000_000_000_000L, "PB", "petabyte", "petabytes"),
    EXA_BYTES(1_000_000_000_000_000L, "EB", "exabyte", "exabytes"),
    ZETTA_BYTES(1_000_000_000_000_000_000L, "ZB", "zettabyte", "zettabytes");

    final static Map<String, MemorySizeUnit> sizeNameToSizeMap;

    static {

        final Map<String, MemorySizeUnit> map = new HashMap<>();
        Arrays.stream(MemorySizeUnit.values()).forEachOrdered(size ->
                size.getSizeNames()
                        .forEach(sizeKey ->
                                map.put(sizeKey, size)
                        )
        );
        sizeNameToSizeMap = Collections.unmodifiableMap(map);
    }

    private final Set<String> sizes;
    private final long multiplier;

    /**
     */
    MemorySizeUnit(final long aMultiplier, final String... aSizes) {
        multiplier = aMultiplier;
        final LinkedHashSet<String> strings = new LinkedHashSet<>(Arrays.asList(aSizes));
        sizes = Collections.unmodifiableSet(strings);
    }

    public static long parse(String value) {
        return parseToConfigMemorySize(value).toBytes();

    }


    public static ConfigMemorySize parseToConfigMemorySize(String value) {
        Objects.requireNonNull(value, "value Must not be null");

        value = value.trim();
        if (value.length() == 0) {
            throw new IllegalArgumentException("Value must not be null");
        }

        int indexOfFirstNonNumber = -1;
        for (int index = 0; index < value.length(); index++) {
            final char c = value.charAt(index);
            if (!Character.isDigit(c)) {
                indexOfFirstNonNumber = index;
                break;
            }
        }

        final String sizeTypeStr = indexOfFirstNonNumber != -1 ? value.substring(indexOfFirstNonNumber) : "b";
        final String numberString = value.substring(0, indexOfFirstNonNumber).trim();
        final MemorySizeUnit sizeUnit = getSize(sizeTypeStr.length() > 0 ? sizeTypeStr : "b");

        if (sizeUnit == null) {
            throw new IllegalArgumentException("Cannot parse size " + value);
        }

        return new ConfigMemorySize(sizeUnit, parseLong(numberString));

    }

    private static MemorySizeUnit getSize(final String sizeTypeStr) {
        return sizeNameToSizeMap.get(sizeTypeStr.trim());
    }

    public Set<String> getSizeNames() {
        return sizes;
    }

    public long getMultiplier() {
        return multiplier;
    }

    public long toLong(long units) {
        return this.multiplier * units;
    }
}
