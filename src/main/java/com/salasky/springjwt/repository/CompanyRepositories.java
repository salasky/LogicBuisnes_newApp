package com.salasky.springjwt.repository;

import com.salasky.springjwt.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepositories extends JpaRepository<Company,Long> {

    Optional<Company> findCompanyByCompanyName(String name);
    boolean existsByCompanyName(String name);
}
