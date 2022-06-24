package com.salasky.springjwt.models.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubdivisionDTO {

    private String name;


    private String contact;


    private String supervisor;

    private String companyName;
    /*private Long companyId;*/

}
