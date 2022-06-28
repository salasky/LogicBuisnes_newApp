package com.salasky.springjwt.controllers;

import com.salasky.springjwt.models.Company;
import com.salasky.springjwt.models.DTO.OrderDTO;
import com.salasky.springjwt.models.DTO.OrderDTOA;
import com.salasky.springjwt.models.Order;
import com.salasky.springjwt.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;


    @GetMapping
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('USER')")
    public List<OrderDTO> getAll() {
        return orderService.getAll();
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity getById(@PathVariable Long id) {
        return orderService.getById(id);
    }


    @PostMapping("/add")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity newOrder(@RequestBody OrderDTO orderDTO) {
        return orderService.newOrder(orderDTO);
    }


    @PostMapping("/update/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity update(@RequestBody OrderDTO orderDTO, @PathVariable Long id) {
        return orderService.update(orderDTO, id);
    }


    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity delete(@PathVariable Long id) {
        return orderService.delete(id);
    }


    @GetMapping("/authorders")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('USER')")
    public List<OrderDTO> getAuthorOrder() {
        return orderService.getAuthorOrder();
    }


    @GetMapping("/execorders")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('USER')")
    public List<OrderDTOA> getExecOrder() {
        return orderService.getExecutionOrderMe();
    }

    @GetMapping("/findBySubject")
    public List<OrderDTO> findOrderBySubjects(@RequestParam String subject) {
        return orderService.findOrderBySubject(subject);
    }


    @GetMapping("/perform/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity perform(@PathVariable Long id) {
        return orderService.performanceState(id);
    }


    @GetMapping("/control/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity control(@PathVariable Long id) {
        return orderService.control(id);
    }


    @GetMapping("/accept/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity accept(@PathVariable Long id) {
        return orderService.accept(id);
    }


    @GetMapping("/revision/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity revision(@PathVariable Long id) {
        return orderService.revision(id);
    }


    @GetMapping("/secondperform/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity secondperform(@PathVariable Long id) {
        return orderService.secondPerform(id);
    }
}
