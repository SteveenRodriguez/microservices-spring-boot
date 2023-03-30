package com.stick.productservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(value = "producto") //define que es un documento de MongoDB
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Product {

    @Id //identificador único del producto cuando se crea
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
}
