package vpnrouter.core.service.client.detection;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vpnrouter.api.client.ClientDetectionService;

@Slf4j
@Service
public class ClientDetectionServiceImpl implements ClientDetectionService {

    @Override
    public void detectAndSave() {
        log.info("Detecting and saving clients");
    }
}
