package com.salasky.springjwt.statemachine.listener;

import com.salasky.springjwt.statemachine.event.Event;
import com.salasky.springjwt.statemachine.state.State;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.transition.Transition;

import java.util.Optional;

public class OrderStateMachineApplicationListener implements StateMachineListener<State, Event> {

    private Object ofNullableState(org.springframework.statemachine.state.State s) {
        return Optional.ofNullable(s)
                .map(org.springframework.statemachine.state.State::getId)
                .orElse(null);
    }

    @Override
    public void stateChanged(org.springframework.statemachine.state.State<State, Event> state, org.springframework.statemachine.state.State<State, Event> state1) {

           /* System.out.println("Переход из статуса " + ofNullableState(state) + " в статус " + ofNullableState(state1));*/

    }


    @Override
    public void stateEntered(org.springframework.statemachine.state.State<State, Event> state) {

    }

    @Override
    public void stateExited(org.springframework.statemachine.state.State<State, Event> state) {

    }

    @Override
    public void eventNotAccepted(Message<Event> message) {
        System.out.println("Евент не принят " + message);
    }

    @Override
    public void transition(Transition<State, Event> transition) {
        System.out.println("Переход из   :"+ofNullableState(transition.getSource())+ " в: "+ofNullableState(transition.getTarget())+"");


    }

    @Override
    public void transitionStarted(Transition<State, Event> transition) {

    }

    @Override
    public void transitionEnded(Transition<State, Event> transition) {

    }

    @Override
    public void stateMachineStarted(StateMachine<State, Event> stateMachine) {
        System.out.println("Machine started");
    }

    @Override
    public void stateMachineStopped(StateMachine<State, Event> stateMachine) {
        System.out.println("Machine stop");
    }

    @Override
    public void stateMachineError(StateMachine<State, Event> stateMachine, Exception e) {

    }

    @Override
    public void extendedStateChanged(Object o, Object o1) {

    }

    @Override
    public void stateContext(StateContext<State, Event> stateContext) {

    }
}
