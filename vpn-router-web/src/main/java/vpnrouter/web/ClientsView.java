package vpnrouter.web;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

@UIScope
@Component
@PageTitle("Clients")
@Route("")
public class ClientsView extends VerticalLayout {

    private final Button addClientButton;

    public ClientsView() {
        addClientButton = new Button(VaadinIcon.PLUS_CIRCLE.create(), $ -> UI.getCurrent().navigate(AddClientView.class));
        add(addClientButton);
    }
}
