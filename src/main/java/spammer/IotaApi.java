package spammer;

import org.iota.jota.IotaAPI;
import org.iota.jota.dto.response.GetNodeInfoResponse;
import org.iota.jota.dto.response.SendTransferResponse;
import org.iota.jota.model.Transfer;
import org.iota.jota.pow.pearldiver.PearlDiverLocalPoW;
import org.iota.jota.utils.TrytesConverter;

import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static spammer.ConsoleColors.GREEN;
import static spammer.ConsoleColors.RESET;
import static spammer.errorHandling.ErrorType.COULD_NOT_SEND_TRANSACTION;


public class IotaApi implements Runnable {

    private final NodeThreadManager nodeThreadManager;
    private IotaAPI api;
    private String nodeURL;

    private Boolean endThread = false;
    private final String threadName;

    //private IOTAJammer iotaJammer;

    Logger logger = Logger.getLogger(IotaApi.class.getName());


    /***
     * Init Object of Iota-Api
     *
     * @param threadId identifier thread
     */
    public IotaApi(int threadId, NodeThreadManager nodeThreadManager) {
        if (nodeURL == null) {
            this.threadName = "Thread: " + threadId;
        } else {
            this.threadName = "Thread: " + threadId + " Node: " + nodeURL;
        }
        this.nodeURL = nodeThreadManager.getNodeURL();
        //this.iotaJammer = nodeThreadManager.getIotaJammer();
        this.nodeThreadManager = nodeThreadManager;
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        logger.setLevel(Level.INFO);

    }


    /**
     * Connect with IOTA-Api to Node
     */
    public void connect() {
        GetNodeInfoResponse response;
        if (nodeURL != null) {
            String protocol = nodeURL.split(":")[0];
            String url = nodeURL.split("//")[1].split(":")[0];
            int port = Integer.parseInt(nodeURL.split(":")[2]);
            try {
                api = nodeThreadManager.isLocalPOW() ? new IotaAPI.Builder()
                        .protocol(protocol)
                        .host(url)
                        .port(port)
                        .localPoW(new PearlDiverLocalPoW())
                        .build() :
                        new IotaAPI.Builder()
                                .protocol(protocol)
                                .host(url)
                                .port(port)
                                .build();

                if (nodeThreadManager.isDEBUG_MODE()) {
                    logger.log(Level.WARNING, api.getNodeInfo().toString());
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, threadName + ": Error: Could not connect to Node! " + (nodeThreadManager.isDEBUG_MODE() ? e.toString() : ""));
                nodeThreadManager.addErrorToThread(COULD_NOT_SEND_TRANSACTION);
            }
        } else {
            try {
                Properties configFile = NodeFileReader.getPropertyFile();
                api = nodeThreadManager.isLocalPOW() ? new IotaAPI.Builder()
                        .config(configFile)
                        .localPoW(new PearlDiverLocalPoW())
                        .build() :
                        new IotaAPI.Builder()
                                .config(configFile)
                                .build();

                response = api.getNodeInfo();
                if (nodeThreadManager.isDEBUG_MODE()) {
                    logger.log(Level.WARNING, response.toString());
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, threadName + ": Error: Could not connect to Node! " + (nodeThreadManager.isDEBUG_MODE() ? e.toString() : ""));
                nodeThreadManager.addErrorToThread(COULD_NOT_SEND_TRANSACTION);
            }
        }
    }

    /**
     * Send Transcation with Message and Tag to Adress
     */
    public void sendZeroValueTransaction() {

        String message = TrytesConverter.asciiToTrytes(nodeThreadManager.getMessage());
        String tag = nodeThreadManager.getTag();
        int securityLevel = 2;
        int value = 0;

        Transfer zeroValueTransaction = new Transfer(nodeThreadManager.getAddress(), value, message, tag);

        @SuppressWarnings("Convert2Diamond")
        ArrayList<Transfer> transfers = new ArrayList<Transfer>();

        for(int i = 0;i < nodeThreadManager.getBundlesize();i++)
            transfers.add(zeroValueTransaction);

        int depth = nodeThreadManager.getDepth();
        int minimumWeightMagnitude = nodeThreadManager.getMwm();

        try {
            SendTransferResponse response = api.sendTransfer(nodeThreadManager.getSeed(), securityLevel, depth, minimumWeightMagnitude, transfers, null, null, false, false, null);
            nodeThreadManager.raiseTps();
            if (nodeThreadManager.isDEBUG_MODE()) {
                logger.log(Level.INFO, threadName + response.getTransactions());
            }
            logger.log(Level.INFO, GREEN + threadName + ": Transaction complete!" + RESET);
        } catch (Exception e) {
            logger.log(Level.WARNING, threadName + " Error: Could not send transaction! "  + (nodeThreadManager.isDEBUG_MODE() ? e.toString() : ""));
            nodeThreadManager.addErrorToThread(COULD_NOT_SEND_TRANSACTION);
        }
    }

    @Override
    public void run() {
        connect();
            if (nodeThreadManager.getReconnect() > 0) {
                while (!endThread) {
                    connect();
                    for(int i = 0; i < nodeThreadManager.getReconnect();i++){
                        sendZeroValueTransaction();
                    }
                }
            }else
            while (!endThread) {
                sendZeroValueTransaction();
            }
    }

    /**
     * pause the thread, if there is a error
     */
    public synchronized void pauseAfterError(int waititme) {
        try {
            this.wait(waititme);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * stops the thread
     */
    public synchronized void setEndThread() {
        endThread = true;
    }
}