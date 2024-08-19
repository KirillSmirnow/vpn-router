package vpnrouter.alice;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/alice")
@RequiredArgsConstructor
public class AliceController {

    private final AliceAuthorizer aliceAuthorizer;

    @GetMapping
    public String aliceCheck() {
        return "OK \uD83D\uDC67";
    }

    @PostMapping
    public AliceResponse alice(@RequestBody JsonNode rawRequest) {
        var request = new AliceRequest(rawRequest);
        log.info("***");
        log.info("Request: {}", request);
        log.info("Raw text: '{}'", request.getText("/request/command").orElseThrow());
        try {
            authorize(request);
            return process(request);
        } catch (Exception e) {
            log.error("Failed to process Alice request: {}", e.getMessage(), e);
            return new AliceResponse("<speaker audio=\"alice-sounds-things-toilet-1.opus\"> Что-то пошло не так");
        }
    }

    private void authorize(AliceRequest request) {
        aliceAuthorizer.authorize(
                request.getText("/session/skill_id").orElseThrow(),
                request.getText("/session/user/user_id").orElseThrow(),
                request.getText("/session/application/application_id").orElseThrow()
        );
    }

    private AliceResponse process(AliceRequest request) {
        var command = request.getText("/request/nlu/intents/generic/slots/command/value").map(Command::valueOf).orElseThrow();
        var client = request.getText("/request/nlu/intents/generic/slots/client/value").orElseThrow();
        log.info("Command: {}, client: '{}'", command, client);
        return new AliceResponse(
                "<speaker audio=\"alice-music-horn-1.opus\"> %s VPN для %s".formatted(
                        command == Command.ON ? "Включил" : "Выключил",
                        client.isBlank() ? "всех" : client
                )
        );
    }
}
