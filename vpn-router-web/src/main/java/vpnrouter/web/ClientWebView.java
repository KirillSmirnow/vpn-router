package vpnrouter.web;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = "ipAddress")
public class ClientWebView {
    private final String ipAddress;
    private String name;
    private boolean tunnelled;
}
