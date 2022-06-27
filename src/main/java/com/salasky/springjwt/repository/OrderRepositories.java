package com.salasky.springjwt.repository;

import com.salasky.springjwt.models.Employee;
import com.salasky.springjwt.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepositories extends JpaRepository<Order,Long> {



    @Query("SELECT o FROM Order o join fetch o.AuthEmployee WHERE o.AuthEmployee= ?1")
    List<Order> getAllByemployeeAuthor(Employee employee);



}
