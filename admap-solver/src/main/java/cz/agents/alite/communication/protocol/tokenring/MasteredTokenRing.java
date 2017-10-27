package cz.agents.alite.communication.protocol.tokenring;

import cz.agents.alite.communication.Communicator;
import cz.agents.alite.communication.protocol.simple.SimpleProtocol;
import cz.agents.alite.common.capability.CapabilityRegister;
import java.util.LinkedList;

/**
 * Extended Token Ring Protocol ({@link TokenRing}).
 * When token is invoked, it is passed to master using ({@see SimpleProtocol}) protocol.
 * The master maintains buffer of active tokens and invokes them sequentially.
 * When a token is waiting, no other equal token is accepted (determined by Object equality).
 * The token allready on the way can be accepted to the waiting queue.
 *
 * The ({@link handleToken(String token)}) method is called for each token on the master first and then successively on the whole ring.
 * No {@link TokenRingInform.tokenBack()} method is called.
 *
 * @author Jiri Vokrinek
 */
public abstract class MasteredTokenRing extends TokenRing {

    private final SimpleProtocol simpleProtocol;
    private final LinkedList<Object> waitingTokens = new LinkedList<Object>();
    private boolean tokenOnTheWay = false;

    /**
     *
     * @param communicator
     * @param directory
     * @param name
     */
    public MasteredTokenRing(Communicator communicator, CapabilityRegister directory, String name) {
        super(communicator, directory, name);
        simpleProtocol = new SimpleProtocol(communicator, name) {

            @Override
            protected void processMessage(Object content) {
                invokeToken(content);
            }
        };

    }

    /**
     * Sends an token to the ring. The method {@link handleToken(Object token)} will
     * be called on all agents in the ring.
     *
     * @param token a token to be sent
     */
    synchronized public void invokeToken(Object token) {
        refresh();
        if (isMaster()) {
            if (!waitingTokens.contains(token)) {
                waitingTokens.add(token);
                if (!tokenOnTheWay) {
                    startNextToken();
                }
            }
        } else {
            simpleProtocol.sendMessage(token, getMaster());
        }
    }

    /**
     * Sends an token to the ring. The method {@link handleToken(Object token)} will
     * be called on all agents in the ring.
     *
     * @param token a token to be sent
     * @param callback TokenRingInform callback (ignored!)
     */
    @Override
    synchronized public void invokeToken(Object token, TokenRingInform callback) {
        invokeToken(token);
    }

    synchronized private void startNextToken() {
        tokenOnTheWay = true;
        if (waitingTokens.isEmpty()) {
            tokenOnTheWay = false;
        } else {
            final Object token = waitingTokens.removeFirst();
            handleToken(token, new TokenProcessCallback() {

                public void processingDone() {
                    superInvokeToken(token);
                }
            });

        }

    }

    private void superInvokeToken(Object token) {
        super.invokeToken(token, new TokenRingInform() {

            public void tokenBack() {
                startNextToken();
            }
        });
    }
}
