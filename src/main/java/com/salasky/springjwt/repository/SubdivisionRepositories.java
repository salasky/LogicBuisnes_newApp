package com.salasky.springjwt.repository;

import com.salasky.springjwt.models.Subdivision;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubdivisionRepositories extends JpaRepository<Subdivision,Long> {
}
