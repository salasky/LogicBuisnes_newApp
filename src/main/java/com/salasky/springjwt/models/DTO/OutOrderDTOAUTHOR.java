package com.salasky.springjwt.models.DTO;

import com.salasky.springjwt.statemachine.state.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutOrderDTOAUTHOR {

    private Long id;

    private String subject;

    private String periodExecution;


    private String signControl;

    private String orderText;


    private String execEmployeeUsername;

    private State state;
}
