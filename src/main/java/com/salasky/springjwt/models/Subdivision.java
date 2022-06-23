package com.salasky.springjwt.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "subdivision")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subdivision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   //IDENTITY - увеличение по правилам в БД
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "contact")
    private String contact;

    @Column(name = "supervisor")
    private String supervisor;

    @ManyToOne
    @JoinColumn(name="company_id")
    private Company company;


    @JsonIgnore
    @OneToMany(mappedBy = "subdivision")
    private List<Employee> employees;

    public Subdivision(String name, String contact, String supervisor, Company company) {
        this.name = name;
        this.contact = contact;
        this.supervisor = supervisor;
        this.company = company;
    }





}
