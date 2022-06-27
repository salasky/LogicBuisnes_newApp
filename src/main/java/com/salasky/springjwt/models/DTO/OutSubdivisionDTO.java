package com.salasky.springjwt.models.DTO;

import com.salasky.springjwt.models.Company;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutSubdivisionDTO {
    private Long id;

    private String name;


    private String contact;

    private String supervisor;


    private String companyName;
}
