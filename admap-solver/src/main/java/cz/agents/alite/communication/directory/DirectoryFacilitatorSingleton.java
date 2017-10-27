package cz.agents.alite.communication.directory;

import cz.agents.alite.common.capability.CapabilityRegister;
import cz.agents.alite.common.capability.CapabilityRegisterImpl;

/**
 * Singleton for encapsulation of @CapabilityRegisterImpl
 * 
 * @author Jiri Vokrinek
 */
public class DirectoryFacilitatorSingleton extends CapabilityRegisterImpl {

    private static final DirectoryFacilitatorSingleton instance = new DirectoryFacilitatorSingleton();

    static public CapabilityRegister getInstance() {
        return instance;
    }
}
