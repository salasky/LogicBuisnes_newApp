package com.salasky.springjwt.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "company")
@Getter
@Setter
@NoArgsConstructor
@ToString
@Data
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   //IDENTITY - увеличение по правилам в БД
    @Column(name = "id")
    private Long id;

    @Column(name = "name_of_company")
    private String companyName;

    @Column(name = "physical_adress")
    private String physicalAddress;

    @Column(name = "legal_address")
    private String legalAddress;

    @Column(name = "supervisor")
    private String supervisor;

    @JsonIgnore
    @OneToMany(mappedBy = "company")
    private List<Subdivision> subdivisions;


    public Company(String companyName, String physical_adress, String legal_address, String supervisor) {
        this.companyName = companyName;
        this.physicalAddress = physical_adress;
        this.legalAddress = legal_address;
        this.supervisor = supervisor;
    }

}
