package vpnrouter.web.ui.clients.newclient;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class ButtonsLayoutBuilder {
    private final Runnable addCallback;
    private final Runnable cancelCallback;

    public ButtonsLayoutBuilder(Runnable addCallback, Runnable cancelCallback) {
        this.addCallback = addCallback;
        this.cancelCallback = cancelCallback;
    }

    public HorizontalLayout build() {
        var buttonLayout = new HorizontalLayout(buildAddButton(), buildCancelButton());
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        buttonLayout.setWidthFull();
        return buttonLayout;
    }

    private Button buildAddButton() {
        var addButton = new Button("Add");
        addButton.addClickListener(event -> addCallback.run());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return addButton;
    }

    public Button buildCancelButton() {
        var cancelButton = new Button("Cancel", event -> cancelCallback.run());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        return cancelButton;
    }
}
