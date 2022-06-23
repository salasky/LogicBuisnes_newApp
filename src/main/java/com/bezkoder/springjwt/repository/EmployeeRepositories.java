package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepositories extends JpaRepository<Employee,Long> {
}
