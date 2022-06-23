package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepositories extends JpaRepository<Order,Long> {
}
