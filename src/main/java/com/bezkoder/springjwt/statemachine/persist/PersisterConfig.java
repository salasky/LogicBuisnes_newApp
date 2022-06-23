package com.bezkoder.springjwt.statemachine.persist;

import com.bezkoder.springjwt.statemachine.event.Event;
import com.bezkoder.springjwt.statemachine.state.State;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;

@Configuration
public class PersisterConfig {

    @Bean
    @Profile({"in-memory", "default"})
    public StateMachinePersist<State, Event, String> inMemoryPersist() {
        return new InMemoryStateMachinePersist();
    }



/*
    @Profile("mongo")
    @Bean
    public StateMachineRuntimePersister<State, Event, String> mongoPersist(
            MongoDbStateMachineRepository mongoRepository) {

        return new MongoDbPersistingStateMachineInterceptor<State,Event,String>(mongoRepository);
    }
*/



    @Bean
    public StateMachinePersister<State, Event, String> persister(StateMachinePersist<State,Event,String> defaultPersist) {
        return new DefaultStateMachinePersister<>(defaultPersist);
    }



}
