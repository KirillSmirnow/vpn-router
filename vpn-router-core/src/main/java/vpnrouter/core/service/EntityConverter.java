package vpnrouter.core.service;

public interface EntityConverter<E, V> {

    V toView(E entity);
}
