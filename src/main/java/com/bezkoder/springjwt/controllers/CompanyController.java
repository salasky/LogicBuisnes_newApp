package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.Company;
import com.bezkoder.springjwt.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/company")
public class CompanyController {
    @Autowired
    private CompanyService companyService;

    @GetMapping
    public List<Company> getAll(){
        return companyService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable Long id){
        return companyService.getById(id);
    }

    @PostMapping("/add")
    public Company addCompany(@RequestBody Company company){
        return companyService.add(company);
    }

    @PostMapping("/update/{id}")
    public Company update(@RequestBody Company company,@PathVariable Long id){
        return companyService.update(id,company);
    }
    @GetMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable Long id){
        return companyService.delete(id);
    }

}
