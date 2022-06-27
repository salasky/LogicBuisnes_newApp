package com.salasky.springjwt.service.impl;

import com.salasky.springjwt.models.Company;
import com.salasky.springjwt.models.payload.response.MessageResponse;
import com.salasky.springjwt.repository.CompanyRepositories;
import com.salasky.springjwt.repository.SubdivisionRepositories;
import com.salasky.springjwt.service.CompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CompanyServiceImpl implements CompanyService {

    Logger logger = LoggerFactory.getLogger(CompanyServiceImpl.class);

    private CompanyRepositories companyRepositories;
    private SubdivisionRepositories subdivisionRepositories;

    @Autowired
    public CompanyServiceImpl(CompanyRepositories companyRepositories, SubdivisionRepositories subdivisionRepositories) {
        this.companyRepositories = companyRepositories;
        this.subdivisionRepositories = subdivisionRepositories;
    }

    @Override
    public List<Company> getAll() {

        logger.info("Выдан список компаний");
        return companyRepositories.findAll();
    }

    @Override
    public Company add(Company company) {
        logger.info("Добавлена компания");
        return companyRepositories.save(company);
    }

    @Override
    public ResponseEntity getById(Long id) {

        if(companyRepositories.existsById(id)){
            logger.info("Выдана информация о компании");
            return ResponseEntity.status(HttpStatus.OK).body(companyRepositories.findById(id).get());
        }
        logger.error("Компания с id "+id+ " не найдена");
        return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Компания с id "+id+ " не найдена"));

    }

    @Override
    public Company update(Long id, Company newCompany) {


        return companyRepositories.findById(id)
                .map(company -> {
                    company.setCompanyName(newCompany.getCompanyName());
                    company.setLegalAddress(newCompany.getLegalAddress());
                    company.setPhysicalAddress(newCompany.getPhysicalAddress());
                    company.setSupervisor(newCompany.getSupervisor());

                    logger.info("Обнавлена информация о компании с id "+id);
                    return companyRepositories.save(company);
                })
                .orElseGet(() -> {
                    newCompany.setId(id);
                    logger.info("Добавлена компания с id "+id);
                    return companyRepositories.save(newCompany);
                });
    }


    @Override
    public ResponseEntity delete(Long id) {
        if(companyRepositories.existsById(id)){
            companyRepositories.deleteById(id);
            logger.info("Компания  с id "+id+ " удалена");
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse("Company с id "+ id+ " успешно удален"));

        }
        logger.info("Удаление.Компания  с id "+id+ " не найдена");
        return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Удаление.Компания  с id "+id+ " не найдена"));
    }

}
