package com.turbospaces.actors.impl;

import com.turbospaces.actors.Actor;
import com.turbospaces.actors.ActorRef;

class DefaultActorRef<T extends Actor> implements ActorRef<T> {
    public void tell(Object msg, ActorRef<?> who) {}
}
