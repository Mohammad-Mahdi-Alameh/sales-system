package com.sales.system.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SellerCriteriaTest {

    @Test
    void newSellerCriteriaHasAllFiltersNullTest() {
        var sellerCriteria = new SellerCriteria();
        assertThat(sellerCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void sellerCriteriaFluentMethodsCreatesFiltersTest() {
        var sellerCriteria = new SellerCriteria();

        setAllFilters(sellerCriteria);

        assertThat(sellerCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void sellerCriteriaCopyCreatesNullFilterTest() {
        var sellerCriteria = new SellerCriteria();
        var copy = sellerCriteria.copy();

        assertThat(sellerCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(sellerCriteria)
        );
    }

    @Test
    void sellerCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var sellerCriteria = new SellerCriteria();
        setAllFilters(sellerCriteria);

        var copy = sellerCriteria.copy();

        assertThat(sellerCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(sellerCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var sellerCriteria = new SellerCriteria();

        assertThat(sellerCriteria).hasToString("SellerCriteria{}");
    }

    private static void setAllFilters(SellerCriteria sellerCriteria) {
        sellerCriteria.id();
        sellerCriteria.name();
        sellerCriteria.lastName();
        sellerCriteria.employeeCode();
        sellerCriteria.distinct();
    }

    private static Condition<SellerCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getLastName()) &&
                condition.apply(criteria.getEmployeeCode()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SellerCriteria> copyFiltersAre(SellerCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getLastName(), copy.getLastName()) &&
                condition.apply(criteria.getEmployeeCode(), copy.getEmployeeCode()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
