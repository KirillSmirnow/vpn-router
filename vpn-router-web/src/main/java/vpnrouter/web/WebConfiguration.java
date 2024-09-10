package vpnrouter.web;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@ConfigurationPropertiesScan
@Push
@EnableAsync
public class WebConfiguration extends SpringBootServletInitializer implements AppShellConfigurator {
}
