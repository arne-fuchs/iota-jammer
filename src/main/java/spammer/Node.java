package spammer;

import spammer.errorHandling.ErrorType;
import spammer.errorHandling.ThreadError;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static spammer.ConsoleColors.RED_BOLD;
import static spammer.ConsoleColors.RESET;

public class Node {
    private final int MAX_ERRORS_PER_THREAD = 5;

    private int amountThreads;
    private Thread[] threads;
    private IotaApi[] iotaApi;

    private String nodeURL;
    private IOTAJammer iotaJammer;
    private ArrayList<ThreadError> threadErrors = new ArrayList<>();

    Logger logger = Logger.getLogger(IotaApi.class.getName());

    private int tps = 0;

    /**
     * Constructor
     */
    public Node(String nodeURL, IOTAJammer iotaJammer, int threads) {
        this.threads = new Thread[threads];
        this.amountThreads = threads;
        this.iotaApi = new IotaApi[threads];
        this.nodeURL = nodeURL;
        this.iotaJammer = iotaJammer;
    }

    /**
     * Starts all Threads of the Node
     */
    public void initThreads() {
        for (int i = 0; i < amountThreads; i++) {
            iotaApi[i] = new IotaApi(i, nodeURL, iotaJammer, this);
            threads[i] = new Thread(iotaApi[i], String.valueOf(i));
        }
        for (int i = 0; i < amountThreads; i++) {
            threads[i].start();
        }
    }

    /**
     * Adds ErrorMessage to the ErrorList
     */
    public synchronized void addErrorToThread(ErrorType errorType) {
        if (threadErrors.size() == MAX_ERRORS_PER_THREAD) {
            threadErrors.add(new ThreadError(errorType));
            logger.log(Level.WARNING, RED_BOLD + " Spamming " + nodeURL + " stopped by error!" + RESET);
            for (int i = 0; i < threads.length; i++) {
                int randomTime = new Random().nextInt(300000);
                iotaApi[i].pauseAfterError((60000 * 60 * 2) + randomTime);
            }
        } else {
            threadErrors.add(new ThreadError(errorType));
            for (int i = 0; i < threads.length; i++) {
                int randomTime = new Random().nextInt(300000);
                iotaApi[i].pauseAfterError(300000 + randomTime);
            }
        }
    }

    /**
     * Returns tps (transactions per seconds)
     *
     * @return tps
     */
    public int getTps() {
        return tps;
    }

    /**
     * Returns nodeURL
     *
     * @return nodeURL
     */
    public String getNodeURL() {
        return nodeURL;
    }

    /**
     * Raises tps by 1
     */
    public void raiseTps() {
        tps++;
    }

    /**
     * resets tps counter
     */
    public void resetTps() {
        tps = 0;
    }
}