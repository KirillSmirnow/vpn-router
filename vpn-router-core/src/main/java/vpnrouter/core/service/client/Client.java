package vpnrouter.core.service.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import vpnrouter.api.client.ClientConstraints;
import vpnrouter.api.validator.IpAddress;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
@EqualsAndHashCode(of = "ipAddress")
public class Client {

    @NotBlank
    @IpAddress
    private final String ipAddress;

    @Size(min = ClientConstraints.Name.MIN, max = ClientConstraints.Name.MAX)
    private String name;

    private boolean tunnelled;

    @NotNull
    private LocalDateTime lastSwitchedAt;
}
