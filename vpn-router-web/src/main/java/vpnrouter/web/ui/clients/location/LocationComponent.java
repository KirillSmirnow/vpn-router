package vpnrouter.web.ui.clients.location;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Getter;

@CssImport("./styles/styles.css")
@Getter
public class LocationComponent {
    private final TextField location;

    public LocationComponent(String ipAddress, String location) {
        this.location = buildTextField();
        setState(ipAddress, location);
    }

    public void setState(String ipAddress, String location) {
        var text = "Your location: %s (%s)".formatted(location, ipAddress);
        this.location.setValue(text);
//        this.location.setWidth(text.length() + "ch");
    }

    private TextField buildTextField() {
        var textField = new TextField();
        textField.setReadOnly(true);
        textField.addClassName("custom-text-field");
        textField.setMaxWidth("100%");
        return textField;
    }
}
