package de.bcersows.codenames.ui.views.game;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;

import de.bcersows.codenames.backend.entity.GameField;
import de.bcersows.codenames.backend.entity.PlayerTeam;
import de.bcersows.codenames.ui.views.game.event.GameFieldUpdateEvent;
import de.bcersows.codenames.ui.views.game.event.GameUpdateEventListener;

/**
 * @author bcersows
 */
@Tag(Tag.DIV)
public class CodenamesGameField extends Component implements HasSize {
    private static final long serialVersionUID = -4395887144914598647L;

    private static final Logger LOG = LoggerFactory.getLogger(CodenamesGameField.class);

    /** Property name on an element containing the field ID. **/
    private static final String ELEMENT_PROPERTY_NAME_FIELD_ID = "FIELDID";

    /** CSS class name for the game field if someone won. **/
    private static final String CSS_CLASSNAME_GAME_FIELD = "game-field";
    /** CSS class name for the game field if someone won. **/
    private static final String CSS_CLASSNAME_GAME_WON = "game-won";
    /** CSS class name for a game field cell. **/
    private static final String CSS_CLASSNAME_GAME_FIELD_CELL = "game-field-cell";
    /** CSS base class name for a game field cell with more information. **/
    private static final String CSS_CLASSNAME_GAME_FIELD_CELL_APPENDUM = CSS_CLASSNAME_GAME_FIELD_CELL + "-";
    /** CSS class name for a flipped game field cell. **/
    private static final String CSS_CLASSNAME_GAME_FIELD_CELL_FLIPPED = CSS_CLASSNAME_GAME_FIELD_CELL_APPENDUM + "flipped";
    /** CSS class name for a killer game field cell. **/
    private static final String CSS_CLASSNAME_GAME_FIELD_CELL_KILLER = CSS_CLASSNAME_GAME_FIELD_CELL_APPENDUM + "killer";

    /** The area containing the fields. **/
    private final Div fields;

    /** The event listener to send game updates to. **/
    @Nullable
    private GameUpdateEventListener gameUpdateEventListener;

    /** If game is displaying as spy master. **/
    private boolean isSpyMaster;

    /** The current game ID. **/
    private long gameId;

    public CodenamesGameField() {
        this.fields = new Div();
        this.fields.setClassName(CSS_CLASSNAME_GAME_FIELD);

        this.getElement().appendChild(fields.getElement());
    }

    /**
     * Clear the whole field.
     */
    public void clearField() {
        this.fields.removeAll();

        setSpyMaster(false);
    }

    /** Set the game update event listener. Will be notified about game updates from the component. **/
    public void setGameUpdateEventListener(@NonNull final GameUpdateEventListener gameUpdateEventListener) {
        this.gameUpdateEventListener = gameUpdateEventListener;
    }

    public boolean toggleSpyMaster() {
        final boolean newState = !this.isSpyMaster;
        this.setSpyMaster(newState);
        return newState;
    }

    public void setSpyMaster(final boolean isSpyMaster) {
        this.isSpyMaster = isSpyMaster;
        this.fields.setClassName("game-field-spymaster", isSpyMaster);
    }

    /**
     * Set the field definitions. Required to display the game.
     */
    public void setFieldDefinition(final long gameId, @NonNull final List<GameField> words) {
        this.gameId = gameId;
        // clear the field
        this.clearField();

        words.stream().forEachOrdered(this::addField);
    }

    /**
     * Set the field status based on an update event.
     */
    public void setFieldStatus(@NonNull final GameFieldUpdateEvent gameFieldUpdateEvent) {
        if (this.gameId == gameFieldUpdateEvent.getGameId()) {
            this.fields.getChildren()
                    .filter(component -> StringUtils.equals(component.getElement().getProperty(ELEMENT_PROPERTY_NAME_FIELD_ID),
                            Long.toString(gameFieldUpdateEvent.getGameFieldId())))
                    .filter(component -> component instanceof HasStyle).map(component -> (HasStyle) component).findFirst().ifPresent(field -> {
                        field.addClassName(CSS_CLASSNAME_GAME_FIELD_CELL_FLIPPED);

                        addConditionalFieldStyles(field, gameFieldUpdateEvent.getFieldTeam(), gameFieldUpdateEvent.isKillerField());
                    });
        }
    }

    /**
     * Mark a game as won.
     */
    public void setWon(@NonNull final PlayerTeam winnerTeam) {
        final String cssClassNameWinnerTeam = CSS_CLASSNAME_GAME_WON + "-" + winnerTeam.name().toLowerCase(Locale.UK);
        this.fields.addClassNames(CSS_CLASSNAME_GAME_WON, cssClassNameWinnerTeam);
    }

    /** Add a field. **/
    private void addField(@NonNull final GameField gameField) {
        final Div field = new Div();
        field.setText(gameField.getWord());
        field.getElement().setProperty(ELEMENT_PROPERTY_NAME_FIELD_ID, gameField.getId());

        // base CSS class
        field.setClassName(CSS_CLASSNAME_GAME_FIELD_CELL);

        // depending on if flipped or not, add/remove class name
        field.setClassName(CSS_CLASSNAME_GAME_FIELD_CELL_FLIPPED, gameField.isFlipped());

        // TODO codenames: put into onClick method
        // add conditional class names
        addConditionalFieldStyles(field, gameField.getTeam(), gameField.isKiller());

        field.addClickListener(clickEvent -> {
            LOG.debug("Field clicked: {}", gameField.getWord());

            // TODO codenames: need to persist to DB and send outputs
            if (null != this.gameUpdateEventListener) {
                this.gameUpdateEventListener
                        .onGameUpdateEvent(new GameFieldUpdateEvent(this.gameId, gameField.getId(), gameField.getTeam(), gameField.isKiller()));
            } else {
                LOG.warn("No game update event listener set. Cannot notify peers.");
            }
        });

        // TODO codenames eventually save in map as well so can better handle removing of the listener and updating the values?
        // add to the UI
        this.fields.add(field);
    }

    /** Add the conditional game field styles to the given component. **/
    private void addConditionalFieldStyles(@NonNull final HasStyle component, @Nullable final PlayerTeam playerTeam, final boolean isKiller) {
        if (null != playerTeam) {
            component.addClassName(CSS_CLASSNAME_GAME_FIELD_CELL_APPENDUM + playerTeam.name().toLowerCase(Locale.UK));
        } else if (isKiller) {
            component.addClassName(CSS_CLASSNAME_GAME_FIELD_CELL_KILLER);
        }
    }

}
