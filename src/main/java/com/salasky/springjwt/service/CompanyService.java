package com.salasky.springjwt.service;

import com.salasky.springjwt.models.Company;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CompanyService {

    Company add(Company company);

    ResponseEntity getById(Long id);

    List<Company> getAll();

    Company update(Long id, Company company);

    ResponseEntity delete(Long id);



}
