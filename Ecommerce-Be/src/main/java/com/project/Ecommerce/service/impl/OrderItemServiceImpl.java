package com.project.Ecommerce.service.impl;

import com.project.Ecommerce.domain.dto.OrderItemDto;
import com.project.Ecommerce.domain.dto.request.OrderRequest;
import com.project.Ecommerce.domain.dto.response.Response;
import com.project.Ecommerce.domain.entity.OrderEntity;
import com.project.Ecommerce.domain.entity.OrderItemEntity;
import com.project.Ecommerce.domain.entity.ProductEntity;
import com.project.Ecommerce.domain.entity.UserEntity;
import com.project.Ecommerce.domain.enums.OrderStatus;
import com.project.Ecommerce.exception.NotFoundException;
import com.project.Ecommerce.mapper.EntityDtoMapper;
import com.project.Ecommerce.repository.OrderItemRepository;
import com.project.Ecommerce.repository.OrderRepository;
import com.project.Ecommerce.repository.ProductRepository;
import com.project.Ecommerce.service.interf.OrderItemService;
import com.project.Ecommerce.service.interf.UserService;
import com.project.Ecommerce.specification.OrderItemSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final EntityDtoMapper entityDtoMapper;

    @Override
    public Response placeOrder(OrderRequest orderRequest) {
        UserEntity user = userService.getLoggedInUser();
        List<OrderItemEntity> orderItemEntities = orderRequest.getItems().stream().map(orderItemRequest -> {
            ProductEntity productEntity = productRepository.findById(orderItemRequest.getProductId()).orElseThrow(
                    () -> new NotFoundException("Product not found")
            );

            OrderItemEntity orderItemEntity = new OrderItemEntity();
            orderItemEntity.setProduct(productEntity);
            orderItemEntity.setQuantity(orderItemRequest.getQuantity());
            orderItemEntity.setPrice(productEntity.getPrice().multiply(BigDecimal.valueOf(orderItemRequest.getQuantity())));
            orderItemEntity.setStatus(OrderStatus.PENDING);
            orderItemEntity.setUser(user);
            return orderItemEntity;
        }).collect(Collectors.toList());

        BigDecimal totalPrice = orderRequest.getTotalPrice() != null && orderRequest.getTotalPrice().compareTo(BigDecimal.ZERO) > 0
                ? orderRequest.getTotalPrice() :
                orderItemEntities.stream().map(OrderItemEntity::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderItemList(orderItemEntities);
        orderEntity.setTotalPrice(totalPrice);

        orderItemEntities.forEach(orderItemEntity -> orderItemEntity.setOrder(orderEntity));
        orderRepository.save(orderEntity);

        return Response.builder()
                .status(200)
                .message("Order successfully placed")
                .build();
    }

    @Override
    public Response updateOrderItemStatus(Long itemId, OrderStatus status) {
        OrderItemEntity orderItemEntity = orderItemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Order item not found"));
        orderItemEntity.setStatus(OrderStatus.valueOf(status.name()));
        orderItemRepository.save(orderItemEntity);

        return Response.builder()
                .status(200)
                .message("Order status successfully updated")
                .build();
    }

    @Override
    public Response filterOrderItems(OrderStatus orderStatus, LocalDateTime startDate, LocalDateTime endDate, Long itemId, Pageable pageable) {
        Specification<OrderItemEntity> specification = Specification.where(OrderItemSpecification.hasStatus(orderStatus))
                .and(OrderItemSpecification.createdBetween(startDate, endDate))
                .and(OrderItemSpecification.hasItemId(itemId));

        Page<OrderItemEntity> orderItemEntityPage = orderItemRepository.findAll(specification, pageable);
        if (orderItemEntityPage.isEmpty()) {
            throw new NotFoundException("Order item not found");
        }
        List<OrderItemDto> orderItemDtos = orderItemEntityPage.getContent()
                .stream()
                .map(entityDtoMapper::mapOrderItemToDtoPlusProductAndUser)
                .collect(Collectors.toList());

        return Response.builder()
                .status(200)
                .orderItemList(orderItemDtos)
                .totalPage(orderItemEntityPage.getTotalPages())
                .totalElement(orderItemEntityPage.getTotalElements())
                .build();
    }
}
