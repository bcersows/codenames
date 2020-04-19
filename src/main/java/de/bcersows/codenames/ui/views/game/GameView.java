package de.bcersows.codenames.ui.views.game;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.WildcardParameter;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.UIScope;

import de.bcersows.codenames.backend.CodenamesBroadcasterService;
import de.bcersows.codenames.backend.entity.Game;
import de.bcersows.codenames.backend.entity.GameField;
import de.bcersows.codenames.backend.entity.PlayerTeam;
import de.bcersows.codenames.backend.service.GameService;
import de.bcersows.codenames.ui.CodenamesMainLayout;
import de.bcersows.codenames.ui.CodenamesUpdateView;
import de.bcersows.codenames.ui.views.game.event.GameFieldUpdateEvent;
import de.bcersows.codenames.ui.views.game.event.GameTurnEvent;

/**
 * The view for a running game.
 * 
 * @author bcersows
 */
@Component
@UIScope
@Route(value = "game", layout = CodenamesMainLayout.class)
@PageTitle("Codenames")
public class GameView extends VerticalLayout implements CodenamesUpdateView, HasUrlParameter<String> {
    private static final long serialVersionUID = -8248059258400016514L;

    private static final Logger LOG = LoggerFactory.getLogger(GameView.class);

    /** Size of the game area. **/
    private static final String GAME_FIELD_SIZE = "700px";

    /** CSS class name for the whole game area. **/
    private static final String CSS_CLASSNAME_GAME = "game";
    /** CSS base class name for teams. **/
    private static final String CSS_CLASSNAME_TEAM = "team-";
    /** CSS class name for team red. **/
    private static final String CSS_CLASSNAME_TEAM_RED = CSS_CLASSNAME_TEAM + "red";
    /** CSS class name for team blue. **/
    private static final String CSS_CLASSNAME_TEAM_BLUE = CSS_CLASSNAME_TEAM + "blue";
    /** CSS class name for labels for remaining words. **/
    private static final String CSS_CLASSNAME_LABEL_REMAINING = "label-remaining";
    /** CSS class name for the game field if someone won. **/
    private static final String CSS_CLASSNAME_GAME_WON = "game-won";

    /** CSS ID for the game area. **/
    private static final String CSS_ID_GAME_AREA = "game-area";
    /** CSS ID for the player information. **/
    private static final String CSS_ID_PLAYER_INFORMATION = "player-information";
    /** CSS ID for the button to switch to next team. **/
    private static final String CSS_ID_BUTTON_NEXT_TEAM = "button-next-team";
    /** CSS ID for the label stating the current team. **/
    private static final String CSS_ID_LABEL_CURRENT_TEAM = "label-current-team";

    /** Service to access games. **/
    @NonNull
    private final transient GameService gameService;
    /** The broadcaster service. **/
    @NonNull
    private final transient CodenamesBroadcasterService broadcasterService;

    private final H3 labelGameName;
    private final CodenamesGameField gameField;

    private final Label labelCurrentTeam;
    /** The label with the remaining words for the start team. **/
    private final Label labelRemainingStart;
    /** The label with the remaining words for the other team. **/
    private final Label labelRemainingOther;

    private final Button buttonSpyMaster;
    private final Button buttonNextTeam;

    private Registration broadcasterRegistration;

    /** The current game. **/
    @Nullable
    private Game currentGame;

    @Autowired
    public GameView(@NonNull final GameService gameService, @NonNull final CodenamesBroadcasterService broadcasterService) {
        this.gameService = gameService;
        this.broadcasterService = broadcasterService;

        // set the class name and reset everything that was there before
        setClassName(CSS_CLASSNAME_GAME);
        this.setId(CSS_CLASSNAME_GAME);

        this.setAlignItems(Alignment.CENTER);

        this.labelGameName = new H3();
        this.gameField = new CodenamesGameField();

        this.labelCurrentTeam = new Label("Current team:");
        this.labelCurrentTeam.setId(CSS_ID_LABEL_CURRENT_TEAM);

        this.labelRemainingStart = new Label();
        this.labelRemainingStart.setClassName(CSS_CLASSNAME_LABEL_REMAINING);
        this.labelRemainingOther = new Label();
        this.labelRemainingOther.setClassName(CSS_CLASSNAME_LABEL_REMAINING);
        final Div labelsRemaining = new Div(this.labelRemainingStart, new Label(" - "), this.labelRemainingOther);

        this.buttonNextTeam = new Button("next team");
        this.buttonNextTeam.setId(CSS_ID_BUTTON_NEXT_TEAM);

        final HorizontalLayout gameInformation = new HorizontalLayout(labelsRemaining, this.labelCurrentTeam, this.buttonNextTeam);
        gameInformation.setAlignItems(Alignment.BASELINE);
        gameInformation.expand(labelsRemaining);
        gameInformation.setWidthFull();

        this.buttonSpyMaster = new Button("spymaster");
        this.buttonSpyMaster.addClickListener(clickEvent -> this.gameField.toggleSpyMaster());

        final VerticalLayout playerInformation = new VerticalLayout(new H4("Teams"));
        playerInformation.setId(CSS_ID_PLAYER_INFORMATION);
        playerInformation.setSizeUndefined();

        final Div gameArea = new Div(gameField, playerInformation);
        gameArea.setId(CSS_ID_GAME_AREA);
        this.add(labelGameName, gameInformation, gameArea, this.buttonSpyMaster);

        // set the total width and the game area height
        this.setWidth(GAME_FIELD_SIZE);
        this.gameField.setHeight(GAME_FIELD_SIZE);

        // add listeners to the elements
        addListeners();
    }

