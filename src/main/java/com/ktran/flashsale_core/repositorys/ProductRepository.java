package com.ktran.flashsale_core.repositorys;

import com.ktran.flashsale_core.entities.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface ProductRepository extends JpaRepository<Product, Long> {

    @Transactional
    @Modifying

    @Query("UPDATE Product p SET p.currentStock = p.currentStock - :quantity " +
            "WHERE p.id = :id " +
            "AND p.currentStock >= :quantity "
    )

    int deductStockAtomic(@Param("id") Long id,
                             @Param("quantity") int qty);


    @Transactional
    @Modifying
    @Query("UPDATE Product p SET p.currentStock = p.currentStock + :quantity " +
            "WHERE p.id = :id")
    int incrementStockAtomic(@Param("id") Long id, @Param("quantity") int qty);
}
