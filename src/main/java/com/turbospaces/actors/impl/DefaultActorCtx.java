package com.turbospaces.actors.impl;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import com.turbospaces.actors.Actor;
import com.turbospaces.actors.ActorContext;
import com.turbospaces.actors.ActorRef;

class DefaultActorCtx {
    private final Queue<TaskWrapper> queue = new LinkedList<>();
    private TaskWrapper activeTask;
    private final Actor actor;
    private final ExecutorService executor;

    public DefaultActorCtx(Actor actor, ExecutorService executor) {
        this.actor = actor;
        this.executor = executor;
    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void onEvent(final Object msg, final ActorRef<?> who) {
        CompletableFuture f = new CompletableFuture();
        enq( new TaskWrapper( f, () -> {
            AtomicBoolean syncMode = new AtomicBoolean();
            actor.onEvent( msg, new ActorContext() {
                @Override
                public void pauseUntilComplete(CompletableFuture<?> async) {
                    syncMode.set( false );
                    async.thenRun( () -> {
                        f.complete( DefaultActorCtx.this );
                    } );
                }
                @Override
                public ActorRef<?> getSender() {
                    return who;
                }
            } );

            if ( syncMode.get() ) {
                f.complete( DefaultActorCtx.this );
            }
        } ) );
    }

    private synchronized void enq(TaskWrapper wrapper) {
        queue.add( wrapper );
        if ( activeTask == null ) {
            runNext();
        }
    }
    private synchronized void runNext() {
        activeTask = queue.poll();
        if ( activeTask != null ) {
            executor.execute( activeTask );
        }
    }

    private class TaskWrapper implements Runnable {
        private final Runnable r;
        private final CompletableFuture<?> f;

        private TaskWrapper(CompletableFuture<?> f, Runnable r) {
            this.f = f;
            this.r = r;
        }

        @Override
        public void run() {
            try {
                r.run();
            }
            finally {
                f.whenComplete( (r, e) -> {
                    runNext();
                } );
            }
        }
    }
}
