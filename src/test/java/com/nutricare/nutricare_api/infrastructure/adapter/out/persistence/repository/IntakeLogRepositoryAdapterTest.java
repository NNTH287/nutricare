package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeLog;
import com.nutricare.nutricare_api.core.domain.entity.menu.MealSlot;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper.IntakeEntryPersistenceMapperImpl;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper.IntakeLogPersistenceMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({IntakeEntryPersistenceMapperImpl.class, IntakeLogPersistenceMapperImpl.class})
class IntakeLogRepositoryAdapterTest {

    @Autowired
    private IntakeLogJpaRepository intakeLogJpaRepository;

    @Autowired
    private IntakeEntryJpaRepository intakeEntryJpaRepository;

    @Autowired
    private IntakeLogPersistenceMapperImpl intakeLogMapper;

    @Autowired
    private IntakeEntryPersistenceMapperImpl intakeEntryMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private IntakeLogRepositoryAdapter adapter;
    private int profileId;
    private int otherProfileId;
    private int foodItemId;

    @BeforeEach
    void setUp() {
        adapter = new IntakeLogRepositoryAdapter(intakeLogJpaRepository, intakeEntryJpaRepository, intakeLogMapper, intakeEntryMapper);

        int userId = insertAndReturnId(
                "insert into users (email, password_hash, role, created_at, updated_at) "
                        + "values ('owner@example.com', 'hash', 'USER', now(), now())");
        profileId = insertProfile(userId);
        int otherUserId = insertAndReturnId(
                "insert into users (email, password_hash, role, created_at, updated_at) "
                        + "values ('other@example.com', 'hash', 'USER', now(), now())");
        otherProfileId = insertProfile(otherUserId);
        foodItemId = insertAndReturnId(
                "insert into food_item (name, serving_size_g) values ('Rice', 100.0)");
    }

    private int insertProfile(int userId) {
        return insertAndReturnId(
                "insert into profile (user_id, name, group_type, sex_type, birth_date, weight_kg, height_cm, "
                        + "activity_level, created_at, updated_at) "
                        + "values (" + userId + ", 'Jane', 'ADULT', 'FEMALE', '1990-01-01', 60.0, 165.0, "
                        + "'MODERATE', now(), now())");
    }

    private int insertAndReturnId(String sql) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS), keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Test
    void givenLogsForTwoProfiles_whenFindAllByProfileIdIsCalled_thenReturnsOnlyTheRequestedProfilesLogs() {
        adapter.save(IntakeLog.create(profileId, LocalDate.now().minusDays(1)));
        adapter.save(IntakeLog.create(otherProfileId, LocalDate.now().minusDays(1)));

        var headers = adapter.findAllByProfileId(profileId, 0, 10);

        assertThat(headers).hasSize(1);
        assertThat(headers.get(0).profileId()).isEqualTo(profileId);
    }

    @Test
    void givenPersistedLogWithEntry_whenFindByIdIsCalled_thenReturnsLogWithItsEntry() {
        IntakeLog log = IntakeLog.create(profileId, LocalDate.now().minusDays(1));
        log.addEntry(foodItemId, 100.0, MealSlot.BREAKFAST);
        IntakeLog saved = adapter.save(log);

        Optional<IntakeLog> found = adapter.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getEntries()).hasSize(1);
        assertThat(found.get().getEntries().get(0).getFoodItemId()).isEqualTo(foodItemId);
    }

    @Test
    void givenLogWithEntryRemovedAndResaved_whenFindByIdIsCalled_thenTheRemovedEntryIsGoneFromTheDatabase() {
        IntakeLog log = IntakeLog.create(profileId, LocalDate.now().minusDays(1));
        var entry = log.addEntry(foodItemId, 100.0, MealSlot.BREAKFAST);
        IntakeLog saved = adapter.save(log);
        IntakeLog reloaded = adapter.findById(saved.getId()).orElseThrow();

        reloaded.removeEntry(reloaded.getEntries().get(0).getId());
        adapter.save(reloaded);

        // orphanRemoval=true on IntakeLogJpaEntity.entries must actually delete the child row,
        // not just detach it from the in-memory collection.
        assertThat(intakeEntryJpaRepository.findAll()).isEmpty();
    }

    @Test
    void givenPersistedLog_whenFindByProfileIdAndDateIsCalled_thenReturnsMatchingLog() {
        LocalDate date = LocalDate.now().minusDays(1);
        adapter.save(IntakeLog.create(profileId, date));

        Optional<IntakeLog> found = adapter.findByProfileIdAndDate(profileId, date);

        assertThat(found).isPresent();
    }

    @Test
    void givenPersistedLog_whenDeleteByIdIsCalled_thenItAndItsEntriesAreRemoved() {
        IntakeLog log = IntakeLog.create(profileId, LocalDate.now().minusDays(1));
        log.addEntry(foodItemId, 100.0, MealSlot.BREAKFAST);
        IntakeLog saved = adapter.save(log);

        adapter.deleteById(saved.getId());

        assertThat(adapter.findById(saved.getId())).isEmpty();
        assertThat(intakeEntryJpaRepository.findAll()).isEmpty();
    }

    @Test
    void givenPersistedEntry_whenFindEntryByIdIsCalled_thenReturnsMatchingEntry() {
        IntakeLog log = IntakeLog.create(profileId, LocalDate.now().minusDays(1));
        log.addEntry(foodItemId, 100.0, MealSlot.BREAKFAST);
        IntakeLog saved = adapter.save(log);
        int entryId = saved.getEntries().get(0).getId();

        Optional<com.nutricare.nutricare_api.core.domain.entity.intake.IntakeEntry> found = adapter.findEntryById(entryId);

        assertThat(found).isPresent();
        assertThat(found.get().getFoodItemId()).isEqualTo(foodItemId);
    }
}
