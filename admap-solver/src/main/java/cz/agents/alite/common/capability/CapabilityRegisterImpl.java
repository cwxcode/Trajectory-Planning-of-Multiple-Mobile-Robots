package cz.agents.alite.common.capability;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Provides basic implementation of @CapabilityRegister using @HashMap
 * 
 * @author Jiri Vokrinek
 */
public class CapabilityRegisterImpl implements CapabilityRegister {

    private final HashMap<String, Set<String>> register = new HashMap<String, Set<String>>();

    public void register(String identity, String capabilityName) {
        Set<String> recS = register.get(capabilityName);
        if (recS == null) {
            LinkedHashSet<String> page = new LinkedHashSet<String>();
            page.add(identity);
            register.put(capabilityName, page);
        } else {
            recS.add(identity);
        }
    }

    public Set<String> getIdentities(String capabilityName) {
        Set<String> get = register.get(capabilityName);
        if (get == null) {
            return new LinkedHashSet<String>();
        }
        return new LinkedHashSet<String>(get);

    }

    public Set<String> getIdentities() {
        LinkedHashSet<String> result = new LinkedHashSet<String>();
        for (Set<String> page : register.values()) {
            result.addAll(page);
        }
        return result;
    }
}
