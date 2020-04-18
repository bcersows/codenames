package de.bcersows.codenames.backend.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.lang.NonNull;

/**
 * The players. A game can have multiple players.
 * 
 * @author bcersows
 */
@Entity
public class Player extends AbstractEntity {
    /** The name of the player. **/
    @NotNull
    @NotEmpty
    private String name;

    /** The game this player is assigned to. **/
    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    /** The team of the player. **/
    @Enumerated(EnumType.STRING)
    @NotNull
    private PlayerTeam playerTeam;

    public Player() {
        // nothing here, but required
    }

    /**
     * Create a user with the given information.
     */
    public Player(@NonNull final String name, @NonNull final PlayerTeam playerTeam) {
        this.name = name;
        this.playerTeam = playerTeam;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the game
     */
    public Game getGame() {
        return this.game;
    }

    /**
     * @return the playerTeam
     */
    public PlayerTeam getPlayerTeam() {
        return this.playerTeam;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @param game
     *            the game to set
     */
    public void setGame(final Game game) {
        this.game = game;
    }

    /**
     * @param playerTeam
     *            the playerTeam to set
     */
    public void setPlayerTeam(final PlayerTeam playerTeam) {
        this.playerTeam = playerTeam;
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        }
        if (!getClass().equals(other.getClass())) {
            return false;
        }
        final Player castOther = (Player) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(name, castOther.name).append(game, castOther.game)
                .append(playerTeam, castOther.playerTeam).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(name).append(game).append(playerTeam).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("name", name).append("game", game).append("playerTeam", playerTeam).toString();
    }

}
