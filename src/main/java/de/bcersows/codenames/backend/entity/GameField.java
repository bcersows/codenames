package de.bcersows.codenames.backend.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * A single field of a game.
 * 
 * @author bcersows
 */
@Entity
public class GameField extends AbstractEntity {
    /** The actual word. **/
    @NotNull
    @NotBlank
    private String word;

    /** The team this belongs to. Null if grey. **/
    @Enumerated(EnumType.STRING)
    @Nullable
    private PlayerTeam team;

    /** If flipped open already. **/
    private boolean flipped;

    /** If this is the killer card. **/
    private boolean killer;

    protected GameField() {
        // nothing
    }

    /** Create with word. **/
    public GameField(@NonNull final String word) {
        this.word = word;
    }

    /** Create with values. **/
    protected GameField(@NonNull final String word, @Nullable final PlayerTeam team, final boolean killer) {
        this.word = word;
        this.team = team;
        this.killer = killer;
    }

    public static GameField createTeamGameField(@NonNull final String word, @NonNull final PlayerTeam team) {
        return new GameField(word, team, false);
    }

    public static GameField createGreyGameField(@NonNull final String word) {
        return new GameField(word, null, false);
    }

    public static GameField createKillerGameField(@NonNull final String word) {
        return new GameField(word, null, true);
    }

    /**
     * @return the word
     */
    public String getWord() {
        return this.word;
    }

    /**
     * @return the team
     */
    public PlayerTeam getTeam() {
        return this.team;
    }

    /**
     * @return the flipped
     */
    public boolean isFlipped() {
        return this.flipped;
    }

    /**
     * @return the killer
     */
    public boolean isKiller() {
        return this.killer;
    }

    /**
     * @param word
     *            the word to set
     */
    public void setWord(final String word) {
        this.word = word;
    }

    /**
     * @param team
     *            the team to set
     */
    public void setTeam(final PlayerTeam team) {
        this.team = team;
    }

    /**
     * @param flipped
     *            the flipped to set
     */
    public void setFlipped(final boolean flipped) {
        this.flipped = flipped;
    }

    /**
     * @param killer
     *            the killer to set
     */
    public void setKiller(final boolean killer) {
        this.killer = killer;
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        }
        if (!getClass().equals(other.getClass())) {
            return false;
        }
        final GameField castOther = (GameField) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(word, castOther.word).append(team, castOther.team).append(flipped, castOther.flipped)
                .append(killer, castOther.killer).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(word).append(team).append(flipped).append(killer).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("word", word).append("team", team).append("flipped", flipped)
                .append("killer", killer).toString();
    }

}
