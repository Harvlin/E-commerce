package com.project.Ecommerce.domain.dto.request;

import lombok.Data;

@Data
public class OrderItemRequest {

    private Long productId;
    private int quantity;
}
