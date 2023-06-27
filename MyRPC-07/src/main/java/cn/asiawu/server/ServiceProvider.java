package cn.asiawu.server;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author asiawu
 * @date 2023/06/25 01:46
 * @description: 用来提供Service实例
 */
public class ServiceProvider {
    private Map<String,Object> map;

    public ServiceProvider() {
        map=new ConcurrentHashMap<>();
    }

    public void addService(Object service) {
        Class<?>[] interfaces = service.getClass().getInterfaces();
        for (Class<?> anInterface : interfaces) {
            map.put(anInterface.getName(),service);
        }
    }
    public Object getService(String interfaceName) {
        return map.get(interfaceName);
    }

    public Set<String> getAllServiceName() {
        return map.keySet();
    }
}
