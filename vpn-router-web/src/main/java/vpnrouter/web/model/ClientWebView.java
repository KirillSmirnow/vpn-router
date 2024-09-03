package vpnrouter.web.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import vpnrouter.api.client.ClientView;

import java.util.List;

@Data
@Builder
@EqualsAndHashCode(of = "ipAddress")
public class ClientWebView {

    private final String ipAddress;
    private String name;
    private boolean tunnelled;

    public static ClientWebView from(ClientView client) {
        return ClientWebView.builder()
                .ipAddress(client.getIpAddress())
                .name(client.getName())
                .tunnelled(client.isTunnelled())
                .build();
    }

    public static List<ClientWebView> from(List<ClientView> clients) {
        return clients.stream()
                .map(ClientWebView::from)
                .toList();
    }
}
