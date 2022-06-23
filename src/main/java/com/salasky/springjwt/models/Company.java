package com.salasky.springjwt.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "company")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   //IDENTITY - увеличение по правилам в БД
    @Column(name = "id")
    private Long id;

    @Column(name = "name_of_company")
    private String name_of_company;

    @Column(name = "physical_adress")
    private String physical_adress;

    @Column(name = "legal_address")
    private String legal_address;

    @Column(name = "supervisor")
    private String supervisor;

    @JsonIgnore
    @OneToMany(mappedBy = "company")
    private List<Subdivision> subdivisions;


    public Company( String name_of_company, String physical_adress, String legal_address, String supervisor) {
        this.name_of_company = name_of_company;
        this.physical_adress = physical_adress;
        this.legal_address = legal_address;
        this.supervisor = supervisor;
    }

}
