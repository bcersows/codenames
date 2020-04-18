package de.bcersows.codenames.ui.views.game.event;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.lang.NonNull;

/**
 * @author bcersows
 */
public class GameUpdateEvent implements Serializable {
    private static final long serialVersionUID = 8164005222676940042L;

    private long gameId;

    protected GameUpdateEvent() {
        // nothing
    }

    public GameUpdateEvent(@NonNull final long gameId) {
        this.gameId = gameId;
    }

    /**
     * @return the gameId
     */
    public long getGameId() {
        return this.gameId;
    }

    /**
     * @param gameId
     *            the gameId to set
     */
    public void setGameId(final long gameId) {
        this.gameId = gameId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("gameId", gameId).toString();
    }
}
