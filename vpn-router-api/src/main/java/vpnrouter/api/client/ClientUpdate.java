package vpnrouter.api.client;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientUpdate {

    @Size(min = ClientConstraints.Name.MIN, max = ClientConstraints.Name.MAX)
    private final String name;

    private final boolean tunnelled;
}
