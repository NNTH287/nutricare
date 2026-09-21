package com.nutricare.nutricare_api.core.domain.entity.user;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {
    private Integer id;
    private Email email;
    private String passwordHash;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static User register(String email, String passwordHash, Role role) {
        LocalDateTime now = LocalDateTime.now();
        return new User(null, new Email(email), passwordHash, role, now, now);
    }

    public static User reconstitute(Integer id, String email, String passwordHash, Role role,
                                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new User(id, new Email(email), passwordHash, role, createdAt, updatedAt);
    }

    private User(Integer id, Email email, String passwordHash, Role role,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new InvalidUserException("passwordHash is required");
        }
        if (role == null) {
            throw new InvalidUserException("role is required");
        }
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email.getValue();
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        if (id == null || user.id == null) return false;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hashCode(id) : System.identityHashCode(this);
    }
}
