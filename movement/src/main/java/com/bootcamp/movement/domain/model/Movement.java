package com.bootcamp.movement.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Document("movements")
public class Movement {

    private String id;

    private MovementType type;

    private String productId;

    private Double amount;

    private String description;

    private LocalDateTime createdAt;

    private Double commission;

}