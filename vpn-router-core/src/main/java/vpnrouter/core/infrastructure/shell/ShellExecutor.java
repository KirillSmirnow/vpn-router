package vpnrouter.core.infrastructure.shell;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vpnrouter.core.exception.UserException;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ShellExecutor {

    @SneakyThrows
    public List<String> execute(String command) {
        var process = new ProcessBuilder("sh", "-c", command).start();
        var completed = process.waitFor(1, TimeUnit.MINUTES);
        if (!completed) {
            log.warn("[{}] -> [TIME OUT]", command);
            throw new UserException("Shell command execution timed out");
        }
        var success = process.exitValue() == 0;
        try (var reader = process.inputReader()) {
            var output = reader.lines().toList();
            if (success) {
                log.info("[{}] -> [SUCCESS]\n{}", command, String.join("\n", output));
            } else {
                log.warn("[{}] -> [FAILURE]\n{}", command, String.join("\n", output));
            }
            return output;
        }
    }
}
