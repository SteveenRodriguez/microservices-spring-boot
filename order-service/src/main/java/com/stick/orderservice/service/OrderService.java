package com.stick.orderservice.service;

import com.stick.orderservice.dto.InventoryResponse;
import com.stick.orderservice.dto.OrderLineItemsDto;
import com.stick.orderservice.dto.OrderRequest;
import com.stick.orderservice.model.Order;
import com.stick.orderservice.model.OrderLineItems;
import com.stick.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order(); // Instanciamos un objeto Order
        order.setOrderNumber(UUID.randomUUID().toString()); // le seteamos un UUID
        // Mapeamos la OrderLineItems a un tipo de OrderLineItemsDto
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList().stream()
                // lambda = orderLineItemsDto -> mapToDto(orderLineItemsDto) se reemplaza por el método a referencia
                .map(this::mapToDto)
                .toList();
        //Seteamos los orderLineItems en la order
        order.setOrderLineItems(orderLineItems);

        // Obtenemos todos los skuCodes de la Order
        // lambda = orderLineItems1 -> orderLineItems1.getSkuCode()
        List<String> skuCodes = order.getOrderLineItems().stream().map(OrderLineItems::getSkuCode).toList();

        //Llamar al Inventory-service y realizar orden si el producto existe en stock
        InventoryResponse[] inventoryResponsesArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean allProductsInStock = Arrays.stream(inventoryResponsesArray)
                .allMatch(InventoryResponse::getIsInStock);

        if (allProductsInStock) {
            //Guardamos en la base de datos la Order
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }
    }
    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
