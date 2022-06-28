package com.salasky.springjwt.models.DTO;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTOA {

    private String subject;

    private String periodExecution;


    private String signControl;

    private String orderText;


    private String AuthorEmployeeUsername;


}