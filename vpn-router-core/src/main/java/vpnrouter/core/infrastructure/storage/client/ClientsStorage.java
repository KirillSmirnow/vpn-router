package vpnrouter.core.infrastructure.storage.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import vpnrouter.core.infrastructure.storage.StorageProperties;
import vpnrouter.core.service.client.Client;
import vpnrouter.core.service.client.ClientRepository;

import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

import static java.util.Comparator.comparing;

@Component
@RequiredArgsConstructor
public class ClientsStorage implements ClientRepository {

    private final FileLock fileLock = new FileLock();
    private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory())
            .findAndRegisterModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private final StorageProperties storageProperties;

    @Override
    public Optional<Client> find(String ipAddress) {
        return findAll().stream()
                .filter(client -> client.getIpAddress().equals(ipAddress))
                .findFirst();
    }

    @Override
    public List<Client> findAll() {
        return fileLock.withReadLock(() -> {
            return readFile().getClients();
        });
    }

    @Override
    public void save(Client client) {
        fileLock.withWriteLock(() -> {
            var clients = readFile();
            clients.getClients().remove(client);
            clients.getClients().add(client);
            writeFile(clients);
        });
    }

    @Override
    public void deleteIfExists(String ipAddress) {
        fileLock.withWriteLock(() -> {
            var clients = readFile();
            clients.getClients().removeIf(client -> client.getIpAddress().equals(ipAddress));
            writeFile(clients);
        });
    }

    @SneakyThrows
    private Clients readFile() {
        var path = storageProperties.getClientsFilePath();
        if (Files.exists(path)) {
            return objectMapper.readValue(path.toFile(), Clients.class);
        } else {
            return new Clients();
        }
    }

    @SneakyThrows
    private void writeFile(Clients clients) {
        var path = storageProperties.getClientsFilePath();
        if (Files.notExists(path)) {
            Files.createDirectories(path.getParent());
        }
        clients.getClients().sort(comparing(Client::getLastSwitchedAt).reversed());
        objectMapper.writeValue(path.toFile(), clients);
    }

    private static class FileLock {

        private final ReadWriteLock lock = new ReentrantReadWriteLock(true);

        public <T> T withReadLock(Supplier<T> supplier) {
            lock.readLock().lock();
            try {
                return supplier.get();
            } finally {
                lock.readLock().unlock();
            }
        }

        public void withWriteLock(Runnable runnable) {
            lock.writeLock().lock();
            try {
                runnable.run();
            } finally {
                lock.writeLock().unlock();
            }
        }
    }
}
