package com.salasky.springjwt.repository;

import com.salasky.springjwt.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepositories extends JpaRepository<Employee,Long> {
}
