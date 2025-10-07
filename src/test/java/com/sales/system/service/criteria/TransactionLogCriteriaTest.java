package com.sales.system.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TransactionLogCriteriaTest {

    @Test
    void newTransactionLogCriteriaHasAllFiltersNullTest() {
        var transactionLogCriteria = new TransactionLogCriteria();
        assertThat(transactionLogCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void transactionLogCriteriaFluentMethodsCreatesFiltersTest() {
        var transactionLogCriteria = new TransactionLogCriteria();

        setAllFilters(transactionLogCriteria);

        assertThat(transactionLogCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void transactionLogCriteriaCopyCreatesNullFilterTest() {
        var transactionLogCriteria = new TransactionLogCriteria();
        var copy = transactionLogCriteria.copy();

        assertThat(transactionLogCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(transactionLogCriteria)
        );
    }

    @Test
    void transactionLogCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var transactionLogCriteria = new TransactionLogCriteria();
        setAllFilters(transactionLogCriteria);

        var copy = transactionLogCriteria.copy();

        assertThat(transactionLogCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(transactionLogCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var transactionLogCriteria = new TransactionLogCriteria();

        assertThat(transactionLogCriteria).hasToString("TransactionLogCriteria{}");
    }

    private static void setAllFilters(TransactionLogCriteria transactionLogCriteria) {
        transactionLogCriteria.id();
        transactionLogCriteria.timestamp();
        transactionLogCriteria.operationType();
        transactionLogCriteria.fieldChanged();
        transactionLogCriteria.oldValue();
        transactionLogCriteria.newValue();
        transactionLogCriteria.modifiedBy();
        transactionLogCriteria.saleTransactionId();
        transactionLogCriteria.distinct();
    }

    private static Condition<TransactionLogCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTimestamp()) &&
                condition.apply(criteria.getOperationType()) &&
                condition.apply(criteria.getFieldChanged()) &&
                condition.apply(criteria.getOldValue()) &&
                condition.apply(criteria.getNewValue()) &&
                condition.apply(criteria.getModifiedBy()) &&
                condition.apply(criteria.getSaleTransactionId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TransactionLogCriteria> copyFiltersAre(
        TransactionLogCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTimestamp(), copy.getTimestamp()) &&
                condition.apply(criteria.getOperationType(), copy.getOperationType()) &&
                condition.apply(criteria.getFieldChanged(), copy.getFieldChanged()) &&
                condition.apply(criteria.getOldValue(), copy.getOldValue()) &&
                condition.apply(criteria.getNewValue(), copy.getNewValue()) &&
                condition.apply(criteria.getModifiedBy(), copy.getModifiedBy()) &&
                condition.apply(criteria.getSaleTransactionId(), copy.getSaleTransactionId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
