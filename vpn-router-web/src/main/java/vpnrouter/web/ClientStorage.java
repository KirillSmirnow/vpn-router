package vpnrouter.web;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ClientStorage {
    private final List<ClientWebView> values = new ArrayList<>(1);

    public ClientWebView getValue() {
        return values.getFirst();
    }

    public void setValue(ClientWebView value) {
        values.add(value);
    }

    public void removeAll() {
        values.clear();
    }
}
