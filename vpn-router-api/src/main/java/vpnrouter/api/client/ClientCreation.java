package vpnrouter.api.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import vpnrouter.api.validator.IpAddress;

@Data
@Builder
public class ClientCreation {

    @NotBlank
    @IpAddress
    private String ipAddress;

    @Size(min = ClientConstraints.Name.MIN, max = ClientConstraints.Name.MAX)
    private String name;

    private boolean tunnelled;
}
