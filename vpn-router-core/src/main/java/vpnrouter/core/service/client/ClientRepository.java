package vpnrouter.core.service.client;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Validated
public interface ClientRepository {

    Optional<Client> find(String ipAddress);

    List<Client> findAll();

    void save(@Valid Client client);

    void deleteIfExists(String ipAddress);
}
