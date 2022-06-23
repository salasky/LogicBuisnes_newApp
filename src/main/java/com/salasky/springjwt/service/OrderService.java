package com.salasky.springjwt.service;

import com.salasky.springjwt.models.DTO.OrderDTO;
import com.salasky.springjwt.models.Order;
import com.salasky.springjwt.statemachine.event.Event;
import com.salasky.springjwt.statemachine.state.State;
import org.springframework.http.ResponseEntity;
import org.springframework.statemachine.StateMachine;

import java.util.List;

public interface OrderService {

    public ResponseEntity newOrder(OrderDTO orderDTO);
    public ResponseEntity update(OrderDTO orderDTO,Long id);
    ResponseEntity delete(Long id);
    List<Order> getAll();
    ResponseEntity getById(Long id);


    ResponseEntity control(Long orderId);

    ResponseEntity  accept(Long orderId);

    ResponseEntity  revision(Long orderId);

    ResponseEntity secondPerform(Long orderId);

    ResponseEntity performanceState(Long orderid);

    //получение конечного автомата из базы данных
    StateMachine<State, Event> build (Long orderId);
    //Конечный автомат поддерживает стандартные сообщения Spring.
    void senEvent(Long orderId,StateMachine<State,Event> sm,Event event);
}
