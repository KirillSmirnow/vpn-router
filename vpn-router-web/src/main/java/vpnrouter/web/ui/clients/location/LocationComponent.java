package vpnrouter.web.ui.clients.location;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Getter;

@CssImport("./styles/styles.css")
public class LocationComponent {
    private final TextField ipAddressTextFiled;
    private final TextField locationTextFiled;
    @Getter
    private final VerticalLayout layout;

    public LocationComponent(String ipAddress, String location) {
        this.ipAddressTextFiled = buildTextField();
        this.locationTextFiled = buildTextField();
        setState(ipAddress, location);
        this.layout = new VerticalLayout(
                new NativeLabel("Your IP:"),
                ipAddressTextFiled,
                new NativeLabel("Your location:"),
                locationTextFiled
        );
    }

    public void setState(String ipAddress, String location) {
        ipAddressTextFiled.setValue(ipAddress);
        locationTextFiled.setValue(location);
    }

    private TextField buildTextField() {
        var textField = new TextField();
        textField.setReadOnly(true);
        return textField;
    }

}
