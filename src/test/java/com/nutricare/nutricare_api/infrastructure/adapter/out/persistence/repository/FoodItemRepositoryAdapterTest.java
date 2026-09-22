package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.core.domain.entity.fooditem.FoodItem;
import com.nutricare.nutricare_api.core.domain.entity.fooditem.NutrientAmount;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper.FoodItemPersistenceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Statement;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class FoodItemRepositoryAdapterTest {

    @Autowired
    private FoodItemJpaRepository jpaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private FoodItemRepositoryAdapter adapter;
    private int nutrientId;

    @BeforeEach
    void setUp() {
        adapter = new FoodItemRepositoryAdapter(jpaRepository, new FoodItemPersistenceMapper() {});
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> connection.prepareStatement(
                "insert into nutrient (code, name, unit) values ('protein', 'Protein', 'g')",
                Statement.RETURN_GENERATED_KEYS), keyHolder);
        nutrientId = keyHolder.getKey().intValue();
    }

    @Test
    void givenFoodItemWithTagsAndNutrients_whenSaveThenFindByIdIsCalled_thenReturnsEquivalentDomainItem() {
        FoodItem item = FoodItem.create("Rice", "Grain", 100.0, Set.of("staple"), null,
                Set.of(new NutrientAmount(nutrientId, 7.5)));

        FoodItem saved = adapter.save(item);
        Optional<FoodItem> found = adapter.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Rice");
        assertThat(found.get().getTags()).containsExactly("staple");
        assertThat(found.get().getNutrients()).hasSize(1);
        assertThat(found.get().getNutrients().iterator().next().getNutrientId()).isEqualTo(nutrientId);
    }

    @Test
    void givenNoFoodItemWithGivenId_whenFindByIdIsCalled_thenReturnsEmpty() {
        Optional<FoodItem> found = adapter.findById(-1);

        assertThat(found).isEmpty();
    }

    @Test
    void givenPersistedFoodItem_whenDeleteByIdIsCalled_thenItIsNoLongerFound() {
        FoodItem item = FoodItem.create("Rice", "Grain", 100.0, Set.of(), null, Set.of());
        FoodItem saved = adapter.save(item);

        adapter.deleteById(saved.getId());

        assertThat(adapter.findById(saved.getId())).isEmpty();
    }

    @Test
    void givenMultiplePersistedFoodItems_whenFindAllByIdsIsCalled_thenReturnsOnlyMatchingItems() {
        FoodItem rice = adapter.save(FoodItem.create("Rice", "Grain", 100.0, Set.of(), null, Set.of()));
        FoodItem bread = adapter.save(FoodItem.create("Bread", "Grain", 50.0, Set.of(), null, Set.of()));
        adapter.save(FoodItem.create("Milk", "Dairy", 200.0, Set.of(), null, Set.of()));

        var found = adapter.findAllByIds(Set.of(rice.getId(), bread.getId()));

        assertThat(found).extracting(FoodItem::getName).containsExactlyInAnyOrder("Rice", "Bread");
    }
}
