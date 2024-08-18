package vpnrouter.web;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.grid.editor.EditorSaveEvent;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import vpnrouter.api.client.ClientService;
import vpnrouter.api.client.ClientUpdate;
import vpnrouter.api.client.ClientView;

import java.util.List;

@UIScope
@Route("clients")
@Component
public class ClientsList extends AppLayout {
    private final ClientService clientService;
    private VerticalLayout layout;
    private Grid<ClientView> grid;

    public ClientsList(ClientService clientService) {
        this.clientService = clientService;
        layout = new VerticalLayout();
        grid = new Grid<>();
        layout.add(grid);
        addToNavbar(new H3("Clients"));
        setContent(layout);
    }

    @PostConstruct
    public void fillGrid() {
        List<ClientView> clients = clientService.getAll();
        if (!clients.isEmpty()) {
            Editor<ClientView> editor = grid.getEditor();
            Binder<ClientView> binder = new Binder<>(ClientView.class);

            grid.addColumn(ClientView::getIpAddress).setHeader("Ip address");
            Grid.Column<ClientView> nameColumn = grid.addColumn(ClientView::getName).setHeader("Name");

            Grid.Column<ClientView> tunnelledColumn = grid.addColumn(ClientView::isTunnelled).setHeader("Tunnelled");
            Checkbox tunneledCheckbox = new Checkbox();
            binder.bind(tunneledCheckbox, ClientView::isTunnelled, ClientView::setTunnelled);
            tunnelledColumn.setEditorComponent(tunneledCheckbox);

            Grid.Column<ClientView> editColumn = grid.addComponentColumn(
                    clientView -> {
                        Button editButton = new Button("Edit");
                        editButton.addClickListener(
                                event -> {
                                    if (editor.isOpen()) {
                                        editor.cancel();
                                    }
                                    grid.getEditor().editItem(clientView);
                                });
                        return editButton;
                    }
            );
            editor.setBinder(binder);
            editor.setBuffered(true);

            TextField nameField = new TextField();
            nameField.setWidthFull();
            binder.forField(nameField).bind(ClientView::getName, ClientView::setName);
            nameColumn.setEditorComponent(nameField);

            Button saveButton = new Button("Save", event -> editor.save());
            editor.addSaveListener(this::updateClient);
            Button cancelButton = new Button(VaadinIcon.CLOSE.create(), event -> editor.cancel());
            cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
            HorizontalLayout actions = new HorizontalLayout(saveButton, cancelButton);
            actions.setPadding(false);
            editColumn.setEditorComponent(actions);

            grid.addComponentColumn(
                    clientView -> {
                        Button deleteButton = new Button("Delete");
                        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
                        deleteButton.addClickListener(event -> getClientDeletionDialog(clientView));
                        return deleteButton;
                    }
            );

            grid.setItems(clients);
        }
    }

    private void updateClient(EditorSaveEvent<ClientView> event) {
        ClientView editedClientView = event.getItem();
        ClientUpdate clientUpdate = ClientUpdate.builder()
                .name(editedClientView.getName())
                .tunnelled(editedClientView.isTunnelled())
                .build();
        clientService.update(editedClientView.getIpAddress(), clientUpdate);
    }

    private void getClientDeletionDialog(ClientView clientView) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Delete client");
        dialog.setText("Do you really want to delete %s client?".formatted(clientView.getName()));
        dialog.setCancelable(true);
        dialog.addCancelListener(event -> dialog.close());
        dialog.setConfirmText("Delete");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(
                event -> {
                    clientService.remove(clientView.getIpAddress());
                    grid.setItems(clientService.getAll());
                }
        );
        dialog.open();
    }

}
