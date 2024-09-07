package vpnrouter.core.service.location;

import com.maxmind.geoip2.DatabaseReader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vpnrouter.api.location.LocationService;

import java.net.InetAddress;
import java.util.Optional;

@Slf4j
@Service
public class LocationServiceImpl implements LocationService {

    private final DatabaseReader locationDatabaseReader = buildLocationDatabaseReader();

    @SneakyThrows
    private DatabaseReader buildLocationDatabaseReader() {
        return new DatabaseReader.Builder(getClass().getResourceAsStream("/GeoLite2-City.mmdb")).build();
    }

    @Override
    public Optional<String> getLocation(String ipAddress) {
        try {
            var address = InetAddress.getByName(ipAddress);
            return locationDatabaseReader.tryCity(address)
                    .map(location -> "%s, %s".formatted(location.getCity().getName(), location.getCountry().getName()));
        } catch (Exception e) {
            log.warn("Failed to get location: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
}
