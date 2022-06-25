package com.salasky.springjwt.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.salasky.springjwt.statemachine.state.State;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Order {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   //IDENTITY - увеличение по правилам в БД
    @Column(name = "id")
    private Long id;

    @Column(name = "subject")
    private String subject;

    @Column(name = "period_execution")
    private String periodExecution;

    @Column(name = "sign_control")
    private String signControl;

    @Enumerated(EnumType.STRING)
    private State state;

    @Column(name = "order_text")
    private String orderText;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="employeeAuthor_id", nullable=false)
    private Employee AuthEmployee;

    @ManyToMany
    @JoinTable(
            name = "exec_like",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "employeeE_id"))
    List<Employee> ExecEmployee;

    public Order(String subject, String periodExecution, String signControl,  String orderText, Employee authEmployee, List<Employee> execEmployee) {
        this.subject = subject;
        this.periodExecution = periodExecution;
        this.signControl = signControl;
        this.orderText = orderText;
        AuthEmployee = authEmployee;
        ExecEmployee = execEmployee;
    }

    public Order(String subject, String periodExecution, String signControl, State state, String orderText, Employee authEmployee, List<Employee> execEmployee) {
        this.subject = subject;
        this.periodExecution = periodExecution;
        this.signControl = signControl;
        this.state = state;
        this.orderText = orderText;
        AuthEmployee = authEmployee;
        ExecEmployee = execEmployee;
    }
}
