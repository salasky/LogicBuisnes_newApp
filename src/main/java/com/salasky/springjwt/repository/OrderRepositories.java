package com.salasky.springjwt.repository;

import com.salasky.springjwt.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepositories extends JpaRepository<Order,Long> {
}
