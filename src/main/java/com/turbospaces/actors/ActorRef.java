package com.turbospaces.actors;

public interface ActorRef<T extends Actor> {
    void tell(Object msg, ActorRef<?> who);
}
