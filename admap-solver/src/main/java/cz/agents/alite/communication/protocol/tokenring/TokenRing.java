package cz.agents.alite.communication.protocol.tokenring;

import cz.agents.alite.communication.Communicator;
import cz.agents.alite.communication.protocol.DefaultProtocol;
import cz.agents.alite.communication.protocol.request.RequestInformInitiator;
import cz.agents.alite.communication.protocol.request.RequestInformResponder;
import cz.agents.alite.common.capability.CapabilityRegister;
import java.util.LinkedList;
import java.util.Set;

/**
 * Token Ring Protocol for synchronizing agent actions.
 * It provides sequentional control behavior in the group of Agents.
 * The master of the ring is determined as the one with lowest
 * lexicographical value of the directory address (according to {@see String.compareTo}).
 *
 * The ({@link handleToken(String token)}) method is called for token on the whole ring except the invoker.
 * After all calls the {@link TokenRingInform.tokenBack()} method is called on the invoker.
 *
 * @author Jiri Vokrinek
 */
public abstract class TokenRing extends DefaultProtocol {

    static final String TOKEN_RING_PROTOCOL_NAME = "TOKEN_RING_PROTOCOL";
    private final String agentName;
    private String nextAgentAddress = null;
    private String master = null;
    private boolean isMaster = false;
    private int directorySize = 0;
    private final LinkedList<Object> activeTokens = new LinkedList<Object>();
    private final CapabilityRegister directory;

    /**
     *
     * @param communicator
     * @param directory
     * @param name
     */
    public TokenRing(final Communicator communicator, CapabilityRegister directory, String name) {
        super(communicator, TOKEN_RING_PROTOCOL_NAME + ": " + name);
        this.agentName = communicator.getAddress();
        this.directory = directory;
        directory.register(agentName, getName());

        new RequestInformResponder(communicator, getName()) {

            @Override
            protected void handleRequest(final Object request, final String session) {
                // handle it locally if not handled previously
                if (!activeTokens.remove(request)) {
                    handleToken(request, new TokenProcessCallback() {

                        public void processingDone() {
                            passToken(request, new TokenRingInform() {

                                public void tokenBack() {
                                    inform(session, request);

                                }
                            });

                        }
                    });
                } else {
                    inform(session, request);
                }
            }
        };

    }

    /**
     * Method called when a new token is obtained.
     * A {@link TokenProcessCallback} has to be called for token releasing
     * (i.e. passing to next agent in the ring).
     *
     * @param token
     * @param callback
     */
    abstract protected void handleToken(Object token, TokenProcessCallback callback);

    /**
     * Sends an token to the ring. The method {@link handleToken(Object token)} will
     * be called on all agents in the ring. When the ring is finnished {@link callback.tokenBack()} is invoked.
     *
     * @param token a token to be sent
     * @param callback TokenRingInform callback
     */
    synchronized public void invokeToken(Object token, final TokenRingInform callback) {
        activeTokens.add(token);
        passToken(token, callback);
    }

    synchronized private void passToken(Object token, final TokenRingInform callback) {
        refresh();
        new RequestInformInitiator(communicator, getName(), token, nextAgentAddress) {

            @Override
            protected void processInform(Object inform, String session) {
                if (callback != null) {
                    callback.tokenBack();
                }
            }
        };
    }

    /**
     * Check whenewer an agent is master of the ring.
     *
     * @return true if this agent has lexicographically lowest address in the ring.
     */
    public synchronized boolean isMaster() {
        refresh();
        return isMaster;
    }

    /**
     * Gets the address of the master of the ring.
     *
     * @return address of the master of the ring.
     */
    public String getMaster() {
        refresh();
        return master;
    }

    synchronized void refresh() {
        Set<String> addresses = directory.getIdentities(getName());
        if (directorySize != addresses.size()) {
            isMaster = true;
            int lowestDiff = Integer.MAX_VALUE;
            int lowestTotal = Integer.MAX_VALUE;
            String nextOne = null;

            addresses.remove(agentName);
            for (String agent : addresses) {
                int diff = agent.compareTo(agentName);
                if (diff < 0) {
                    isMaster = false;
                    if (diff < lowestTotal) {
                        lowestTotal = diff;
                        master = agent;
                    }
                } else {
                    if (lowestDiff > diff) {
                        lowestDiff = diff;
                        nextOne = agent;
                    }
                }
            }
            directorySize = addresses.size();
            if (nextOne != null) {
                nextAgentAddress = nextOne;
            } else {
                nextAgentAddress = master;
            }

        }
    }

    /**
     * Callback interface for synchronizing.
     * Method {@ tokenBack()} is called when the token is processed by all agents
     * in the ring (except the invoker).
     */
    public interface TokenRingInform {

        /**
         * Method called when the token is processed by all agents
         * in the ring (except the invoker)
         */
        void tokenBack();
    }

    /**
     * Callback interface for token releasing.
     */
    public interface TokenProcessCallback {

        /**
         * Call whne token processing is finished.
         * This method passes the token to next agent.
         */
        void processingDone();
    }
}
