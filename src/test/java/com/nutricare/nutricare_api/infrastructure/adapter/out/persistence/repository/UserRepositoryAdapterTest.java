package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.core.domain.entity.user.Role;
import com.nutricare.nutricare_api.core.domain.entity.user.User;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper.UserPersistenceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class UserRepositoryAdapterTest {

    @Autowired
    private UserJpaRepository jpaRepository;

    private UserRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UserRepositoryAdapter(jpaRepository, new UserPersistenceMapper() {});
    }

    @Test
    void givenNewUser_whenSaveIsCalled_thenUserIsPersistedAndAssignedAnId() {
        User user = User.register("user@example.com", "hashed-password", Role.USER);

        User saved = adapter.save(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void givenPersistedUser_whenFindByEmailIsCalled_thenReturnsMatchingUser() {
        adapter.save(User.register("user@example.com", "hashed-password", Role.USER));

        Optional<User> found = adapter.findByEmail("user@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void givenNoUserWithGivenEmail_whenFindByEmailIsCalled_thenReturnsEmpty() {
        Optional<User> found = adapter.findByEmail("missing@example.com");

        assertThat(found).isEmpty();
    }

    @Test
    void givenPersistedUser_whenExistsByEmailIsCalled_thenReturnsTrue() {
        adapter.save(User.register("user@example.com", "hashed-password", Role.USER));

        assertThat(adapter.existsByEmail("user@example.com")).isTrue();
    }

    @Test
    void givenNoUserWithGivenEmail_whenExistsByEmailIsCalled_thenReturnsFalse() {
        assertThat(adapter.existsByEmail("missing@example.com")).isFalse();
    }

    @Test
    void givenEmailAlreadyPersisted_whenSavingAnotherUserWithSameEmail_thenThrowsDataIntegrityViolationException() {
        adapter.save(User.register("user@example.com", "hashed-password", Role.USER));
        User duplicate = User.register("user@example.com", "another-hash", Role.USER);

        assertThatThrownBy(() -> adapter.save(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
