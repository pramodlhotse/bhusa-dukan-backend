package com.bhusadukan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "weighing_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeighingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @Column(name = "ordered_weight", nullable = false, precision = 14, scale = 3)
    private BigDecimal orderedWeight;

    @Column(name = "actual_weight", nullable = false, precision = 14, scale = 3)
    private BigDecimal actualWeight;

    @Column(name = "weighing_machine_id", length = 100)
    private String weighingMachineId;

    @Column(name = "weighing_machine_reading", precision = 14, scale = 3)
    private BigDecimal weighingMachineReading;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(length = 500)
    private String remarks;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}