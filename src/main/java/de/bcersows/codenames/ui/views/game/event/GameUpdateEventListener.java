package de.bcersows.codenames.ui.views.game.event;

import org.springframework.lang.NonNull;

/**
 * @author bcersows
 */
public interface GameUpdateEventListener {
    void onGameUpdateEvent(@NonNull GameUpdateEvent gameUpdateEvent);
}
