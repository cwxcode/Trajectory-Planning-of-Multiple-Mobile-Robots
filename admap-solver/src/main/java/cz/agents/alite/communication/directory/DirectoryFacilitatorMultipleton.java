package cz.agents.alite.communication.directory;

import cz.agents.alite.common.capability.CapabilityRegister;
import cz.agents.alite.common.capability.CapabilityRegisterImpl;
import java.util.HashMap;
import java.util.Map;

/**
 * Multiple directories singleton implemented using @HashMap of @CapabilityRegisterImpl 
 * and unique instance identification as keys.
 * 
 * @author Jiri Vokrinek
 */
public class DirectoryFacilitatorMultipleton extends CapabilityRegisterImpl {


    private static final Map<String, CapabilityRegisterImpl> instances = new HashMap<String, CapabilityRegisterImpl>();

    static public CapabilityRegister getInstance(String id) {
        if (!instances.containsKey(id)) {
            instances.put(id, new CapabilityRegisterImpl());
        }
        return instances.get(id);
    }
}
