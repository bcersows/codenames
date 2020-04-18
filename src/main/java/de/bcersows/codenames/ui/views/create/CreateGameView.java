package de.bcersows.codenames.ui.views.create;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.WrapMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;

import de.bcersows.codenames.backend.service.GameService;
import de.bcersows.codenames.exception.InvalidWordsException;
import de.bcersows.codenames.ui.CodenamesMainLayout;
import de.bcersows.codenames.ui.CodenamesUpdateView;
import de.bcersows.codenames.ui.views.game.GameView;

@Component
@Scope("prototype")
@Route(value = "", layout = CodenamesMainLayout.class)
@PageTitle("Create/join game | Codenames")
@PreserveOnRefresh
public class CreateGameView extends VerticalLayout implements CodenamesUpdateView {
    private static final long serialVersionUID = 4691868224906265522L;

    private static final Logger LOG = LoggerFactory.getLogger(CreateGameView.class);

    /** Service to access games. **/
    @NonNull
    private final GameService gameService;

    // TODO codenames: add tabs: https://vaadin.com/components/vaadin-tabs/java-examples

    @Autowired
    public CreateGameView(@NonNull final GameService gameService) {
        this.gameService = gameService;

        addClassName("game-creation");
        setSizeFull();

        // build the area for creating a game
        final VerticalLayout createGameArea = buildCreateGameArea();
        createGameArea.addClassName("create-game-area");
        createGameArea.setSizeUndefined();

        // build area with other information (open games, ...)
        final VerticalLayout otherInformationArea = buildOtherInformation();
        otherInformationArea.setSizeUndefined();

        // add everything to the UI
        final FlexLayout wrapLayout = new FlexLayout(createGameArea, otherInformationArea);
        wrapLayout.setJustifyContentMode(JustifyContentMode.EVENLY);
        wrapLayout.setFlexGrow(1, createGameArea, otherInformationArea);
        wrapLayout.setWrapMode(WrapMode.WRAP);
        wrapLayout.setSizeFull();
        this.add(wrapLayout);
    }

    /**
     * Build the area to create a game.
     */
    private VerticalLayout buildCreateGameArea() {
        final Label errorLabel = new Label();
        errorLabel.setVisible(false);
        errorLabel.addClassName("error");

        final Button buttonCreateGame = new Button("create", VaadinIcon.CHECK.create());
        final TextField textFieldGameName = new TextField("Game Name", "Enter the name of the new game! 5 to 20 characters.");
        final RadioButtonGroup<String> radioButtonGroupLanguageSelection = new RadioButtonGroup<>();
        radioButtonGroupLanguageSelection.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        radioButtonGroupLanguageSelection.setItems("en", "de", "fr");
        radioButtonGroupLanguageSelection.setValue("en");
        radioButtonGroupLanguageSelection.setRenderer(new TextRenderer<>(item -> item.toUpperCase(Locale.getDefault())));

        // configure the text field
        textFieldGameName.setPattern("\\w+");
        textFieldGameName.setMaxLength(20);
        textFieldGameName.setPreventInvalidInput(true);

        textFieldGameName.setClearButtonVisible(true);
        textFieldGameName.setValueChangeMode(ValueChangeMode.LAZY);
        textFieldGameName
                .addValueChangeListener(event -> buttonCreateGame.setEnabled(!textFieldGameName.isInvalid() && textFieldGameName.getValue().length() > 4));

        // configure the button (disabling at first, set a click listener, etc)
        buttonCreateGame.setEnabled(false);
        buttonCreateGame.setDisableOnClick(true);
        buttonCreateGame.addClickListener(clickEvent -> {
            errorLabel.setVisible(false);

            final String gameName = textFieldGameName.getValue();
            if (!textFieldGameName.isInvalid() && GameService.isGameNameValid(gameName)) {
                final String language = "en";
                try {
                    final boolean result = this.gameService.create(gameName, language);

                    if (result) {
                        // redirect to game
                        UI.getCurrent().navigate(GameView.class, gameName);
                    } else {
                        // show error message
                        errorLabel.setText("Already got a game with this name!");
                        errorLabel.setVisible(true);
                    }
                } catch (final InvalidWordsException e) {
                    LOG.error("Could not create a game.", e);
                    // show error message
                    errorLabel.setText("Could not create the game! Please try again!");
                    errorLabel.setVisible(true);
                }
            } else {
                errorLabel.setText("Invalid name for a game!");
                errorLabel.setVisible(true);
            }
        });
        final FormLayout formLayout = new FormLayout(textFieldGameName, new Div(new Label("Language:"), radioButtonGroupLanguageSelection), buttonCreateGame);

        // wrap it vertically
        return new VerticalLayout(new H2("CREATE A NEW GAME"), formLayout, errorLabel);
    }

    /**
     * Build the area to display other information, e.g. the
     */
    private VerticalLayout buildOtherInformation() {
        final Label labelOnlinePlayers = new Label(this.gameService.countPlayers() + " players are playing right now.");
        final Div onlinePlayers = new Div(new H3("online players"), labelOnlinePlayers);

        final Div sources = new Div(new H3("Sources"), new Label("Inspired by the amazing https://www.horsepaste.com/!"),
                new Label("The word lists are coming from: "), new Anchor("http://www.desiquintans.com/nounlist", "English"));

        final VerticalLayout layout = new VerticalLayout(new H2("Other information"), onlinePlayers, sources);
        return layout;
    }
}
