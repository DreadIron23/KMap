package sk.uniza.fri.janmokry.karnaughmap.util;

import android.content.Context;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Service Locator.
 *
 * Class for handling service instances.
 */
public class SL {
    private static final Map<String, Object> sServicesInstances = new HashMap<>();
    private static final Map<String, Class> sServicesImplementationsMapping = new HashMap<>();
    private static Context sContext;

    public interface IService {
    }

    public static void init(Context context) {
        sContext = context.getApplicationContext();
    }

    /**
     * Return instance of desired class or object that implement desired interface.
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> clazz) {

        @SuppressWarnings("ResourceType")
        T instance = (T)getService(clazz.getName(), sContext);
        return instance;
    }

    /**
     * This method allow bind custom service implementation to some interface.
     *
     * @param interfaceClass      interface
     * @param implementationClass class which implement interface specified in first param
     */
    public static void bindCustomServiceImplementation(Class interfaceClass, Class implementationClass) {
        sServicesImplementationsMapping.put(interfaceClass.getName(), implementationClass);
    }

    private static Object getService(String name, Context applicationContext) {
        // if name contains . it should be class path and we try to instantiate it.
        if (sServicesInstances.containsKey(name)) {
            return sServicesInstances.get(name);
        } else {
            try {
                final Class<?> clazz;
                if (sServicesImplementationsMapping.containsKey(name)) {
                    clazz = sServicesImplementationsMapping.get(name);
                } else {
                    clazz = Class.forName(name);
                }
                Object serviceInstance;
                try {
                    final Constructor e1 = clazz.getConstructor(Context.class);
                    serviceInstance = e1.newInstance(applicationContext);
                } catch (NoSuchMethodException var6) {
                    final Constructor constructor = clazz.getConstructor();
                    serviceInstance = constructor.newInstance();
                }
                if (!(serviceInstance instanceof IService)) {
                    throw new IllegalArgumentException("Requested service must implement IService interface");
                }
                sServicesInstances.put(name, serviceInstance);
                return serviceInstance;
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Requested service class was not found: " + name, e);
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot initialize requested service: " + name, e);
            }
        }
    }
}
