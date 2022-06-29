package com.salasky.springjwt.service.impl;

import com.salasky.springjwt.models.DTO.OrderDTO;
import com.salasky.springjwt.models.DTO.OutOrderDToEMPLOYEE;
import com.salasky.springjwt.models.DTO.OutOrderDTOAUTHOR;
import com.salasky.springjwt.models.Employee;
import com.salasky.springjwt.models.Order;
import com.salasky.springjwt.models.payload.response.MessageResponse;
import com.salasky.springjwt.repository.EmployeeRepositories;
import com.salasky.springjwt.repository.OrderRepositories;
import com.salasky.springjwt.service.OrderService;
import com.salasky.springjwt.statemachine.event.Event;
import com.salasky.springjwt.statemachine.state.State;
import com.salasky.springjwt.validator.DateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        var authorOrder=employeeRepositories.findByUsername(currentPrincipalName).get();

        if (!employeeRepositories.findByUsername(orderDTO.getExecEmployeeUsername()).isPresent()) {
            logger.error("Исполнитель поручения поручения с username " + orderDTO.getExecEmployeeUsername() + " не существует");
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Исполнитель поручения поручения с username " + orderDTO.getExecEmployeeUsername() + " не существует"));
        }

        if (!dateValidator.isValidDate(orderDTO.getPeriodExecution())) {
            logger.error("Неверный формат даты исполнения поручения");
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Неверный формат даты исполнения поручения\n" +
                            "Формат даты YYYY-MM-DD"));
        }
        List<Employee> employeesList = new ArrayList<>();
        var employee = employeeRepositories.findByUsername(orderDTO.getExecEmployeeUsername()).get();
        employeesList.add(employee);

        var order = new Order(orderDTO.getSubject(), orderDTO.getPeriodExecution(), orderDTO.getSignControl(), orderDTO.getOrderText()
                , authorOrder
                , employeesList);

        var sm = stateMachineFactory.getStateMachine();
        try {
            persister.persist(sm, String.valueOf(order.getId()));
            order.setState(sm.getState().getId());//восстанавливаем состояние см, возможно не надо дополнительно хранить state в бд
            var saveOrder = orderRepositories.save(order);
            logger.info("Создано поручение с id " + saveOrder.getId());
            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Создано поручение с id"+ saveOrder.getId()));

        } catch (Exception e) {
            logger.error("Не удалось сохранить состояние StateMachine");
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Не удалось сохранить состояние StateMachine"));
        }
    }
    private Order setter(OrderDTO orderDTO,Order order,Employee authorOrder){
        order.setOrderText(orderDTO.getOrderText());
        order.setAuthEmployee(authorOrder);
        List<Employee> employees=new ArrayList<>();
        employees.add(employeeRepositories.findByUsername(orderDTO.getExecEmployeeUsername()).get());
        order.setExecEmployee(employees);
        order.setPeriodExecution(orderDTO.getPeriodExecution());
        order.setSignControl(orderDTO.getSignControl());
        order.setSubject(orderDTO.getSubject());
        return order;
    }

    @Override
    public ResponseEntity update(OrderDTO orderDTO, Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        var authorOrder=employeeRepositories.findByUsername(currentPrincipalName).get();
            if(employeeRepositories.existsByUsername(orderDTO.getExecEmployeeUsername())){
                if(orderRepositories.existsById(id)){
                    if (!dateValidator.isValidDate(orderDTO.getPeriodExecution())) {
                        logger.error("Неверный формат даты исполнения поручения");
                        return ResponseEntity
                                .badRequest()
                                .body(new MessageResponse("Неверный формат даты исполнения поручения\n" +
                                        "Формат даты YYYY-MM-DD"));
                    }
                    var order=orderRepositories.findById(id).get();
                    logger.info("Пользователь "+currentPrincipalName+". Обновление информации о подразделении");
                    var saveOrder=setter(orderDTO,order,authorOrder);
                    orderRepositories.save(saveOrder);
                    return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Информация о подразделении успешно обновлена"));//
                }
                var order=new Order();
                order.setId(id);

                orderRepositories.save( setter(orderDTO,order,authorOrder));

                logger.error("Пользователь "+currentPrincipalName+".Создано новое поручение с id "+id);
                return ResponseEntity.status(HttpStatus.OK).body(orderDTO);
            }
            logger.error("Не найден работник с username "+orderDTO.getExecEmployeeUsername());
             return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Не найден работник с username "+orderDTO.getExecEmployeeUsername()));
    }

    @Override
    public List<OutOrderDTOAUTHOR> getAll() {
        //в текущем функционале не используется,но добавить автора поручения (создай еще dto)
        return  orderRepositories.findAll().stream().map(order -> new OutOrderDTOAUTHOR(order.getId(),order.getSubject(),order.getPeriodExecution(),order.getSignControl(),
                order.getOrderText(),employeeRepositories.findByUsername(order.getExecEmployee().get(0).getUsername()).get().getUsername(),order.getState())).collect(Collectors.toList());

    }

    @Override
    public ResponseEntity getById(Long id) {///Ищем по id , но кладем исполнителя поруения в тело
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        var orders=orderRepositories.findById(id);

        if(orders.isPresent()){
            var order=orders.get();
            logger.info("Пользователь "+currentPrincipalName+ ".Выдача инфрмации о поручении");
            var orderDTOAUTHOR=new OutOrderDTOAUTHOR(order.getId(),order.getSubject(),order.getPeriodExecution(),order.getSignControl(),
                    order.getOrderText(),order.getExecEmployee().get(0).getUsername(),order.getState());
            return ResponseEntity.status(HttpStatus.OK).body(orderDTOAUTHOR); ///
        }
        logger.error("Пользователь "+currentPrincipalName+".Не удалось найти поручение с id "+id);
        return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Не удалось найти поручение с id "+id));
    }

    @Override
    public ResponseEntity getByIdE(Long id) { ///Ищем по id , но кладем автора поруения в тело
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        var orders=orderRepositories.findById(id);

        if(orders.isPresent()){
            var order=orders.get();
            logger.info("Пользователь "+currentPrincipalName+ ".Выдача инфрмации о поручении");
            var orderDTOEmployee=new OutOrderDToEMPLOYEE(order.getId(),order.getSubject(),order.getPeriodExecution(),order.getSignControl(),
                    order.getOrderText(),order.getAuthEmployee().getUsername(),order.getState());
            return ResponseEntity.status(HttpStatus.OK).body(orderDTOEmployee); ///
        }
        logger.error("Пользователь "+currentPrincipalName+".Не удалось найти поручение с id "+id);
        return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Не удалось найти поручение с id "+id));
    }

    @Override
    public List<OutOrderDTOAUTHOR> getAuthorOrder() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        var emp=employeeRepositories.findByUsername(currentPrincipalName);
        if(emp.isPresent()){
            var ordersList=orderRepositories.findAllByEmployeeAuthor(emp.get());

           return ordersList.stream().map(order -> new OutOrderDTOAUTHOR(order.getId(),order.getSubject(),order.getPeriodExecution(),order.getSignControl(),
                    order.getOrderText(),employeeRepositories.findByUsername(order.getExecEmployee().get(0).getUsername()).get().getUsername(),order.getState())).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public List<OutOrderDToEMPLOYEE> getExecutionOrderMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();


        var orderList=orderRepositories.findOrderByExecEmployee(currentPrincipalName);
        return orderList.stream().map(order -> new OutOrderDToEMPLOYEE(order.getId(),order.getSubject(),order.getPeriodExecution(),order.getSignControl(),
                order.getOrderText(),employeeRepositories.findByUsername(order.getAuthEmployee().getUsername()).get().getUsername(),order.getState())).collect(Collectors.toList());

    }

    @Override
    public List<OutOrderDTOAUTHOR> findOrderBySubject(String subject) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        return  orderRepositories.findOrderBySubject(subject).stream().map(order->new OutOrderDTOAUTHOR(order.getId(),order.getSubject(),order.getPeriodExecution(),order.getSignControl(),
                order.getOrderText(),employeeRepositories.findByUsername(order.getExecEmployee().get(0).getUsername()).get().getUsername(),order.getState())).collect(Collectors.toList());

    }

    @Override
    public ResponseEntity delete(Long id) {
        if(orderRepositories.existsById(id)){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentPrincipalName = authentication.getName();
           if(orderRepositories.findById(id).get().getAuthEmployee().getUsername().equals(currentPrincipalName)){
               orderRepositories.deleteById(id);
               logger.info("Поручение  с id "+id+ " удалено");
               return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Информация о подразделении успешно удалена"));//
           }
            logger.info("Попытка удаления поручения");
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Поручение  может удалить только автор"));
        }
        logger.error("Удаление.Поручение  с id "+id+ " не найдено");
        return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Поручение  с id "+id+ " не найдено"));
    }

    @Override
    public ResponseEntity performanceState(Long orderid) {

        var order = orderRepositories.findById(orderid);
        if (order.isPresent()) {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentPrincipalName = authentication.getName();

            //В стутус в исполнении могут перевести только исполнители поручения..реализовать
            var empList = order.get().getExecEmployee();
            if (empList.contains(employeeRepositories.findByUsername(currentPrincipalName).get())) {
                var sm = build(orderid);

                //в статус исполнении можем перейти только с состояния PREPARATION
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
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Предыдущее состояние машины не Preparation.Текщее состояние "+ sm.getState().getId()+"\n" +
                        "PREPARATION-->PERFORMANCE-->CONTROL-->ACCEPTANCE\n" +
                        "                ^              |\n" +
                        "                |              |\n" +
                        "                |-REVISION <---\n" ));
            }
            logger.error("Попытка изменить состояние поручения в исполнении не исполнителем поручения");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Не удалось поменять состояние StateMchine. Нет поручения с id " + orderid));
        }
        logger.error("Не удалось поменять состояние StateMchine. Нет поручения с id " + orderid);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Не удалось поменять состояние StateMchine. Нет поручения с id " + orderid));
    }

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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Предыдущее состояние машины не Performance.Текщее состояние " + sm.getState().getId()+"\n" +
                    "PREPARATION-->PERFORMANCE-->CONTROL-->ACCEPTANCE\n" +
                    "                ^              |\n" +
                    "                |              |\n" +
                    "                |-REVISION <---\n" ));
        }
        logger.error("Не удалось поменять состояние StateMchine. Нет поручения с id " + orderid);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Не удалось поменять состояние StateMchine. Нет поручения с id " + orderid));
    }

    //В статус принятие меняет только автор поручения..реализовать
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Предыдущее состояние машины не Control.Текщее состояние " + sm.getState().getId()+"\n" +
                    "PREPARATION-->PERFORMANCE-->CONTROL-->ACCEPTANCE\n" +
                    "                ^              |\n" +
                    "                |              |\n" +
                    "                |-REVISION <---\n" ));
        }
        logger.error("Не удалось поменять состояние StateMchine. Нет поручения с id " + orderid);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Не удалось поменять состояние StateMchine. Нет поручения с id " + orderid));
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Предыдущее состояние машины не Control.Текщее состояние " + sm.getState().getId()+"\n" +
                    "PREPARATION-->PERFORMANCE-->CONTROL-->ACCEPTANCE\n" +
                    "                ^              |\n" +
                    "                |              |\n" +
                    "                |-REVISION <---\n" ));
        }
        logger.error("Не удалось поменять состояние StateMchine. Нет поручения с id " + orderid);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Не удалось поменять состояние StateMchine. Нет поручения с id " + orderid));
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Предыдущее состояние машины не Revision.Текщее состояние " + sm.getState().getId()+"\n" +
                    "PREPARATION-->PERFORMANCE-->CONTROL-->ACCEPTANCE\n" +
                    "                ^              |\n" +
                    "                |              |\n" +
                    "                |-REVISION <---\n" ));
        }
        logger.error("Не удалось поменять состояние StateMchine. Нет поручения с id " + orderid);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Не удалось поменять состояние StateMchine. Нет поручения с id " + orderid));

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
