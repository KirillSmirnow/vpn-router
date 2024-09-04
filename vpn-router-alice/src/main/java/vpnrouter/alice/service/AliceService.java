package vpnrouter.alice.service;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Validated
public interface AliceService {

    void switchTunnelingOnFor(@NotBlank String query);

    void switchTunnelingOffFor(@NotBlank String query);
}
