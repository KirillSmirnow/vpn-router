package vpnrouter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class VpnRouterTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void whenStartApplication_thenApplicationContextCreated() {
        assertThat(applicationContext).isNotNull();
    }
}
