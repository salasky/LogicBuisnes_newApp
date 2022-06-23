package com.bezkoder.springjwt.service.impl;

import com.bezkoder.springjwt.models.DTO.EmployeeDTO;
import com.bezkoder.springjwt.models.Employee;
import com.bezkoder.springjwt.repository.EmployeeRepositories;
import com.bezkoder.springjwt.repository.SubdivisionRepositories;
import com.bezkoder.springjwt.service.EmployeeService;
import com.bezkoder.springjwt.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class EmployeeServiceImpl implements EmployeeService {
    Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private EmployeeRepositories employeeRepositories;
    private SubdivisionRepositories subdivisionRepositories;


    @Autowired
    public EmployeeServiceImpl(EmployeeRepositories employeeRepositories, SubdivisionRepositories subdivisionRepositories) {
        this.employeeRepositories = employeeRepositories;
        this.subdivisionRepositories = subdivisionRepositories;

    }


    @Override
    public ResponseEntity getById(Long id) {
        if(employeeRepositories.existsById(id)){
            logger.info("Выдача информации о работнике");
            return ResponseEntity.status(HttpStatus.OK).body(employeeRepositories.findById(id).get());
        }
        logger.error("Работник с id "+id+" не существует");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Работник с id "+id+" не существует");
    }

    @Override
    public List<Employee> getAll() {
        logger.info("Выдача информации о работниках");
        return employeeRepositories.findAll();
    }

    @Override
    public ResponseEntity update(Long id, EmployeeDTO employeeDTO) {

        if(subdivisionRepositories.existsById(employeeDTO.getSubdivisionId())){
            if(employeeRepositories.existsById(id)){
                var employee=employeeRepositories.findById(id).get();
                employee.setFirst_name(employeeDTO.getFirst_name());
                employee.setSecond_name(employeeDTO.getSecond_name());
                employee.setLast_name(employeeDTO.getLast_name());
                employee.setJob_title(employeeDTO.getJob_title());
                employee.setUsername(employeeDTO.getUsername());
                employee.setSubdivision(subdivisionRepositories.findById(employeeDTO.getSubdivisionId()).get());
                logger.info("Обновление информации о работнике");
                return ResponseEntity.status(HttpStatus.OK).body(employeeRepositories.save(employee));
            }
            var emp=new Employee();
            emp.setId(id);
            emp.setFirst_name(employeeDTO.getFirst_name());
            emp.setSecond_name(employeeDTO.getSecond_name());
            emp.setLast_name(employeeDTO.getLast_name());
            emp.setJob_title(employeeDTO.getJob_title());
            emp.setUsername(employeeDTO.getUsername());
            emp.setSubdivision(subdivisionRepositories.findById(employeeDTO.getSubdivisionId()).get());

            logger.error("Создан новый работник с id "+id);
            return ResponseEntity.status(HttpStatus.OK).body("Создан новый работник с id "+id);
        }
        logger.error("Не найдено подразделение с id "+employeeDTO.getSubdivisionId());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Не найдено подразделение с id "+employeeDTO.getSubdivisionId());
    }

    @Override
    public ResponseEntity delete(Long id) {

        if(employeeRepositories.existsById(id)){
            employeeRepositories.deleteById(id);
            logger.info("Работник  с id "+id+ " удален");
            return ResponseEntity.status(HttpStatus.OK).body("Работник  с id "+id+ " удален");
        }
        logger.info("Удаление.Работник  с id "+id+ " не найден");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Удаление.Работник  с id "+id+ " не найден");
    }
}
