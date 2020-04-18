package de.bcersows.codenames.ui.views.game.event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.lang.NonNull;

import de.bcersows.codenames.backend.entity.PlayerTeam;

/**
 * Update event indicating a game turn.
 * 
 * @author bcersows
 */
public class GameTurnEvent extends GameUpdateEvent {
    private static final long serialVersionUID = -6579395291035069070L;

    /** The new team. **/
    @NonNull
    private PlayerTeam nextTeam;

    /**
     * Create an instance.
     */
    public GameTurnEvent(final long gameId, @NonNull final PlayerTeam nextTeam) {
        super(gameId);

        this.nextTeam = nextTeam;
    }

    /**
     * @param nextTeam
     *            the nextTeam to set
     */
    public void setNextTeam(final PlayerTeam nextTeam) {
        this.nextTeam = nextTeam;
    }

    /**
     * @return the newTeam
     */
    public PlayerTeam getNextTeam() {
        return this.nextTeam;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("nextTeam", nextTeam).toString();
    }
}
