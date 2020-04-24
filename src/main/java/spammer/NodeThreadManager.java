package spammer;

import org.json.JSONException;
import org.json.JSONObject;
import spammer.errorHandling.ErrorType;
import spammer.errorHandling.ThreadError;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static spammer.ConsoleColors.RED_BOLD;
import static spammer.ConsoleColors.RESET;

public class NodeThreadManager extends VariablesHolder{
    private final int MAX_ERRORS_PER_THREAD = 5;

    private Thread[] threads;
    private IotaApi[] iotaApi;
    private IOTAJammer iotaJammer;

    private final String nodeURL;

    private ArrayList<ThreadError> threadErrors = new ArrayList<>();

    Logger logger = Logger.getLogger(IotaApi.class.getName());

    private int tps = 0;

    /**
     * Constructor if the NodeThreadManager is loaded via normal URL. The variables will be loaded over the arguments or the default one will be used, if not specified.
     * @param nodeURL Nodeurl in String format (e.g. https://mynode.com:14265)
     * @param iotaJammer Just the main instance so parameters can be loaded.
     */
    public NodeThreadManager(String nodeURL, IOTAJammer iotaJammer) {
        this.threads = new Thread[iotaJammer.getThreadAmount()];
        this.iotaJammer = iotaJammer;
        this.iotaApi = new IotaApi[iotaJammer.getThreadAmount()];
        this.nodeURL = nodeURL;

        this.seed = iotaJammer.getSeed();
        this.address = iotaJammer.getAddress();
        this.tag = iotaJammer.getTag();
        this.message = iotaJammer.getMessage();

        this.reconnect = iotaJammer.getReconnect();
        this.mwm = iotaJammer.getMwm();
        this.depth = iotaJammer.getDepth();

        this.localPOW = iotaJammer.isLocalPOW();
        this.threadAmount = iotaJammer.getThreadAmount();
    }

    /**
     * Constructor if the NodeThreadManager is loaded over the nodes.json.
     * The NodeThreadMananger will try to get the parameters out of the json first. If some parameters are not specified the default ones will be loaded.
     * @param jsonObject
     * @param iotaJammer
     */
    public NodeThreadManager(JSONObject jsonObject, IOTAJammer iotaJammer){
        this.iotaJammer = iotaJammer;
        this.nodeURL = jsonObject.getString("protocol")+ "://" + jsonObject.getString("host") + ":" + jsonObject.getInt("port");

        try { this.seed = jsonObject.getString("seed"); } catch (JSONException e){ this.seed = getSeed();}
        try { this.address = jsonObject.getString("address"); } catch (JSONException e){ this.address = getAddress();}
        try { this.tag = jsonObject.getString("tag"); } catch (JSONException e){ this.tag = getTag();}
        try { this.message = jsonObject.getString("message"); } catch (JSONException e){ this.message = getMessage();}

        try { this.reconnect = jsonObject.getInt("reconnect"); } catch (JSONException e){ this.reconnect = getReconnect();}
        try { this.mwm = jsonObject.getInt("mwm"); } catch (JSONException e){ this.mwm = getMwm();}
        try { this.depth = jsonObject.getInt("depth"); } catch (JSONException e){ this.depth = getDepth();}

        try { this.localPOW = jsonObject.getBoolean("EnableLocalPOW"); } catch (JSONException e){ this.localPOW = isLocalPOW();}
        try { this.threadAmount = jsonObject.getInt("threads"); } catch (JSONException e){ this.threadAmount = getThreadAmount();}
        try { this.threads = new Thread[jsonObject.getInt("threads")]; } catch (JSONException e){ this.threads = new Thread[getThreadAmount()];}
        try { this.iotaApi = new IotaApi[jsonObject.getInt("threads")]; } catch (JSONException e){ this.iotaApi = new IotaApi[getThreadAmount()];}
    }

    /**
     * Starts all Threads of the Node
     */
    public void initThreads() {
        for (int i = 0; i < threadAmount; i++) {
            iotaApi[i] = new IotaApi(i, this);
            threads[i] = new Thread(iotaApi[i], String.valueOf(i));
        }
        for (int i = 0; i < threadAmount; i++) {
            threads[i].start();
        }
    }

    /**
     * Starts all Threads of Node with delay
     * @param delay delay in milliseconds
     * @throws InterruptedException
     */
    public void initThreads(long delay) throws InterruptedException {
        for (int i = 0; i < threadAmount; i++) {
            iotaApi[i] = new IotaApi(i, this);
            threads[i] = new Thread(iotaApi[i], String.valueOf(i));
        }
        for (int i = 0; i < threadAmount; i++) {
            Thread.sleep(delay);
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
     * Just some getter to track the tps (transaction per seconds) and to get the NodeURL
     */
    public int getTps() {
        return tps;
    }

    public String getNodeURL() {
        return nodeURL;
    }

    public void raiseTps() {
        tps++;
    }

    public void resetTps() {
        tps = 0;
    }
}