    /**
     * Add the listeners to the elements.
     */
    private void addListeners() {
        this.gameField.setGameUpdateEventListener(gameUpdateEvent -> doIfGameExists(gameUpdateEvent.getGameId(), game -> {
            if (gameUpdateEvent instanceof GameFieldUpdateEvent) {
                game.getWords().stream().filter(gameField -> gameField.getId().equals(((GameFieldUpdateEvent) gameUpdateEvent).getGameFieldId())).findFirst()
                        .ifPresent(gameField -> {
                            gameField.setFlipped(true);
                            // TODO codenames send push to all clients
                            this.gameService.updateGameField(game.getId(), gameField);

                            LOG.trace("{}: Flipped: {}.", game.getId(), gameField);
                        });
            }

            this.broadcasterService.broadcast(gameUpdateEvent);
        }));

        this.buttonNextTeam.addClickListener(clickEvent -> doIfGameExists(game -> {
            final PlayerTeam nextTeam = this.gameService.updateGameTurn(game.getId());
            this.broadcasterService.broadcast(new GameTurnEvent(game.getId(), nextTeam));
        }));
    }

    // https://vaadin.com/docs/v14/flow/advanced/tutorial-push-configuration.html
    // https://vaadin.com/docs/flow/advanced/tutorial-push-access.html
    // https://vaadin.com/docs/v14/flow/advanced/tutorial-push-broadcaster.html
    @Override
    public void setParameter(final BeforeEvent event, @WildcardParameter final String gameName) {
        this.currentGame = null;

        final Optional<Game> gameInDatabase = this.gameService.find(gameName);

        LOG.debug("Name: {}, game: {}.", gameName, gameInDatabase);

        if (gameInDatabase.isPresent()) {
            // TODO codenames: prepare the game area
            final Game game = gameInDatabase.get();
            this.currentGame = game;

            UI.getCurrent().access(() -> {
                this.labelGameName.setText(game.getName());
                this.buttonNextTeam.setEnabled(true);
                this.buttonNextTeam.setVisible(true);

                // give the field definition to the game field
                this.gameField.setFieldDefinition(game.getId(), game.getWords());

                updateGameInformation(game.getCurrentTeam());

                // set the CSS classes for the labels
                final boolean redIsStartTeam = PlayerTeam.RED == game.getStartTeam();
                this.labelRemainingStart.setClassName(CSS_CLASSNAME_TEAM_RED, redIsStartTeam);
                this.labelRemainingStart.setClassName(CSS_CLASSNAME_TEAM_BLUE, !redIsStartTeam);
                this.labelRemainingOther.setClassName(CSS_CLASSNAME_TEAM_BLUE, redIsStartTeam);
                this.labelRemainingOther.setClassName(CSS_CLASSNAME_TEAM_RED, !redIsStartTeam);

                updateRemainingLabelsTextAndSearchForWinner(game);
            });
        } else {
            Notification.show("Game ID is unknown.");
            UI.getCurrent().navigate("");
        }
    }

    @Override
    protected void onAttach(final AttachEvent attachEvent) {
        final UI ui = attachEvent.getUI();
        // register an update listener on the broadcaster
        this.broadcasterRegistration = this.broadcasterService.registerGameUpdateEventListener(gameUpdateEvent -> ui.access(() ->
        // only proceed if a game is known
        doIfGameExists(game -> {
            if (gameUpdateEvent instanceof GameFieldUpdateEvent) {
                final GameFieldUpdateEvent gameFieldUpdateEvent = (GameFieldUpdateEvent) gameUpdateEvent;
                this.gameField.setFieldStatus(gameFieldUpdateEvent);

                // update the labels and announce a winner, if possible
                final Optional<PlayerTeam> possibleWinner = updateRemainingLabelsTextAndSearchForWinner(game);
                if (possibleWinner.isPresent()) {
                    this.announceWinner(possibleWinner.get());
                } else
                // or check if killer field was activated, which also means a winner (=the next team)
                if (gameFieldUpdateEvent.isKillerField()) {
                    announceWinner(game.getCurrentTeam().nextTeam());
                } else
                // if flipped wrong color, set next player
                if (game.getCurrentTeam() != gameFieldUpdateEvent.getFieldTeam()) {
                    this.buttonNextTeam.click();
                }
            } else if (gameUpdateEvent instanceof GameTurnEvent) {
                final PlayerTeam newTeam = ((GameTurnEvent) gameUpdateEvent).getNextTeam();
                updateGameInformation(newTeam);
                game.setCurrentTeam(newTeam);
            }
        })));
    }

