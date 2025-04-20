package io.nullptr.cmb.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "t_product_net_value", indexes = {
        @Index(name = "uniq_product_net_value_by_date", columnList = "date, innerCode", unique = true)
})
public class ProductNetValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 127)
    private String innerCode;

    /**
     * 净值更新日期
     */
    private LocalDate date;

    /**
     * 特定日期的净值
     */
    @Column(precision = 10, scale = 6, updatable = false)
    private BigDecimal value;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
