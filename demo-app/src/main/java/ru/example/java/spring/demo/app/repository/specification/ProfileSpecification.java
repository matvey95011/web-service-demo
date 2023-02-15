package ru.example.java.spring.demo.app.repository.specification;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import ru.example.java.spring.demo.app.entity.Profile;
import ru.example.java.spring.demo.app.entity.Profile_;
import ru.example.java.spring.demo.app.entity.User_;
import ru.example.java.spring.demo.model.ProfileCriteriaFilter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class ProfileSpecification extends AbstractSpecification<Profile> {

    private final ProfileCriteriaFilter filter;

    @Override
    protected @NotNull Stream<Predicate> getPredicates(@NotNull Root<Profile> root, CriteriaQuery<?> criteriaQuery, @NotNull CriteriaBuilder cb) {
        return Stream.of(
                createEq(root, cb, Profile_.CASH, filter.getCash()),
                betweenBigDecimalRange(root, cb, Profile_.CASH,
                        Pair.of(filter.getMinCash(), filter.getMaxCash())),
                createJoinEq(root, cb, Profile_.USER, User_.ID, filter.getUserId()),
                createJoinEq(root, cb, Profile_.USER, User_.NAME, filter.getUserName()),
                createJoinEq(root, cb, Profile_.USER, User_.EMAIL, filter.getUserEmail()),
                createJoinIn(root, Profile_.USER, User_.ID, filter.getUserIds()),
                createJoinIn(root, Profile_.USER, User_.NAME, filter.getUserNames()),
                createJoinIn(root, Profile_.USER, User_.EMAIL, filter.getUserEmails())
        );
    }
}
