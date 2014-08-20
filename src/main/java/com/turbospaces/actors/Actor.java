package com.turbospaces.actors;

public abstract class Actor {
    public abstract void onEvent(Object msg, ActorContext ctx);
}
