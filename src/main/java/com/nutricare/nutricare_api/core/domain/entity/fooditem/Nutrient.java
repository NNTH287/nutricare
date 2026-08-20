package com.nutricare.nutricare_api.core.domain.entity.fooditem;

import java.util.Objects;

public class Nutrient {
    private Integer id;
    private String code;
    private String name;
    private String unit;

    public Nutrient(Integer id, String code, String name, String unit) {
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
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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
