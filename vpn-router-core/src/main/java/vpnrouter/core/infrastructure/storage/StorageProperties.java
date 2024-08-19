package vpnrouter.core.infrastructure.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@Data
@ConfigurationProperties("storage")
public class StorageProperties {
    private final Path clientsFilePath;
}
