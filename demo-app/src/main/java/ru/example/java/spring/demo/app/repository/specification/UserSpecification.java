package ru.example.java.spring.demo.app.repository.specification;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ru.example.java.spring.demo.app.entity.Profile_;
import ru.example.java.spring.demo.app.entity.User;
import ru.example.java.spring.demo.app.entity.User_;
import ru.example.java.spring.demo.model.UserCriteriaFilter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class UserSpecification extends AbstractSpecification<User> {

    private final UserCriteriaFilter filter;

    @Override
    protected @NotNull Stream<Predicate> getPredicates(
            @NotNull Root<User> root, CriteriaQuery<?> criteriaQuery, @NotNull CriteriaBuilder cb) {
        return Stream.of(
                createLike(root, cb, User_.NAME, filter.getName()),
                createEq(root, cb, User_.AGE, filter.getAge()),
                createLike(root, cb, User_.EMAIL, filter.getEmail()),
                createJoinIn(root, User_.PROFILE, Profile_.ID, filter.getProfileIds())
        );
    }
}
