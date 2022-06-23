package com.salasky.springjwt.service;


import com.salasky.springjwt.models.DTO.EmployeeDTO;
import com.salasky.springjwt.models.Employee;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface EmployeeService {


    ResponseEntity getById(Long id);

    List<Employee> getAll();

    ResponseEntity update(Long id, EmployeeDTO employeeDTO);

    ResponseEntity delete(Long id);
}
