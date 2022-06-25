package com.salasky.springjwt.models.DTO;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private String subject;

    private String periodExecution;


    private String signControl;

    private String orderText;


    private String execEmployeeUsername;


}