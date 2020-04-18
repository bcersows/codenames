package de.bcersows.codenames.backend;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vaadin.flow.shared.Registration;

import de.bcersows.codenames.ui.views.game.event.GameUpdateEvent;

/** The broadcaster service. **/
@Service
public class CodenamesBroadcasterService {
    private static final Logger LOG = LoggerFactory.getLogger(CodenamesBroadcasterService.class);

    private final Executor executor = Executors.newSingleThreadExecutor();

    private final List<Consumer<GameUpdateEvent>> gameUpdateEventlisteners = new CopyOnWriteArrayList<>();

    public synchronized Registration registerGameUpdateEventListener(final Consumer<GameUpdateEvent> listener) {
        gameUpdateEventlisteners.add(listener);

        return () -> {
            synchronized (CodenamesBroadcasterService.class) {
                gameUpdateEventlisteners.remove(listener);
            }
        };
    }

    public synchronized void broadcast(final GameUpdateEvent gameUpdateEvent) {
        LOG.trace("Broadcasting message: {}.", gameUpdateEvent);
        for (final Consumer<GameUpdateEvent> listener : gameUpdateEventlisteners) {
            executor.execute(() -> listener.accept(gameUpdateEvent));
        }
    }
}