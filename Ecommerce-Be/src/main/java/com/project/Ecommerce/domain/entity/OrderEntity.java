package com.project.Ecommerce.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal totalPrice;
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY,  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> orderItemList;

    @Column(name = "created_at")
    private final LocalDateTime createdAt = LocalDateTime.now();

    //PAYMENT
}