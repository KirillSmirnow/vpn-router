package vpnrouter.api.client;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientView {
    private String ipAddress;
    private String name;
    private boolean tunnelled;

    @Override
    public int hashCode() {
        if (getIpAddress() != null) {
            return getIpAddress().hashCode();
        }
        return super.hashCode();
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ClientView other = (ClientView) obj;
        if (getIpAddress() == null || other.getIpAddress() == null) {
            return false;
        }
        return getIpAddress().equals(other.getIpAddress());
    }
}
