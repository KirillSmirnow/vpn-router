package vpnrouter.api.client;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface ClientService {

    List<ClientView> getAll();

    void add(@Valid ClientCreation creation);

    void update(String ipAddress, @Valid ClientUpdate update);

    void remove(String ipAddress);
}
