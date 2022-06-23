package com.bezkoder.springjwt.models.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeDTO {

    private String username;

    private String first_name;

    private String second_name;

    private String last_name;

    private String job_title;


    private Long subdivisionId;



}
