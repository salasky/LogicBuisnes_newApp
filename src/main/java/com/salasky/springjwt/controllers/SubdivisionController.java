package com.salasky.springjwt.controllers;

import com.salasky.springjwt.models.DTO.OutSubdivisionDTO;
import com.salasky.springjwt.models.DTO.SubdivisionDTO;
import com.salasky.springjwt.models.Subdivision;
import com.salasky.springjwt.service.SubdivisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/subdivisions")
public class SubdivisionController {
    private SubdivisionService subdivisionService;

    @Autowired
    public SubdivisionController(SubdivisionService subdivisionService) {
        this.subdivisionService = subdivisionService;
    }

    @GetMapping
    public List<OutSubdivisionDTO> getAll(){
        return subdivisionService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable Long id){
        return subdivisionService.getById(id);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity addCompany(@RequestBody SubdivisionDTO subdivisionDTO){
        return subdivisionService.add(subdivisionDTO);
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity update(@RequestBody SubdivisionDTO subdivisionDTO,@PathVariable Long id){
        return subdivisionService.update(id,subdivisionDTO);
    }
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity delete(@PathVariable Long id){
        return subdivisionService.delete(id);
    }
}
