package com.nutricare.nutricare_api.core.domain.entity.fooditem;

import java.util.Objects;

public class Nutrient {
    private Integer id;
    private String code;
    private String name;
    private String unit;

    public static Nutrient create(String code, String name, String unit) {
        return new Nutrient(null, code, name, unit);
    }

    public static Nutrient reconstitute(Integer id, String code, String name, String unit) {
        return new Nutrient(id, code, name, unit);
    }

    private Nutrient(Integer id, String code, String name, String unit) {
        validateCode(code);
        validateName(name);
        validateUnit(unit);
        this.id = id;
        this.code = code;
        this.name = name;
        this.unit = unit;
    }

    public Integer getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        validateCode(code);
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        validateUnit(unit);
        this.unit = unit;
    }

    private static void validateCode(String code) {
        if (code == null || code.isBlank()) {
            throw new InvalidNutrientException("code is required");
        }
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidNutrientException("name is required");
        }
    }

    private static void validateUnit(String unit) {
        if (unit == null || unit.isBlank()) {
            throw new InvalidNutrientException("unit is required");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Nutrient that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hashCode(id) : System.identityHashCode(this);
    }
}
