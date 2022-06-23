package com.salasky.springjwt.repository;

import com.salasky.springjwt.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepositories extends JpaRepository<Company,Long> {
    Company findByName_of_company(String name);
}
