package com.turbospaces.actors;

import java.io.Closeable;

public interface ActorFabric extends Closeable {
    <T extends Actor> ActorRef<T> create(Class<T> clazz) throws Exception;
    ActorRef<Actor> anonymous();
}
