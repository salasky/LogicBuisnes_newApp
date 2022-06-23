package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepositories extends JpaRepository<Company,Long> {
}
