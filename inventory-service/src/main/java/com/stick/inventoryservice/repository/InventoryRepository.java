package com.stick.inventoryservice.repository;

import com.stick.inventoryservice.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
//    Optional<Inventory> findBySkuCode(String skuCode); obtiene solo 1 skuCode del producto

    List<Inventory> findBySkuCodeIn(List<String> skuCodes);
}
