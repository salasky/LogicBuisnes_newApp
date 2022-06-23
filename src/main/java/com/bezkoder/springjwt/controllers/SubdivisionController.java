package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.DTO.SubdivisionDTO;
import com.bezkoder.springjwt.models.Subdivision;
import com.bezkoder.springjwt.service.SubdivisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public List<Subdivision> getAll(){
        return subdivisionService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable Long id){
        return subdivisionService.getById(id);
    }

    @PostMapping("/add")
    public ResponseEntity addCompany(@RequestBody SubdivisionDTO subdivisionDTO){
        return subdivisionService.add(subdivisionDTO);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity update(@RequestBody SubdivisionDTO subdivisionDTO,@PathVariable Long id){
        return subdivisionService.update(id,subdivisionDTO);
    }
    @GetMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable Long id){
        return subdivisionService.delete(id);
    }
}
