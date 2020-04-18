package de.bcersows.codenames.ui.views.game.event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.lang.Nullable;

import de.bcersows.codenames.backend.entity.PlayerTeam;

/**
 * Event for a game field update.
 * 
 * @author bcersows
 */
public class GameFieldUpdateEvent extends GameUpdateEvent {
    private static final long serialVersionUID = 1985318199383074481L;

    private long gameFieldId;

    private PlayerTeam fieldTeam;

    private boolean killerField;

    public GameFieldUpdateEvent(final long gameId, final long gameFieldId, @Nullable final PlayerTeam fieldTeam, final boolean killerField) {
        super(gameId);

        this.gameFieldId = gameFieldId;
        this.fieldTeam = fieldTeam;
        this.killerField = killerField;
    }

    /**
     * @return the gameFieldId
     */
    public long getGameFieldId() {
        return this.gameFieldId;
    }

    /**
     * @param gameFieldId
     *            the gameFieldId to set
     */
    public void setGameFieldId(final long gameFieldId) {
        this.gameFieldId = gameFieldId;
    }

    /**
     * @return the fieldTeam
     */
    public PlayerTeam getFieldTeam() {
        return this.fieldTeam;
    }

    /**
     * @return the killerField
     */
    public boolean isKillerField() {
        return this.killerField;
    }

    /**
     * @param fieldTeam
     *            the fieldTeam to set
     */
    public void setFieldTeam(final PlayerTeam fieldTeam) {
        this.fieldTeam = fieldTeam;
    }

    /**
     * @param killerField
     *            the killerField to set
     */
    public void setKillerField(final boolean killerField) {
        this.killerField = killerField;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("gameFieldId", gameFieldId).append("fieldTeam", fieldTeam)
                .append("killerField", killerField).toString();
    }

}
