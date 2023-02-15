package ru.example.java.spring.demo.app.utils;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class CommonSort {

    @NotNull
    public static Sort getSort(@NotNull List<String> sorts) {
        List<Sort.Order> orders = new ArrayList<>();
        for (String sort : sorts) {
            String[] split = sort.split("\\|");
            orders.add(new Sort.Order(Sort.Direction.fromString(split[1]), split[0]));
        }
        return Sort.by(orders);
    }

    @NotNull
    public static Sort getOrderSort(List<String> requestSort, @NotNull List<String> defaultSort) {
        return CollectionUtils.isEmpty(requestSort) ? getSort(defaultSort) : getSort(requestSort);
    }

}
