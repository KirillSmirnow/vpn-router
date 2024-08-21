package vpnrouter.alice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AliceAuthorizer {

    private final AliceProperties aliceProperties;

    public void authorize(String secret, String skillId, String userId, String applicationId) {
        if (!secret.equals(aliceProperties.getSecret())) {
            throw new ForbiddenException("Invalid secret: " + secret);
        }
        if (!skillId.equals(aliceProperties.getSkillId())) {
            throw new ForbiddenException("Invalid skill: " + skillId);
        }
        if (!userId.equals(aliceProperties.getUserId())) {
            throw new ForbiddenException("Invalid user: " + userId);
        }
        if (!aliceProperties.getApplicationIds().contains(applicationId)) {
            throw new ForbiddenException("Invalid application: " + applicationId);
        }
    }

    public static class ForbiddenException extends RuntimeException {

        public ForbiddenException(String message) {
            super(message);
        }
    }
}
