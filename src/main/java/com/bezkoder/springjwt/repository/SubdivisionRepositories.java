package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Subdivision;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubdivisionRepositories extends JpaRepository<Subdivision,Long> {
}