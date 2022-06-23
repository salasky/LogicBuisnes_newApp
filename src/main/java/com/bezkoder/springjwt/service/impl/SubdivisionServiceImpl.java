package com.bezkoder.springjwt.service.impl;

import com.bezkoder.springjwt.models.DTO.SubdivisionDTO;
import com.bezkoder.springjwt.models.Subdivision;
import com.bezkoder.springjwt.repository.CompanyRepositories;
import com.bezkoder.springjwt.repository.EmployeeRepositories;
import com.bezkoder.springjwt.repository.SubdivisionRepositories;
import com.bezkoder.springjwt.service.SubdivisionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubdivisionServiceImpl implements SubdivisionService {
    Logger logger = LoggerFactory.getLogger(SubdivisionServiceImpl.class);

    private SubdivisionRepositories subdivisionRepositories;
    private EmployeeRepositories employeeRepositories;
    private CompanyRepositories companyRepositories;
    @Autowired
    public SubdivisionServiceImpl(SubdivisionRepositories subdivisionRepositories, EmployeeRepositories employeeRepositories, CompanyRepositories companyRepositories) {
        this.subdivisionRepositories = subdivisionRepositories;
        this.employeeRepositories = employeeRepositories;
        this.companyRepositories = companyRepositories;
    }

    @Override
    public ResponseEntity getById(Long id) {
        if(subdivisionRepositories.existsById(id)){
            logger.info("Выдача инфрмации о подразделении с id "+id);
            return ResponseEntity.status(HttpStatus.OK).body(subdivisionRepositories.findById(id).get());
        }
        logger.info("Не найдено подразделение с id "+id);
        return ResponseEntity.status(HttpStatus.OK).body("Не найдено подразделение с id "+id);
    }

    @Override
    public List<Subdivision> getAll() {
        logger.info("Выдача инфрмации о подразделениях");
        return subdivisionRepositories.findAll();
    }

    @Override
    public ResponseEntity add(SubdivisionDTO subdivisionDTO) {

        var companys=companyRepositories.findById(subdivisionDTO.getCompanyId());
        if(companys.isPresent()){
            var company=companys.get();
            Subdivision subdivision=new Subdivision(subdivisionDTO.getName(),subdivisionDTO.getContact(),subdivisionDTO.getSupervisor(),company);
            logger.info("Добавление нового подразделения");
            var sub=subdivisionRepositories.save(subdivision);
            return ResponseEntity.status(HttpStatus.OK).body(sub);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Нет компании с данным id");

    }

    @Override
    public ResponseEntity update(Long id, SubdivisionDTO newsubdivision) {
        if(companyRepositories.existsById(newsubdivision.getCompanyId())){
           if(subdivisionRepositories.existsById(id)){
               var sb=subdivisionRepositories.findById(id).get();
                      sb.setName(newsubdivision.getName());
                      sb.setContact(newsubdivision.getContact());
                      sb.setCompany(companyRepositories.findById(newsubdivision.getCompanyId()).get());
                      sb.setSupervisor(newsubdivision.getSupervisor());
                      logger.info("Обновление информации о подразделении");
                      return ResponseEntity.status(HttpStatus.OK).body(subdivisionRepositories.save(sb));
           }
            var sab=new Subdivision();
            sab.setId(id);
            sab.setName(newsubdivision.getName());
            sab.setContact(newsubdivision.getContact());
            sab.setCompany(companyRepositories.findById(newsubdivision.getCompanyId()).get());
            sab.setSupervisor(newsubdivision.getSupervisor());
            logger.error("Создано новое подразделение с id "+id);
            return ResponseEntity.status(HttpStatus.OK).body("Создано новое подразделение с id "+id);
        }
        logger.error("Не найдена компания с id "+newsubdivision.getCompanyId());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Не найдена компания с id "+newsubdivision.getCompanyId());
    }

    @Override
    public ResponseEntity delete(Long id) {
        try {
            subdivisionRepositories.deleteById(id);
            logger.info("Удаление подразделения с id "+id);
            return ResponseEntity.status(HttpStatus.OK).body("Subdivision с id "+ id+ " успешно удален");
        }catch (Exception e){
            logger.error("Удаление подразделения. Подразделение с id "+id+ " не найдено");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Subdivision с id "+ id+ " не найден");
        }

    }
}
