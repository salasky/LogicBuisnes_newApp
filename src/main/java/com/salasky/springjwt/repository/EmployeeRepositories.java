package com.salasky.springjwt.repository;

import com.salasky.springjwt.models.Employee;
import com.salasky.springjwt.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepositories extends JpaRepository<Employee,Long> {

    Optional<Employee> findByUsername(String username);
    boolean existsByUsername(String username);



}
