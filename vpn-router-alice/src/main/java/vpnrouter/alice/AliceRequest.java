package vpnrouter.alice;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

@RequiredArgsConstructor
@ToString
public class AliceRequest {

    private final JsonNode root;

    public Optional<String> getText(String pointer) {
        try {
            return Optional.of(root.at(pointer).asText());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
