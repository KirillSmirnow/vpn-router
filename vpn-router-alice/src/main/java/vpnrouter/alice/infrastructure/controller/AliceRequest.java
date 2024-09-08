package vpnrouter.alice.infrastructure.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

import static java.util.function.Predicate.not;

@RequiredArgsConstructor
@ToString
public class AliceRequest {

    private final JsonNode root;

    public String getInputText() {
        return getText(Pointers.INPUT_TEXT).orElseThrow();
    }

    public SwitchCommand getCommand() {
        return getText(Pointers.COMMAND).map(SwitchCommand::valueOf).orElseThrow();
    }

    public Optional<String> getClient() {
        return getText(Pointers.CLIENT).filter(not(String::isBlank));
    }

    public String getSkill() {
        return getText(Pointers.SKILL).orElseThrow();
    }

    public String getUser() {
        return getText(Pointers.USER).orElseThrow();
    }

    public String getApplication() {
        return getText(Pointers.APPLICATION).orElseThrow();
    }

    private Optional<String> getText(String pointer) {
        try {
            return Optional.of(root.at(pointer).asText());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static class Pointers {
        private static final String INPUT_TEXT = "/request/command";
        private static final String COMMAND = "/request/nlu/intents/generic/slots/command/value";
        private static final String CLIENT = "/request/nlu/intents/generic/slots/client/value";
        private static final String SKILL = "/session/skill_id";
        private static final String USER = "/session/user/user_id";
        private static final String APPLICATION = "/session/application/application_id";
    }
}
