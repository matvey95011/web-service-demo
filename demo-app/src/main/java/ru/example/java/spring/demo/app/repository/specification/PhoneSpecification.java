package ru.example.java.spring.demo.app.repository.specification;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import ru.example.java.spring.demo.app.entity.Phone;
import ru.example.java.spring.demo.app.entity.Phone_;
import ru.example.java.spring.demo.app.entity.User_;
import ru.example.java.spring.demo.model.PhoneCriteriaFilter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class PhoneSpecification extends AbstractSpecification<Phone> {

    private final PhoneCriteriaFilter filter;

    @Override
    protected @NotNull Stream<Predicate> getPredicates(
            @NotNull Root<Phone> root, CriteriaQuery<?> criteriaQuery, @NotNull CriteriaBuilder cb) {
        return Stream.of(
                createEq(root, cb, Phone_.ID, filter.getId()),
                createEq(root, cb, Phone_.VALUE, filter.getValue()),
                createLike(root, cb, Phone_.VALUE, filter.getLikeValue()),

                createJoinIn(root, Phone_.USER, User_.ID, filter.getUserIds()),
                createJoinIn(root, Phone_.USER, User_.NAME, filter.getUserNames()),
                createJoinIn(root, Phone_.USER, User_.EMAIL, filter.getUserEmails()),

                createJoinLike(root, cb, Phone_.USER, User_.ID, filter.getLikeUserName()),
                createJoinLike(root, cb, Phone_.USER, User_.ID, filter.getLikeUserEmail()),

                betweenJoinIntRange(root, cb, Phone_.USER, User_.AGE,
                        Pair.of(filter.getMinUserAge(), filter.getMaxUserAge())),
                createJoinEq(root, cb, Phone_.USER, User_.AGE, filter.getUserAge()),

                createLike(root, cb, Phone_.VALUE, filter.getValue())
        );
    }
}
