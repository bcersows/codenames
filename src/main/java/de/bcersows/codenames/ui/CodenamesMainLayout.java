package de.bcersows.codenames.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@PWA(name = "Codenames", shortName = "Codenames", offlineResources = { "./styles/offline.css", "./images/offline.png" }, enableInstallPrompt = false)
@CssImport("./styles/shared-styles.css")
@Push
@Theme(value = Lumo.class, variant = Lumo.LIGHT)
public class CodenamesMainLayout extends AppLayout {
    private static final Logger LOG = LoggerFactory.getLogger(CodenamesMainLayout.class);

    /** Manager for the personal settings. **/
    private final PersonalSettingsManager personalSettingsManager;

    @Autowired
    public CodenamesMainLayout(final PersonalSettingsManager personalSettingsManager) {
        this.personalSettingsManager = personalSettingsManager;

        createHeader();
    }

    /** Create the header. **/
    private void createHeader() {
        final H1 logo = new H1("Codenames");
        logo.addClassName("logo");
        logo.addClickListener(clickEvent -> UI.getCurrent().navigate(""));

        final Button themeToggleButton = new Button(VaadinIcon.ADJUST.create());
        themeToggleButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        themeToggleButton.addClickListener(clickEvent -> this.personalSettingsManager.toggleDarkMode());

        final HorizontalLayout header = new HorizontalLayout(logo, themeToggleButton);
        header.addClassName("header");
        header.setWidth("100%");
        header.expand(logo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        addToNavbar(header);
    }

    @Override
    protected void onAttach(final AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        // there's no other way to detect initial page loads as the PSM is session-scoped
        this.personalSettingsManager.onComponentEvent(attachEvent);
    }

}
