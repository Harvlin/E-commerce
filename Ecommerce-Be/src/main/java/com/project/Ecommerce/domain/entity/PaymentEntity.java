package com.project.Ecommerce.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal amount;
    private String method;
    private String status;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private  OrderEntity order;
    @Column(name = "created_at")
    private final LocalDateTime createdAt = LocalDateTime.now();
}
