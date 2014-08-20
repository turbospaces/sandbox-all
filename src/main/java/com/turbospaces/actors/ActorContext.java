package com.turbospaces.actors;

import java.util.concurrent.CompletableFuture;

public interface ActorContext {
    ActorRef<?> getSender();
    void pauseUntilComplete(CompletableFuture<?> f);
}
