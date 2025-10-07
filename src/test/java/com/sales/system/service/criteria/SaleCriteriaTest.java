package com.sales.system.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SaleCriteriaTest {

    @Test
    void newSaleCriteriaHasAllFiltersNullTest() {
        var saleCriteria = new SaleCriteria();
        assertThat(saleCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void saleCriteriaFluentMethodsCreatesFiltersTest() {
        var saleCriteria = new SaleCriteria();

        setAllFilters(saleCriteria);

        assertThat(saleCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void saleCriteriaCopyCreatesNullFilterTest() {
        var saleCriteria = new SaleCriteria();
        var copy = saleCriteria.copy();

        assertThat(saleCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(saleCriteria)
        );
    }

    @Test
    void saleCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var saleCriteria = new SaleCriteria();
        setAllFilters(saleCriteria);

        var copy = saleCriteria.copy();

        assertThat(saleCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(saleCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var saleCriteria = new SaleCriteria();

        assertThat(saleCriteria).hasToString("SaleCriteria{}");
    }

    private static void setAllFilters(SaleCriteria saleCriteria) {
        saleCriteria.id();
        saleCriteria.creationDate();
        saleCriteria.total();
        saleCriteria.transactionsId();
        saleCriteria.clientId();
        saleCriteria.sellerId();
        saleCriteria.distinct();
    }

    private static Condition<SaleCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCreationDate()) &&
                condition.apply(criteria.getTotal()) &&
                condition.apply(criteria.getTransactionsId()) &&
                condition.apply(criteria.getClientId()) &&
                condition.apply(criteria.getSellerId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SaleCriteria> copyFiltersAre(SaleCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCreationDate(), copy.getCreationDate()) &&
                condition.apply(criteria.getTotal(), copy.getTotal()) &&
                condition.apply(criteria.getTransactionsId(), copy.getTransactionsId()) &&
                condition.apply(criteria.getClientId(), copy.getClientId()) &&
                condition.apply(criteria.getSellerId(), copy.getSellerId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
