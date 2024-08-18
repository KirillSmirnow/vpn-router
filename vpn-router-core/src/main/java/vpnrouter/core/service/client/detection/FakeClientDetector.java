package vpnrouter.core.service.client.detection;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import vpnrouter.core.exception.UserException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Collections.emptyList;

@Component
public class FakeClientDetector implements ClientDetector {

    @Override
    @SneakyThrows
    public List<String> detectIpAddresses() {
        Thread.sleep(Duration.ofSeconds(10));
        var result = ThreadLocalRandom.current().nextInt(0, 4 + 1);
        if (result == 0) {
            throw new UserException("Something went wrong");
        } else if (result <= 2) {
            return emptyList();
        } else {
            return generateIpAddresses();
        }
    }

    private List<String> generateIpAddresses() {
        var ipAddresses = new ArrayList<String>();
        var count = ThreadLocalRandom.current().nextInt(1, 5 + 1);
        for (var i = 0; i < count; i++) {
            var lastGroup = ThreadLocalRandom.current().nextInt(1, 30 + 1);
            ipAddresses.add("192.168.0.%d".formatted(lastGroup));
        }
        return ipAddresses;
    }
}
