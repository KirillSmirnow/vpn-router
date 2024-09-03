package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vpnrouter.api.client.ClientService;
import vpnrouter.web.model.ClientWebView;

@Component
@RequiredArgsConstructor
public class ClientDeletion {

    private final ClientService clientService;

    public Button buildDeleteButton(ClientWebView client, Runnable onSuccessListener) {
        var deleteButton = new Button(new Icon(VaadinIcon.TRASH));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(event -> openConfirmDialog(client, onSuccessListener));
        return deleteButton;
    }

    private void openConfirmDialog(ClientWebView client, Runnable onSuccessListener) {
        var dialog = new ConfirmDialog();
        dialog.setHeader("Delete client");
        dialog.setText("Do you really want to delete %s?".formatted(
                client.getName() != null ? client.getName() : client.getIpAddress()
        ));
        dialog.setCancelable(true);
        dialog.setConfirmText("Delete");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(event -> {
            clientService.remove(client.getIpAddress());
            onSuccessListener.run();
        });
        dialog.open();
    }
}
