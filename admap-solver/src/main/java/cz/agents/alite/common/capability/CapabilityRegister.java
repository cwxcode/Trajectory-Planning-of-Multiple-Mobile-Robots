package cz.agents.alite.common.capability;

import java.util.Set;

/**
 *
 * @author Jiri Vokrinek
 */
public interface CapabilityRegister {

    /**
     *
     * Registers a capability to the identity
     *
     * @param identity
     * @param capabilityName
     */
    void register(String identity, String capabilityName);

    /**
     * 
     * @param capabilityName
     * @return Set of all identity records of the specified capabilityName registered
     */
    Set<String> getIdentities(String capabilityName);

    /**
     *  Provides copy of the identities directory.
     *
     * @return Set of all identity records registered
     */
    Set<String> getIdentities();
}
