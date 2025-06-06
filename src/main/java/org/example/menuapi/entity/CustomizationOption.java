package org.example.menuapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "customization_option", indexes = {
        @Index(name = "idx_customization_option_group", columnList = "group_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomizationOption {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private CustomizationGroup group;

    @Column(name = "option_name")
    private String optionName;

    @Column(name = "extra_price", precision = 10, scale = 2)
    private BigDecimal extraPrice;
}