package com.bezkoder.springjwt.service.impl;

import com.bezkoder.springjwt.models.DTO.OrderDTO;
import com.bezkoder.springjwt.models.Employee;
import com.bezkoder.springjwt.models.Order;
import com.bezkoder.springjwt.models.Subdivision;
import com.bezkoder.springjwt.repository.EmployeeRepositories;
import com.bezkoder.springjwt.repository.OrderRepositories;
import com.bezkoder.springjwt.service.OrderService;
import com.bezkoder.springjwt.statemachine.event.Event;
import com.bezkoder.springjwt.statemachine.state.State;
import com.bezkoder.springjwt.validator.DateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private StateMachineFactory<State, Event> stateMachineFactory;
    private OrderRepositories orderRepositories;
    private DateValidator dateValidator;
    private EmployeeRepositories employeeRepositories;
    private StateMachinePersister<State, Event, String> persister;
    @Autowired
    public OrderServiceImpl(StateMachineFactory<State, Event> stateMachineFactory, OrderRepositories orderRepositories, DateValidator dateValidator, EmployeeRepositories employeeRepositories, StateMachinePersister<State, Event, String> persister) {
        this.stateMachineFactory = stateMachineFactory;
        this.orderRepositories = orderRepositories;
        this.dateValidator = dateValidator;
        this.employeeRepositories = employeeRepositories;
        this.persister = persister;
    }



    @Override
    public ResponseEntity newOrder(OrderDTO orderDTO) {

        //Реализовать: автор поручения-тот кто аутентифицировался



        if (!employeeRepositories.findById(orderDTO.getAuthEmployeeId()).isPresent()) {
            logger.error("Автор поручения с id" + orderDTO.getAuthEmployeeId() + " не существует");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Автор поручения с id " + orderDTO.getAuthEmployeeId() + " не существует");
        }

        if (!employeeRepositories.findById(orderDTO.getExecEmployeeId()).isPresent()) {
            logger.error("Исполнитель поручения поручения с id " + orderDTO.getExecEmployeeId() + " не существует");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Исполнитель поручения поручения с id " + orderDTO.getExecEmployeeId() + " не существует");
        }

        if (!dateValidator.isValidDate(orderDTO.getPeriodExecution())) {
            logger.error("Неверный формат даты исполнения поручения");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Неверный формат даты исполнения поручения\n" +
                    "Формат даты YYYY-MM-DD");
        }
        List<Employee> employeesList = new ArrayList<>();
        var employee = employeeRepositories.findById(orderDTO.getExecEmployeeId()).get();
        employeesList.add(employee);

        var order = new Order(orderDTO.getSubject(), orderDTO.getPeriodExecution(), orderDTO.getSignControl(), orderDTO.getOrderText()
                , employeeRepositories.findById(orderDTO.getAuthEmployeeId()).get()
                , employeesList);

        var sm = stateMachineFactory.getStateMachine();
        try {
            persister.persist(sm, String.valueOf(order.getId()));
            order.setState(sm.getState().getId());
            var saveOrder = orderRepositories.save(order);
            logger.info("Создано поручение с id " + saveOrder.getId());
            return ResponseEntity.status(HttpStatus.OK).body(saveOrder);
        } catch (Exception e) {
            logger.error("Не удалось сохранить состояние StateMachine");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Не удалось сохранить состояние StateMachine");
        }
    }
    private Order setter(OrderDTO orderDTO,Order order){
        order.setOrderText(orderDTO.getOrderText());
        order.setAuthEmployee(employeeRepositories.findById(orderDTO.getAuthEmployeeId()).get());
        List<Employee> employees=new ArrayList<>();
        employees.add(employeeRepositories.findById(orderDTO.getExecEmployeeId()).get());
        order.setExecEmployee(employees);
        order.setPeriodExecution(orderDTO.getPeriodExecution());
        order.setSignControl(orderDTO.getSignControl());
        order.setSubject(orderDTO.getSubject());
        return order;
    }
    @Override
    public ResponseEntity update(OrderDTO orderDTO, Long id) {
        if(employeeRepositories.existsById(orderDTO.getAuthEmployeeId())){
            if(employeeRepositories.existsById(orderDTO.getExecEmployeeId())){
                if(orderRepositories.existsById(id)){
                    var order=orderRepositories.findById(id).get();
                    logger.info("Обновление информации о подразделении");
                    return ResponseEntity.status(HttpStatus.OK).body(orderRepositories.save(setter(orderDTO,order)));
                }
                var order=new Order();
                order.setId(id);
                orderRepositories.save( setter(orderDTO,order));
                logger.error("Создано новое поручение с id "+id);
                return ResponseEntity.status(HttpStatus.OK).body("Создано новое поручение с id "+id);
            }
            logger.error("Не найден работник с id "+orderDTO.getExecEmployeeId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Не найден работник с id "+orderDTO.getExecEmployeeId());
        }
        logger.error("Не найден работник с id "+orderDTO.getAuthEmployeeId());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Не найден работник с id "+orderDTO.getAuthEmployeeId());
    }

    @Override
    public List<Order> getAll() {
        logger.info("Выдача инфрмации о поручениях");
        return orderRepositories.findAll();
    }

    @Override
    public ResponseEntity getById(Long id) {
        var order=orderRepositories.findById(id);

        if(order.isPresent()){
            logger.info("Выдача инфрмации о поручении");
            return ResponseEntity.status(HttpStatus.OK).body(order.get());
        }
        logger.error("Не удалось найти поручение с id "+id);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Не удалось найти поручение с id "+id);
    }

    @Override
    public ResponseEntity delete(Long id) {
        if(orderRepositories.existsById(id)){
            orderRepositories.deleteById(id);
            logger.info("Поручение  с id "+id+ " удалено");
            return ResponseEntity.status(HttpStatus.OK).body("Поручение  с id "+id+ " удалено");
        }
        logger.error("Удаление.Поручение  с id "+id+ " не найдено");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Удаление.Поручение  с id "+id+ " не найдено");
    }


    //В стутус в исполнении могут перевести только исполнители поручения..реализовать
    @Override
    public ResponseEntity performanceState(Long orderid) {

        //Если
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //String currentPrincipalName = authentication.getName(); равен исполнителю поручения то

        var order = orderRepositories.findById(orderid);
        if (order.isPresent()) {
            var sm = build(orderid);

            if ( sm.getState().getId().equals(State.PREPARATION)) {

                senEvent(orderid, sm, Event.START); //меняем состояние

                try {
                    persister.persist(sm, String.valueOf(orderid));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                var neworder = order.get();
                neworder.setState(sm.getState().getId());
                var saveorder = orderRepositories.save(neworder);
                return ResponseEntity.status(HttpStatus.OK).body(saveorder);
            }

            logger.error("Предыдущее состояние машины не Preparation.Текщее состояние " +sm.getState().getId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Предыдущее состояние машины не Preparation.Текщее состояние "+ sm.getState().getId()+"\n" +
                    "PREPARATION-->PERFORMANCE-->CONTROL-->ACCEPTANCE\n" +
                    "                ^              |\n" +
                    "                |              |\n" +
                    "                |-REVISION <---\n" );
        }
        logger.error("Не удалось поменять состояние StateMchine. Нет поручения с id " + orderid);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Не удалось поменять состояние StateMchine. Нет поручения с id " + orderid);
    }



    //В статус принятие меняет только автор поручения..реализовать
    @Override
    public ResponseEntity control(Long orderid) {
        //Если
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //String currentPrincipalName = authentication.getName(); равен исполнителю поручения то

        var order = orderRepositories.findById(orderid);
        if (order.isPresent()) {

            var sm = build(orderid);

            if ( sm.getState().getId().equals(State.PERFORMANCE)) {

                senEvent(orderid, sm, Event.FIRST_CONTROL); //меняем состояние

                try {
                    persister.persist(sm, String.valueOf(orderid));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                var neworder = order.get();
                neworder.setState(sm.getState().getId());
                var saveorder = orderRepositories.save(neworder);
                return ResponseEntity.status(HttpStatus.OK).body(saveorder);
            }

            logger.error("Предыдущее состояние машины не Performance.Текщее состояние " + sm.getState().getId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Предыдущее состояние машины не Performance.Текщее состояние " + sm.getState().getId()+"\n" +
                    "PREPARATION-->PERFORMANCE-->CONTROL-->ACCEPTANCE\n" +
                    "                ^              |\n" +
                    "                |              |\n" +
                    "                |-REVISION <---\n" );
        }
        logger.error("Не удалось поменять состояние StateMchine. Нет поручения с id " + orderid);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Не удалось поменять состояние StateMchine. Нет поручения с id " + orderid);
    }


    @Override
    public ResponseEntity accept(Long orderid) {
        //Если
        //String currentPrincipalName = authentication.getName(); равен автору поручения

        var order = orderRepositories.findById(orderid);
        if (order.isPresent()) {

            var sm = build(orderid);

            if ( sm.getState().getId().equals(State.CONTROL)) {

                senEvent(orderid, sm, Event.InternalSuccess);
                senEvent(orderid, sm, Event.SUCCESS);

                try {
                    persister.persist(sm, String.valueOf(orderid));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                var neworder = order.get();
                neworder.setState(sm.getState().getId());
                var saveorder = orderRepositories.save(neworder);
                return ResponseEntity.status(HttpStatus.OK).body(saveorder);
            }

            logger.error("Предыдущее состояние машины не Control.Текщее состояние " + sm.getState().getId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Предыдущее состояние машины не Control.Текщее состояние " + sm.getState().getId()+"\n" +
                    "PREPARATION-->PERFORMANCE-->CONTROL-->ACCEPTANCE\n" +
                    "                ^              |\n" +
                    "                |              |\n" +
                    "                |-REVISION <---\n" );
        }
        logger.error("Не удалось поменять состояние StateMchine. Нет поручения с id " + orderid);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Не удалось поменять состояние StateMchine. Нет поручения с id " + orderid);
    }

    @Override
    public ResponseEntity revision(Long orderid) {
        //Если
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //String currentPrincipalName = authentication.getName(); равен автору поручения то

        var order = orderRepositories.findById(orderid);
        if (order.isPresent()) {

            var sm = build(orderid);

            if ( sm.getState().getId().equals(State.CONTROL)) {

                senEvent(orderid, sm, Event.InternalFailed); //меняем состояние
                senEvent(orderid, sm, Event.FAIL_CONTROL);
                try {
                    persister.persist(sm, String.valueOf(orderid));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                var neworder = order.get();
                neworder.setState(sm.getState().getId());
                var saveorder = orderRepositories.save(neworder);
                return ResponseEntity.status(HttpStatus.OK).body(saveorder);
            }

            logger.error("Предыдущее состояние машины не Control.Текщее состояние " + sm.getState().getId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Предыдущее состояние машины не Control.Текщее состояние " + sm.getState().getId()+"\n" +
                    "PREPARATION-->PERFORMANCE-->CONTROL-->ACCEPTANCE\n" +
                    "                ^              |\n" +
                    "                |              |\n" +
                    "                |-REVISION <---\n" );
        }
        logger.error("Не удалось поменять состояние StateMchine. Нет поручения с id " + orderid);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Не удалось поменять состояние StateMchine. Нет поручения с id " + orderid);
    }


    @Override
    public ResponseEntity secondPerform(Long orderid) {
        //Если
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //String currentPrincipalName = authentication.getName(); равен исполнителю поручения то

        var order = orderRepositories.findById(orderid);
        if (order.isPresent()) {

            var sm = build(orderid);

            if ( sm.getState().getId().equals(State.REVISION)) {

                senEvent(orderid, sm, Event.SECOND_CONTROL); //меняем состояние

                try {
                    persister.persist(sm, String.valueOf(orderid));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                var neworder = order.get();
                neworder.setState(sm.getState().getId());
                var saveorder = orderRepositories.save(neworder);
                return ResponseEntity.status(HttpStatus.OK).body(saveorder);
            }

            logger.error("Предыдущее состояние машины не Revision.Текщее состояние " + sm.getState().getId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Предыдущее состояние машины не Revision.Текщее состояние " + sm.getState().getId()+"\n" +
                    "PREPARATION-->PERFORMANCE-->CONTROL-->ACCEPTANCE\n" +
                    "                ^              |\n" +
                    "                |              |\n" +
                    "                |-REVISION <---\n" );
        }
        logger.error("Не удалось поменять состояние StateMchine. Нет поручения с id " + orderid);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Не удалось поменять состояние StateMchine. Нет поручения с id " + orderid);

    }



    @Override
    public StateMachine<State, Event> build(Long orderId) {
        Order order = orderRepositories.getReferenceById(orderId);

        var sm = stateMachineFactory.getStateMachine();

        try {
            persister.restore(sm, String.valueOf(orderId));
        } catch (Exception e) {
            e.printStackTrace();
        }

        sm.getStateMachineAccessor().doWithAllRegions(sma -> {
            sma.resetStateMachine(new DefaultStateMachineContext<>(order.getState(), null, null, null));
        });

        return sm;

    }

    @Override
    public void senEvent(Long orderId, StateMachine<State, Event> sm, Event event) {
        Message msg = MessageBuilder.withPayload(event)
                .setHeader("ORDER_ID_HEADER", orderId)
                .build();
        sm.sendEvent(msg);
    }
}
