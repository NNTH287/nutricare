package com.nutricare.nutricare_api.core.domain.entity.user;

import java.util.Objects;
import java.util.regex.Pattern;

public final class Email {
    private static final Pattern FORMAT = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final String value;

    public Email(String value) {
        if (value == null || !FORMAT.matcher(value).matches()) {
            throw new InvalidEmailException("Email must be a well-formed address: " + value);
        }
        this.value = value.toLowerCase();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Email email)) return false;
        return value.equals(email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
