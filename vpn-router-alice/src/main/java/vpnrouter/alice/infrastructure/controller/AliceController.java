package vpnrouter.alice.infrastructure.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vpnrouter.alice.security.AliceAuthorizer;
import vpnrouter.alice.service.AliceService;

@Slf4j
@RestController
@RequestMapping("/alice/**")
@RequiredArgsConstructor
public class AliceController {

    private final AliceService aliceService;
    private final AliceAuthorizer aliceAuthorizer;

    @GetMapping
    public String check() {
        return "OK \uD83D\uDC67";
    }

    @PostMapping
    public AliceResponse process(@RequestParam String secret, @RequestBody JsonNode requestBody) {
        try {
            var request = new AliceRequest(requestBody);
            aliceAuthorizer.authorize(secret, request);
            return process(request);
        } catch (Exception e) {
            log.error("Failed to process Alice request: {}", e.getMessage(), e);
            return new AliceResponse("%s Что-то пошло не так: %s".formatted(
                    Audio.FAILURE,
                    e.getMessage()
            ));
        }
    }

    private AliceResponse process(AliceRequest request) {
        var command = request.getCommand();
        var client = request.getClient().orElseThrow(() -> new IllegalArgumentException("Нужен конкретный клиент"));
        log.info("Request: {}\nInput: {}\nCommand: {}\nClient: {}", request, request.getInputText(), command, client);
        switch (command) {
            case ON -> aliceService.switchTunnelingOnFor(client);
            case OFF -> aliceService.switchTunnelingOffFor(client);
        }
        return new AliceResponse("%s %s VPN для %s".formatted(
                Audio.SUCCESS,
                command == SwitchCommand.ON ? "Включил" : "Выключил",
                client
        ));
    }
}
