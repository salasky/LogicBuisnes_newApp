package com.salasky.springjwt.controllers;

import com.salasky.springjwt.models.DTO.OrderDTO;
import com.salasky.springjwt.models.Order;
import com.salasky.springjwt.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;


    @PostMapping("/add")
    public ResponseEntity newOrder(@RequestBody OrderDTO orderDTO){
        return orderService.newOrder(orderDTO);
    }


    @GetMapping
    public List<Order> getAll(){
        return orderService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable Long id){
        return orderService.getById(id);
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable Long id){
        return orderService.delete(id);
    }



    @GetMapping("/perform/{id}")
    public ResponseEntity perform(@PathVariable Long id){
        return orderService.performanceState(id);
    }

    @GetMapping("/control/{id}")
    public ResponseEntity control(@PathVariable Long id){
        return orderService.control(id);
    }

    @GetMapping("/accept/{id}")
    public ResponseEntity accept(@PathVariable Long id){
        return orderService.accept(id);
    }

    @GetMapping("/revision/{id}")
    public ResponseEntity revision(@PathVariable Long id){
        return orderService.revision(id);
    }

    @GetMapping("/secondperform/{id}")
    public ResponseEntity secondperform(@PathVariable Long id){
        return orderService.secondPerform(id);
    }
}
