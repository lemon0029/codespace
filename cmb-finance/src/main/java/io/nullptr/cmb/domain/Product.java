package io.nullptr.cmb.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Entity(name = "t_product")
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 7)
    private String productTag;

    private String offNae;

    private String shortName;

    private String saCode;

    @Column(unique = true, length = 127)
    private String innerCode;

    @Column(unique = true, length = 127)
    private String saleCode;

    /**
     * 是否已售罄 (Y/N)
     */
    @Column(length = 7, nullable = false)
    private String sellOut;

    /**
     * 稳健低波 - B, 稳健增值 - C, 稳中求进 - D
     */
    @Column(length = 7, nullable = false)
    private String riskType;

    private String riskLevel;

    /**
     * 可购额度？
     */
    private String quota;

    /**
     * 是否为热门产品
     */
    private boolean hotProduct;

    private SalesPlatform salesPlatform;

    private boolean subscribed;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
