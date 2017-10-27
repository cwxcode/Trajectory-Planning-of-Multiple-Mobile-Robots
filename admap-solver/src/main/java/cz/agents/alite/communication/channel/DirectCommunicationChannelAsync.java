package cz.agents.alite.communication.channel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cz.agents.alite.communication.CommunicationReceiver;
import cz.agents.alite.communication.Message;

/**
 * Asynchronous version of {@link DirectCommunicationChannel}.
 * Uses direct calling of the receiveMessage method using asynchronous {@link Executors}.
 * The number of threads in the executos pool correspond to the number of processor cores
 * {@see Runtime.getRuntime().availableProcessors()}.
 *
 * @author Jiri Vokrinek
 */
public class DirectCommunicationChannelAsync extends DirectCommunicationChannel {

    private final ExecutorService executorService;
    @Deprecated
    private static ExecutorService obsoleteExecutorService = null;

    /**
     *
     * @param communicator
     */
    @Deprecated
    public DirectCommunicationChannelAsync(CommunicationReceiver communicator) throws CommunicationChannelException {
        super(communicator);
        if (obsoleteExecutorService == null) {
            int availableProcessors = Runtime.getRuntime().availableProcessors();
            obsoleteExecutorService = Executors.newFixedThreadPool(availableProcessors);
        }
        executorService = obsoleteExecutorService;
    }

    @Deprecated
    public DirectCommunicationChannelAsync(CommunicationReceiver communicator, ReceiverTable channelReceiverTable) throws CommunicationChannelException {
        super(communicator, channelReceiverTable);
        if (obsoleteExecutorService == null) {
            int availableProcessors = Runtime.getRuntime().availableProcessors();
            obsoleteExecutorService = Executors.newFixedThreadPool(availableProcessors);
        }
        executorService = obsoleteExecutorService;
    }

    public DirectCommunicationChannelAsync(CommunicationReceiver communicator, ReceiverTable channelReceiverTable, ExecutorService executorService) throws CommunicationChannelException {
        super(communicator, channelReceiverTable);
        this.executorService = executorService;
    }

    /**
     * Asynchronous direct call using {@link Executors}.
     *
     * @param receiver
     * @param message
     */
    @Override
    protected void callDirectReceive(final CommunicationReceiver receiver, final Message message) {
        executorService.submit(new Runnable() {

            @Override
            public void run() {
                receiver.receiveMessage(message);
            }
        });
    }
}
