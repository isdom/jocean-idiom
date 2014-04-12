package org.jocean.idiom;

public interface InterfaceSource {
    public <INTF> INTF queryInterfaceInstance(final Class<INTF> intfCls);
}
