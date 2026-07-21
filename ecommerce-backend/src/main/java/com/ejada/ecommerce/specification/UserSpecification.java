package com.ejada.ecommerce.specification;

import com.ejada.ecommerce.entity.User;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> filterUsers(String name, String email, Boolean isActive) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.trim().isEmpty()) {
                String searchPattern = "%" + name.trim().toLowerCase() + "%";
                Predicate usernameMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), searchPattern);
                Expression<String> fullName = criteriaBuilder.concat(
                        criteriaBuilder.concat(root.get("firstName"), " "),
                        root.get("lastName")
                );
                Predicate fullNameMatch = criteriaBuilder.like(criteriaBuilder.lower(fullName), searchPattern);
                predicates.add(criteriaBuilder.or(usernameMatch, fullNameMatch));
            }

            if (email != null && !email.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")),
                        "%" + email.trim().toLowerCase() + "%"
                ));
            }

            if (isActive != null) {
                predicates.add(criteriaBuilder.equal(root.get("isActive"), isActive));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
