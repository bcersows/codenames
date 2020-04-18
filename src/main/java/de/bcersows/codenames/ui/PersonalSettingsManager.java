package de.bcersows.codenames.ui;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationListener;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;

/**
 * Manager for handling personal settings like dark mode.
 * 
 * @author bcersows
 */
@Component
@VaadinSessionScope
public class PersonalSettingsManager implements AfterNavigationListener, ComponentEventListener<AttachEvent> {
    private static final long serialVersionUID = -292618517357264018L;
    private static final Logger LOG = LoggerFactory.getLogger(PersonalSettingsManager.class);

    // TODO codenames: store in browser property and reload
    /** If dark mode is activated. **/
    private boolean darkMode;

    @PostConstruct
    protected void setUp() {
        UI.getCurrent().addAfterNavigationListener(this);
        UI.getCurrent().addAttachListener(this);
    }

    /**
     * Toggle the dark mode.
     */
    public void toggleDarkMode() {
        this.darkMode = !this.darkMode;

        setDarkModeToUi();
    }

    /** Set the dark mode to the UI. **/
    private void setDarkModeToUi() {
        final Element uiElement = UI.getCurrent().getElement();
        final String newMode = this.darkMode ? "dark" : "light";
        uiElement.setAttribute("theme", newMode);

        LOG.trace("Set mode to {}", newMode);
    }

    @Override
    public void afterNavigation(final AfterNavigationEvent event) {
        // after every page change, set the dark mode state
        setDarkModeToUi();
    }

    @Override
    public void onComponentEvent(final AttachEvent event) {
        // on page reload etc., restore dark mode
        setDarkModeToUi();
    }

}
