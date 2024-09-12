package vpnrouter.core.service.event.annotation;

import vpnrouter.api.event.Event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventOnSuccess {

    Class<? extends Event> value();
}
