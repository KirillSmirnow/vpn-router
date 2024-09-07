package vpnrouter.core.service.location;

import com.maxmind.geoip2.DatabaseReader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import vpnrouter.api.location.LocationInfo;
import vpnrouter.api.location.LocationService;

import java.net.InetAddress;
import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
public class LocationServiceImpl implements LocationService {

    private final RestTemplate ipAddressClient = new RestTemplateBuilder()
            .rootUri("http://ip.cucurum.ru")
            .setConnectTimeout(Duration.ofSeconds(1))
            .setReadTimeout(Duration.ofSeconds(1))
            .build();

    private final DatabaseReader locationDatabaseReader = buildLocationDatabaseReader();

    @SneakyThrows
    private DatabaseReader buildLocationDatabaseReader() {
        return new DatabaseReader.Builder(getClass().getResourceAsStream("/GeoLite2-City.mmdb")).build();
    }

    @Override
    public LocationInfo getLocationInfo() {
        var ipAddress = getIpAddress();
        return LocationInfo.builder()
                .ipAddress(ipAddress.orElse("N/A"))
                .location(ipAddress.flatMap(this::getLocation).orElse("N/A"))
                .build();
    }

    private Optional<String> getIpAddress() {
        try {
            return Optional.ofNullable(ipAddressClient.getForObject("/", String.class));
        } catch (Exception e) {
            log.warn("Failed to get IP address: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @SneakyThrows
    private Optional<String> getLocation(String ipAddress) {
        var address = InetAddress.getByName(ipAddress);
        return locationDatabaseReader.tryCity(address)
                .map(location -> "%s, %s".formatted(location.getCity().getName(), location.getCountry().getName()));
    }
}
