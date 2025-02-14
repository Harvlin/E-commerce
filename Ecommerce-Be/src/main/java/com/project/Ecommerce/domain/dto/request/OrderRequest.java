package com.project.Ecommerce.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.Ecommerce.domain.entity.PaymentEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderRequest {

    private BigDecimal totalPrice;
    private List<OrderItemRequest> items;
    private PaymentEntity paymentInfo;
}