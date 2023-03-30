package com.stick.productservice.service;

import com.stick.productservice.dto.ProductRequest;
import com.stick.productservice.dto.ProductResponse;
import com.stick.productservice.model.Product;
import com.stick.productservice.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor //crea un constructor que toma como argumentos todos los campos marcados como final
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public void createProduct(ProductRequest productRequest) {
        // Creación Object Product
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build(); //-> Construye el product

        productRepository.save(product); // se guarda el producto en la base de datos
        log.info("Product id: {} is Save", product.getId());

    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        // llamamos al método para mapear el Product en un ProductResponses
        // se puede método a referencia -> this::mapToProductResponse
        return products.stream().map(product -> mapToProductResponse(product)).toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
        // construcción del ProductResponse
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
