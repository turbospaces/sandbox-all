package com.turbospaces.actors;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.turbospaces.actors.impl.DefaultActorFabric;

public class MyActor extends Actor {
    @Override
    public void onEvent(Object msg, ActorContext ctx) {
        CompletableFuture<MyActor> f = new CompletableFuture<>();
        ctx.pauseUntilComplete( f );

        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.execute( () -> {
            try {
                Thread.sleep( 10 );
            }
            catch ( InterruptedException e ) {
                Thread.currentThread().interrupt();
            }
            finally {
                f.complete( MyActor.this );
            }
        } );
        exec.shutdown();
    }

    public static void main(String... args) throws Exception {
        DefaultActorFabric f = new DefaultActorFabric( Runtime.getRuntime().availableProcessors() );
        ActorRef<Actor> sender = f.anonymous();
        ActorRef<MyActor> actorRef = f.create( MyActor.class );
        actorRef.tell( "xxx", sender );

        Thread.sleep( 1000 );
        f.close();
    }
}
