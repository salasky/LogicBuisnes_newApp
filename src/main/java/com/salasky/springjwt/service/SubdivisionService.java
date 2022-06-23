package com.salasky.springjwt.service;

import com.salasky.springjwt.models.DTO.SubdivisionDTO;
import com.salasky.springjwt.models.Subdivision;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SubdivisionService {
    ResponseEntity add(SubdivisionDTO subdivisionDTO);

    ResponseEntity getById(Long id);

    List<Subdivision> getAll();

    ResponseEntity update(Long id, SubdivisionDTO newsubdivision);

    ResponseEntity delete(Long id);
}
