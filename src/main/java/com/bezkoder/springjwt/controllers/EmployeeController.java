package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.DTO.EmployeeDTO;
import com.bezkoder.springjwt.models.Employee;
import com.bezkoder.springjwt.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<Employee> getAll(){
        return employeeService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable Long id){
        return employeeService.getById(id);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity update(@RequestBody EmployeeDTO employeeDTO,@PathVariable Long id){
        return employeeService.update(id,employeeDTO);
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable Long id){
        return employeeService.delete(id);
    }


}
