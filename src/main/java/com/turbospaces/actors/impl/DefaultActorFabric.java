package com.turbospaces.actors.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.turbospaces.actors.Actor;
import com.turbospaces.actors.ActorFabric;
import com.turbospaces.actors.ActorRef;

public class DefaultActorFabric implements ActorFabric {
    private final ActorRef<Actor> anon;
    private final ExecutorService exec;

    public DefaultActorFabric(int threads) {
        anon = (msg, who) -> {};
        exec = Executors.newFixedThreadPool( threads );
    }
    @Override
    public <T extends Actor> ActorRef<T> create(final Class<T> clazz) throws Exception {
        final T actor = clazz.newInstance();
        final DefaultActorCtx ctx = new DefaultActorCtx( actor, exec );

        return (msg, who) -> ctx.onEvent( msg, who );
    }
    @Override
    public ActorRef<Actor> anonymous() {
        return anon;
    }
    @Override
    public void close() {
        exec.shutdown();
    }
}
