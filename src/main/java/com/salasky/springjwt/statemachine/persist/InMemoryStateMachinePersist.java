package com.salasky.springjwt.statemachine.persist;

import com.salasky.springjwt.statemachine.event.Event;
import com.salasky.springjwt.statemachine.state.State;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import java.util.HashMap;


public class InMemoryStateMachinePersist implements StateMachinePersist<State, Event,String> {

    private HashMap<String, StateMachineContext<State, Event>> contexts = new HashMap<>();
    @Override
    public void write(StateMachineContext<State, Event> stateMachineContext, String s) throws Exception {
        contexts.put(s,stateMachineContext);
    }

    @Override
    public StateMachineContext<State, Event> read(String s) throws Exception {
        return contexts.get(s);
    }

}
