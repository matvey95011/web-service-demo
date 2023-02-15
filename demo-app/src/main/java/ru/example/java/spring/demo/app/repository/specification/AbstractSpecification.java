package ru.example.java.spring.demo.app.repository.specification;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class AbstractSpecification<T> implements Specification<T> {

    @Override
    public Predicate toPredicate(
            @NotNull Root<T> root,
            @NotNull CriteriaQuery<?> criteriaQuery,
            @NotNull CriteriaBuilder cb
    ) {
        return getPredicates(root, criteriaQuery, cb)
                .filter(Objects::nonNull)
                .reduce(cb::and)
                .orElseGet(cb::conjunction);
    }

    @NotNull
    protected abstract Stream<Predicate> getPredicates(
            @NotNull Root<T> root,
            CriteriaQuery<?> criteriaQuery,
            @NotNull CriteriaBuilder cb
    );

    protected Predicate betweenDates(
            Root<?> root,
            CriteriaBuilder cb,
            String fieldName,
            Pair<LocalDate, LocalDate> dates
    ) {
        if (dates == null) {
            return null;
        }
        LocalDate startDate = dates.getLeft();
        LocalDate endDate = dates.getRight();

        if (startDate == null && endDate == null) {
            return null;
        }

        if (startDate != null && endDate != null) {
            return cb.between(root.get(fieldName).as(LocalDate.class), startDate, endDate);
        }

        return startDate != null
                ? greaterOrEquals(root, cb, fieldName, startDate)
                : lessOrEquals(root, cb, fieldName, endDate);
    }

    protected Predicate betweenBigDecimalRange(
            Root<?> root,
            CriteriaBuilder cb,
            String fieldName,
            Pair<BigDecimal, BigDecimal> range
    ) {
        if (range == null) {
            return null;
        }
        BigDecimal min = range.getLeft();
        BigDecimal max = range.getRight();

        if (min == null && max == null) {
            return null;
        }

        if (min != null && max != null) {
            return cb.between(root.get(fieldName).as(BigDecimal.class), min, max);
        }

        return min != null
                ? cb.greaterThanOrEqualTo(root.get(fieldName).as(BigDecimal.class), min)
                : cb.lessThanOrEqualTo(root.get(fieldName).as(BigDecimal.class), max);

    }

    protected Predicate betweenJoinIntRange(
            Root<?> root,
            CriteriaBuilder cb,
            String joinColumn,
            String fieldName,
            Pair<Integer, Integer> range
    ) {
        if (range == null) {
            return null;
        }
        Integer min = range.getLeft();
        Integer max = range.getRight();

        if (min == null && max == null) {
            return null;
        }

        if (min != null && max != null) {
            return cb.between(root.join(joinColumn).get(fieldName).as(Integer.class), min, max);
        }

        return min != null
                ? cb.greaterThanOrEqualTo(root.join(joinColumn).get(fieldName).as(Integer.class), min)
                : cb.lessThanOrEqualTo(root.join(joinColumn).get(fieldName).as(Integer.class), max);
    }

    private Predicate greaterOrEquals(Root<?> root, CriteriaBuilder cb, String fieldName, LocalDate value) {
        return value != null ? cb.greaterThanOrEqualTo(root.get(fieldName).as(LocalDate.class), value) : null;
    }

    private Predicate lessOrEquals(Root<?> root, CriteriaBuilder cb, String fieldName, LocalDate value) {
        return value != null ? cb.lessThanOrEqualTo(root.get(fieldName).as(LocalDate.class), value) : null;
    }

    @Nullable
    protected Predicate createJoinLike(
            Root<?> root,
            CriteriaBuilder cb,
            String joinColumn,
            String fieldName,
            String value
    ) {
        return value != null
                ? cb.like(cb.upper(root.join(joinColumn).get(fieldName)), "%" + StringUtils.upperCase(value) + "%")
                : null;
    }

    @Nullable
    protected Predicate createJoinEq(
            Root<?> root,
            CriteriaBuilder cb,
            String joinColumn,
            String fieldName,
            Object value
    ) {
        return value != null ? cb.equal(root.join(joinColumn).get(fieldName), value) : null;
    }

    @Nullable
    protected Predicate createEq(Root<?> root, CriteriaBuilder cb, String fieldName, Object value) {
        return value != null ? cb.equal(root.get(fieldName), value) : null;
    }

    protected Predicate createLike(Root<?> root, CriteriaBuilder cb, String fieldName, String value) {
        return value != null ? cb.like(cb.upper(root.get(fieldName)), "%" + StringUtils.upperCase(value) + "%") : null;
    }

    @Nullable
    protected Predicate createJoinFieldIn(
            Root<?> root,
            CriteriaBuilder cb,
            String joinField,
            String fieldName,
            Collection<?> value
    ) {
        Predicate predicate;
        if (value == null) {
            predicate = null;
        } else if (value.isEmpty()) {
            predicate = cb.or(); // always false
        } else {
            predicate = root.join(joinField).get(fieldName).in(value);
        }
        return predicate;
    }

    protected Predicate createIn(Root<?> root, CriteriaBuilder cb, String fieldName, Collection<?> value) {
        Predicate predicate;
        if (value == null) {
            predicate = null;
        } else if (value.isEmpty()) {
            predicate = cb.or(); // always false
        } else {
            predicate = root.get(fieldName).in(value);
        }
        return predicate;
    }

    protected Predicate createJoinIn(Root<?> root, String join, String field, Collection<?> inValues) {
        return inValues != null ? root.join(join).get(field).in(inValues) : null;
    }

    @Nullable
    protected Predicate createJsonEq(
            Root<?> root,
            CriteriaBuilder cb,
            String jsonFieldName,
            String fieldName,
            Object value
    ) {
        return value != null
                ? cb.equal(
                cb.function("jsonb_extract_path_text", String.class, root.get(jsonFieldName), cb.literal(fieldName)),
                value
        )
                : null;
    }

    @Nullable
    protected Predicate createJsonLike(
            Root<?> root,
            CriteriaBuilder cb,
            String jsonFieldName,
            String fieldName,
            String pattern
    ) {
        return pattern != null
                ? cb.like(
                cb.function("jsonb_extract_path_text", String.class, root.get(jsonFieldName), cb.literal(fieldName)),
                pattern
        )
                : null;
    }

    @Nullable
    protected Predicate createGrThanOrEqualTo(
            Root<T> root,
            CriteriaBuilder cb,
            String fieldName,
            LocalDate value
    ) {
        return value != null ? cb.greaterThanOrEqualTo(root.get(fieldName), value) : null;
    }

    @Nullable
    protected Predicate createLessThanOrEqualTo(
            Root<T> root,
            CriteriaBuilder cb,
            String fieldName,
            LocalDate value
    ) {
        return value != null ? cb.lessThanOrEqualTo(root.get(fieldName), value) : null;
    }

    @Nullable
    protected Predicate createGrThanOrEqualTo(
            Root<T> root,
            CriteriaBuilder cb,
            String fieldName,
            LocalDateTime value
    ) {
        return value != null ? cb.greaterThanOrEqualTo(root.get(fieldName), value) : null;
    }

    @Nullable
    protected Predicate createLessThanOrEqualTo(Root<T> root, CriteriaBuilder cb, String fieldName, LocalDateTime value) {
        return value != null ? cb.lessThanOrEqualTo(root.get(fieldName), value) : null;
    }
}
