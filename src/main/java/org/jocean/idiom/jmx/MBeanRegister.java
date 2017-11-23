/**
 * 
 */
package org.jocean.idiom.jmx;


/**
 * @author isdom
 *
 */
public interface MBeanRegister {
    
    public  String getObjectNamePrefix();
    
    public  boolean registerMBean(final String suffix, final Object mbean);
    
    public  void unregisterMBean(final String suffix);
    
    public  boolean isRegistered(final String suffix);

    public  boolean replaceRegisteredMBean(final String suffix, final Object oldMBean, final Object newMBean);
    
    public  Object  getMBean(final String suffix);

    public  void unregisterAllMBeans();
    
    public  void destroy();
}
