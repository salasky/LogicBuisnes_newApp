package com.salasky.springjwt;

import com.salasky.springjwt.statemachine.event.Event;
import com.salasky.springjwt.statemachine.state.State;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.test.StateMachineTestPlan;
import org.springframework.statemachine.test.StateMachineTestPlanBuilder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("in-memory")
class StateMachineTestinMemory {

    @Autowired
    private StateMachineFactory<State, Event> stateMachineFactory;

    @Autowired
    private StateMachinePersister<State,Event,String> persister;


    @DisplayName("Восстановление по orderId")
    @Test
    void testRestorByOrderId() throws Exception {
        Long orderId=1212L;
        var machine=stateMachineFactory.getStateMachine(orderId.toString());

        Assertions.assertEquals(machine.getState().getId(),State.PREPARATION);

        machine.getExtendedState().getVariables().putIfAbsent("orderId",orderId);
        Assertions.assertEquals(orderId,machine.getExtendedState().getVariables().get("orderId"));

        machine.sendEvent(Event.START);
        persister.persist(machine,orderId.toString());
        machine.stop();

        var machine2=persister.restore(stateMachineFactory.getStateMachine(),"1212");
        Assertions.assertEquals(machine2.getState().getId(),State.PERFORMANCE);

        Assertions.assertEquals(orderId,machine2.getExtendedState().getVariables().get("orderId"));

    }

    @DisplayName("Проверка цепочки State")
    @Test
    void testInit() throws Exception {

        StateMachine<State, Event> machine=stateMachineFactory.getStateMachine();
        Assertions.assertNotNull(machine);
        Assertions.assertEquals(machine.getState().getId(),State.PREPARATION);

        StateMachineTestPlan<State, Event> plan=
                StateMachineTestPlanBuilder.<State, Event>builder()
                        .defaultAwaitTime(2)
                        .stateMachine(machine)
                        .step()
                        .expectStates(State.PREPARATION)
                        .expectStateChanged(0)

                        .and()
                        .step()
                        .sendEvent(Event.START)
                        .expectStates(State.PERFORMANCE)
                        .expectStateChanged(1)

                        .and()
                        .step()
                        .sendEvent(Event.FIRST_CONTROL)
                        .expectState(State.CONTROL)
                        .expectStateChanged(1)

                        .and()
                        .step()
                        .sendEvent(Event.InternalFailed)
                        .sendEvent(Event.FAIL_CONTROL)
                        .expectState(State.REVISION)
                        .expectStateChanged(1)

                        .and()
                        .step()
                        .sendEvent(Event.SECOND_CONTROL)
                        .expectState(State.PERFORMANCE)
                        .expectStateChanged(1)

                        .and()
                        .step()
                        .sendEvent(Event.FIRST_CONTROL)
                        .expectState(State.CONTROL)
                        .expectStateChanged(1)

                        .and()
                        .step()
                        .sendEvent(Event.InternalSuccess)
                        .sendEvent(Event.SUCCESS)
                        .expectState(State.ACCEPTANCE)
                        .expectStateChanged(1)
                        .and()

                        .build();
        plan.test();

    }
    @DisplayName("Проверка persist")
    @Test
    void testPersist() throws Exception {

        StateMachine <State, Event> machine1=stateMachineFactory.getStateMachine();
        StateMachine <State, Event> machine2=stateMachineFactory.getStateMachine();
        StateMachine <State, Event> machine3=stateMachineFactory.getStateMachine();

        machine1.sendEvent(Event.START);
        /*machine1.sendEvent(Event.FIRST_CONTROL);*/
        machine1.sendEvent(MessageBuilder.withPayload(Event.FIRST_CONTROL).setHeader("1","a").build());

        //action-->succeesControl=true
        machine1.sendEvent(Event.InternalSuccess);

        persister.persist(machine1, "1");

        persister.restore(machine2,"1");

        //State восстановился
        Assertions.assertEquals(machine2.getState().getId(),State.CONTROL);
        //Контекст восстановился
        Assertions.assertTrue((Boolean) machine2.getExtendedState().getVariables().get("succeesControl"));

        machine3.sendEvent(Event.START);machine3.sendEvent(Event.FIRST_CONTROL);machine3.sendEvent(Event.InternalSuccess);
        machine3.sendEvent(Event.SUCCESS);
        persister.persist(machine3,"1");

        persister.restore(machine2,"1");
        Assertions.assertEquals(machine2.getState().getId(),State.ACCEPTANCE);

        persister.restore(machine2,"");
        Assertions.assertEquals(machine2.getState().getId(),State.PREPARATION);
    }

    @DisplayName("Проверкee persist")
    @Test
    void testPerseist() throws Exception {

        StateMachine <State, Event> machine1=stateMachineFactory.getStateMachine("1");
        machine1.sendEvent(Event.START);

        StateMachine <State, Event> machine2=stateMachineFactory.getStateMachine("1");



        //State восстановился
        Assertions.assertEquals(machine1.getState().getId(),State.PERFORMANCE);



    }


}
