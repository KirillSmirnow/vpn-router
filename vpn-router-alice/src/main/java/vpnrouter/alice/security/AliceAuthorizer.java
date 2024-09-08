package vpnrouter.alice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vpnrouter.alice.infrastructure.controller.AliceRequest;

@Component
@RequiredArgsConstructor
public class AliceAuthorizer {

    private final AliceProperties aliceProperties;

    public void authorize(String secret, AliceRequest request) {
        if (!secret.equals(aliceProperties.getSecret())) {
            throw new ForbiddenException("Invalid secret: " + secret);
        }
        if (!request.getSkill().equals(aliceProperties.getSkill())) {
            throw new ForbiddenException("Invalid skill: " + request.getSkill());
        }
        if (!request.getUser().equals(aliceProperties.getUser())) {
            throw new ForbiddenException("Invalid user: " + request.getUser());
        }
        if (!aliceProperties.getApplications().contains(request.getApplication())) {
            throw new ForbiddenException("Invalid application: " + request.getApplication());
        }
    }
}
