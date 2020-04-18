package de.bcersows.codenames.backend.entity;

import org.springframework.lang.NonNull;

/**
 * Possible teams of a player.
 * 
 * @author bcersows
 */
public enum PlayerTeam {
    /** Red team. **/
    RED,
    /** Blue team. **/
    BLUE;

    /** Get the next team. **/
    @NonNull
    public PlayerTeam nextTeam() {
        return this == RED ? BLUE : RED;
    }
}
