package com.project.Ecommerce.specification;

import com.project.Ecommerce.domain.entity.OrderItemEntity;
import com.project.Ecommerce.domain.enums.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class OrderItemSpecification {

    // Specification to filter order item by status
    public static Specification<OrderItemEntity> hasStatus(OrderStatus status) {
        return ((root, query, criteriaBuilder) ->
                status != null ? criteriaBuilder.equal(root.get("status"), status) : null
        );
    }

    // Specification to filter order item by data range
    public static Specification<OrderItemEntity> createdBetween(LocalDateTime start, LocalDateTime end) {
        return ((root, query, criteriaBuilder) -> {
            if (start != null && end != null) {
                return criteriaBuilder.between(root.get("createdAt"), start, end);
            } else if (start != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), start);
            } else if (end != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), end);
            } else {
                return null;
            }
        });
    }

    // Generates specification to filter order item by item id
    public static Specification<OrderItemEntity> hasItemId(Long itemId) {
        return ((root, query, criteriaBuilder) ->
                itemId != null ? criteriaBuilder.equal(root.get("itemId"), itemId) : null
        );
    }
}
