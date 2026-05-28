package com.musiconline.repository;

import com.musiconline.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o JOIN FETCH o.vinyl v JOIN FETCH v.seller " +
           "WHERE o.buyer.email = :email ORDER BY o.orderedAt DESC")
    List<Order> findByBuyerEmailOrderByOrderedAtDesc(@Param("email") String email);
}
