package com.bezkoder.springjwt.service;

import com.bezkoder.springjwt.models.DTO.SubdivisionDTO;
import com.bezkoder.springjwt.models.Subdivision;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SubdivisionService {
    ResponseEntity add(SubdivisionDTO subdivisionDTO);

    ResponseEntity getById(Long id);

    List<Subdivision> getAll();

    ResponseEntity update(Long id, SubdivisionDTO newsubdivision);

    ResponseEntity delete(Long id);
}