    /**
     * Update the game information.
     * 
     * @param nextTeam
     *            the new team
     */
    private void updateGameInformation(@NonNull final PlayerTeam nextTeam) {
        UI.getCurrent().access(() -> {
            this.buttonNextTeam.setText("End turn of " + nextTeam);
            this.labelCurrentTeam.setText(nextTeam + "'s turn!");

            // set the CSS class to the game area
            setOverallTeamCssClass(nextTeam);
        });
    }

    /**
     * Update the labels with the remaining words.
     * 
     * @return if a team has 0 remaining words, return the team name
     */
    @NonNull
    private Optional<PlayerTeam> updateRemainingLabelsTextAndSearchForWinner(@NonNull final Game game) {
        // count the remaining words by grouping the unflipped ones
        final Map<PlayerTeam, Long> remainingWords = game.getWords().stream().filter(gameField -> null != gameField.getTeam())
                .filter(gameField -> !gameField.isFlipped()).map(GameField::getTeam).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        final long remainingWordsOfBlue = remainingWords.getOrDefault(PlayerTeam.BLUE, 0L);
        final long remainingWordsOfRed = remainingWords.getOrDefault(PlayerTeam.RED, 0L);

        // check if any has 0 left; if so, set it as the return value
        final PlayerTeam eventualWinnerTeam;
        if (0 == remainingWordsOfBlue) {
            eventualWinnerTeam = PlayerTeam.BLUE;
        } else if (0 == remainingWordsOfRed) {
            eventualWinnerTeam = PlayerTeam.RED;
        } else {
            eventualWinnerTeam = null;
        }

        // also update the texts
        updateRemainingLabelText(remainingWordsOfBlue, remainingWordsOfRed, game.getStartTeam());

        return Optional.ofNullable(eventualWinnerTeam);
    }

    /** Announce the winner and end the game. **/
    private void announceWinner(@NonNull final PlayerTeam winnerTeam) {
        UI.getCurrent().access(() -> {
            LOG.debug("Winner team is {}!", winnerTeam);
            this.labelCurrentTeam.setText(winnerTeam + " has won!");
            this.addClassName(CSS_CLASSNAME_GAME_WON);

            this.buttonNextTeam.setEnabled(false);
            this.buttonNextTeam.setText(":)");

            setOverallTeamCssClass(winnerTeam);
        });
    }

    /**
     * Update the text of the remaining labels.
     */
    private void updateRemainingLabelText(final long remainingWordsOfBlue, final long remainingWordsOfRed, @NonNull final PlayerTeam startTeam) {
        UI.getCurrent().access(() -> {
            if (PlayerTeam.BLUE == startTeam) {
                this.labelRemainingStart.setText(Long.toString(remainingWordsOfBlue));
                this.labelRemainingOther.setText(Long.toString(remainingWordsOfRed));
            } else {
                this.labelRemainingStart.setText(Long.toString(remainingWordsOfRed));
                this.labelRemainingOther.setText(Long.toString(remainingWordsOfBlue));
            }
        });
    }

    /**
     * Sets the team's CSS class to the game area. <strong>Must be ensured that only being called inside of a UI access.</strong>
     */
    private void setOverallTeamCssClass(@NonNull final PlayerTeam nextTeam) {
        final String className = CSS_CLASSNAME_TEAM + nextTeam.name().toLowerCase();
        this.removeClassNames(CSS_CLASSNAME_TEAM_RED, CSS_CLASSNAME_TEAM_BLUE);
        this.addClassName(className);
    }

    @Override
    protected void onDetach(final DetachEvent detachEvent) {
        this.broadcasterRegistration.remove();
        this.broadcasterRegistration = null;
    }

    /**
     * If a current game with the given ID is known, pass it to the given consumer and run it.
     */
    private void doIfGameExists(final long gameId, @NonNull final Consumer<Game> consumerIfGameExists) {
        if (null != this.currentGame && this.currentGame.getId().longValue() == gameId) {
            consumerIfGameExists.accept(this.currentGame);
        }
    }

    /**
     * If a current game is known, pass it to the given consumer and run it.
     */
    private void doIfGameExists(@NonNull final Consumer<Game> consumerIfGameExists) {
        if (null != this.currentGame) {
            consumerIfGameExists.accept(this.currentGame);
        }
    }
}
