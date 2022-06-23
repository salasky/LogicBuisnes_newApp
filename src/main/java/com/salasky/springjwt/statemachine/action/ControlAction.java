package com.salasky.springjwt.statemachine.action;


import com.salasky.springjwt.statemachine.event.Event;
import com.salasky.springjwt.statemachine.state.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

public class ControlAction implements Action<State, Event> {
    Logger logger = LoggerFactory.getLogger(ControlAction .class);
    @Override
    public void execute(StateContext<State, Event> stateContext) {
 /*       final String orderId = stateContext.getExtendedState().get("Order_ID", String.class);
        logger.info("Поручение с номером " + orderId + " проверяется");*/



    }
}
