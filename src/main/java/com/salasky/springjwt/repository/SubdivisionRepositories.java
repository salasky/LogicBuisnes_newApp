package com.salasky.springjwt.repository;

import com.salasky.springjwt.models.Company;
import com.salasky.springjwt.models.Subdivision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubdivisionRepositories extends JpaRepository<Subdivision,Long> {
    Optional<Subdivision> findByName(String name);
    boolean existsByName(String name);
    Optional<Subdivision> findByCompanyAndName(Company company,String subName);
}
