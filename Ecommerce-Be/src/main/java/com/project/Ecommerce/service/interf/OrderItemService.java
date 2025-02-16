package com.project.Ecommerce.service.interf;

import com.project.Ecommerce.domain.dto.request.OrderRequest;
import com.project.Ecommerce.domain.dto.response.Response;
import com.project.Ecommerce.domain.enums.OrderStatus;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface OrderItemService {
    Response placeOrder(OrderRequest order);
    Response updateOrderItemStatus(Long itemId, OrderStatus status);
    Response filterOrderItems(OrderStatus orderStatus, LocalDateTime startDate, LocalDateTime endDate, Long itemId, Pageable pageable);
}
