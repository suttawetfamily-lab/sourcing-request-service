package com.pantavanij.sourcingreq.services.enums;

public enum VatType {
    INCLUDED_VAT(1, "Included VAT"),
    EXCLUDED_VAT(2, "Excluded VAT");

    private final Integer value;
    private final String description;

    VatType(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    public Integer value() {
        return this.value;
    }

    public String description() {
        return this.description;
    }

    public static String getDescriptionByValue(Integer value) {
        for (VatType vatType : VatType.values()) {
            if (vatType.value.equals(value)) {
                return vatType.description();
            }
        }
        throw new IllegalArgumentException("No matching VAT type for value: " + value);
    }
}
