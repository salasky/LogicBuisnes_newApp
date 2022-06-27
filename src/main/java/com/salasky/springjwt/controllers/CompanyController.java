package com.salasky.springjwt.controllers;

import com.salasky.springjwt.models.Company;
import com.salasky.springjwt.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/company")
public class CompanyController {
    @Autowired
    private CompanyService companyService;

    @GetMapping
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('USER')")
    public List<Company> getAll(){
        return companyService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity getById(@PathVariable Long id){
        return companyService.getById(id);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public Company addCompany(@RequestBody Company company){
        return companyService.add(company);
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public Company update(@RequestBody Company company,@PathVariable Long id){
        return companyService.update(id,company);
    }
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity delete(@PathVariable Long id){
        return companyService.delete(id);
    }

}
