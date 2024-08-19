package vpnrouter.alice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AliceResponse {

    private final String version = "1.0";
    private final Response response;

    public AliceResponse(String response) {
        this.response = new Response(response);
    }

    @Data
    public static class Response {

        @JsonProperty("end_session")
        private final boolean endSession = true;

        private final String text;
    }
}
