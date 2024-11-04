package vpnrouter.api.client;

import jakarta.annotation.Nullable;
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
    private final String ipAddress;

    @Nullable
    @Size(min = ClientConstraints.Name.MIN, max = ClientConstraints.Name.MAX)
    private final String name;

    private final boolean tunnelled;
}
