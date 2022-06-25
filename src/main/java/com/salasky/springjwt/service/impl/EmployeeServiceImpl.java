package com.salasky.springjwt.service.impl;

import com.salasky.springjwt.models.DTO.EmployeeDTO;
import com.salasky.springjwt.models.Employee;
import com.salasky.springjwt.repository.EmployeeRepositories;
import com.salasky.springjwt.repository.SubdivisionRepositories;
import com.salasky.springjwt.service.EmployeeService;
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

        if(subdivisionRepositories.existsByName(employeeDTO.getSubdivisionName())){
            if(employeeRepositories.existsById(id)){
                var employee=employeeRepositories.findById(id).get();
                employee.setFirstName(employeeDTO.getFirstName());
                employee.setSecondName(employeeDTO.getSecondName());
                employee.setLastName(employeeDTO.getLastName());
                employee.setJobTitle(employeeDTO.getJobTitle());
                employee.setUsername(employeeDTO.getUsername());
                employee.setSubdivision(subdivisionRepositories.findByName(employeeDTO.getSubdivisionName()).get());
                logger.info("Обновление информации о работнике");
                return ResponseEntity.status(HttpStatus.OK).body(employeeRepositories.save(employee));
            }
            var emp=new Employee();
            emp.setId(id);
            emp.setFirstName(employeeDTO.getFirstName());
            emp.setSecondName(employeeDTO.getSecondName());
            emp.setLastName(employeeDTO.getLastName());
            emp.setJobTitle(employeeDTO.getJobTitle());
            emp.setUsername(employeeDTO.getUsername());
            emp.setSubdivision(subdivisionRepositories.findByName(employeeDTO.getSubdivisionName()).get());

            logger.error("Создан новый работник с id "+id);
            return ResponseEntity.status(HttpStatus.OK).body("Создан новый работник с id "+id);
        }
        logger.error("Не найдено подразделение названием "+employeeDTO.getSubdivisionName());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Не найдено подразделение названием "+employeeDTO.getSubdivisionName()+"\n" +
                "Пожалуйста,обратитесь к модератору для добавления подразделения в базу");
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
