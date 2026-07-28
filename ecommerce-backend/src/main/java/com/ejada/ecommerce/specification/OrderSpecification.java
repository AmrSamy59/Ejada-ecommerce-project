package com.ejada.ecommerce.specification;

import com.ejada.ecommerce.entity.Order;
import com.ejada.ecommerce.entity.OrderStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Join;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {

    public static Specification<Order> filterOrders(LocalDateTime dateFrom, LocalDateTime dateTo, String userName, OrderStatus orderStatus) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (dateFrom != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("orderDate"), dateFrom));
            }

            if (dateTo != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("orderDate"), dateTo));
            }

            if (StringUtils.hasText(userName)) {
                Join<Object, Object> userJoin = root.join("user");
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("username")), "%" + userName.toLowerCase() + "%"));
            }

            if (orderStatus != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), orderStatus));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
