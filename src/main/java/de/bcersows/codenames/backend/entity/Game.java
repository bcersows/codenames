package de.bcersows.codenames.backend.entity;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.lang.NonNull;

/**
 * The list of games.
 * 
 * @author bcersows
 */
@Entity
public class Game extends AbstractEntity {
    /** The given name of the game. **/
    @NotNull
    @NotBlank
    private String name;

    /** The start team. **/
    @Enumerated(EnumType.STRING)
    @NotNull
    private PlayerTeam startTeam;

    /** The current team. **/
    @Enumerated(EnumType.STRING)
    @NotNull
    private PlayerTeam currentTeam;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Player> players = new LinkedList<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<GameField> words = new LinkedList<>();

    public Game() {
        // nothing here, but required
    }

    public Game(@NonNull final String name, @NonNull final PlayerTeam startTeam, @NonNull final List<GameField> gameFields) {
        this.name = name;
        this.startTeam = startTeam;
        this.currentTeam = startTeam;
        this.words = gameFields;
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    /**
     * @param players
     *            the players to set
     */
    public void setPlayers(final List<Player> players) {
        this.players = players;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the startTeam
     */
    public PlayerTeam getCurrentTeam() {
        return this.currentTeam;
    }

    /**
     * @param currentTeam
     *            the startTeam to set
     */
    public void setCurrentTeam(final PlayerTeam currentTeam) {
        this.currentTeam = currentTeam;
    }

    /**
     * @return the words
     */
    public List<GameField> getWords() {
        return this.words;
    }

    /**
     * @param words
     *            the words to set
     */
    public void setWords(final List<GameField> words) {
        this.words = words;
    }

    /**
     * @return the startTeam
     */
    public PlayerTeam getStartTeam() {
        return this.startTeam;
    }

    /**
     * @param startTeam
     *            the startTeam to set
     */
    public void setStartTeam(final PlayerTeam startTeam) {
        this.startTeam = startTeam;
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        }
        if (!getClass().equals(other.getClass())) {
            return false;
        }
        final Game castOther = (Game) other;
        return new EqualsBuilder().appendSuper(super.equals(other)).append(name, castOther.name).append(startTeam, castOther.startTeam)
                .append(currentTeam, castOther.currentTeam).append(players, castOther.players).append(words, castOther.words).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(name).append(startTeam).append(currentTeam).append(players).append(words)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("name", name).append("startTeam", startTeam).append("currentTeam", currentTeam)
                .append("players", players).append("words", words).toString();
    }

}