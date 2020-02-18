package spammer;

import org.iota.jota.IotaAPI;
import org.iota.jota.dto.response.GetNodeInfoResponse;
import org.iota.jota.dto.response.SendTransferResponse;
import org.iota.jota.model.Transfer;
import org.iota.jota.pow.pearldiver.PearlDiverLocalPoW;
import org.iota.jota.utils.TrytesConverter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static spammer.ConsoleColors.GREEN;
import static spammer.ConsoleColors.RESET;
import static spammer.errorHandling.ErrorType.COULD_NOT_SEND_TRANSACTION;


public class IotaApi implements Runnable {

    private Node node;
    private IotaAPI api;
    private String nodeURL;

    private Boolean endThread = false;
    private String threadName;

    private IOTAJammer iotaJammer;

    Logger logger = Logger.getLogger(IotaApi.class.getName());


    /***
     * Init Object of Iota-Api
     *
     * @param iotaJammer as Controller
     * @param nodeURL URL to node
     * @param threadId identifier thread
     */
    public IotaApi(int threadId, String nodeURL, IOTAJammer iotaJammer, Node node) {
        if (nodeURL == null) {
            this.threadName = "Thread: " + threadId;
        } else {
            this.threadName = "Thread: " + threadId + " Node: " + nodeURL;
        }
        this.nodeURL = nodeURL;
        this.iotaJammer = iotaJammer;
        this.node = node;
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
                api = iotaJammer.isLocalPOW() ? new IotaAPI.Builder()
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

                if (iotaJammer.isDEBUG_MODE()) {
                    logger.log(Level.WARNING, api.getNodeInfo().toString());
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, threadName + ": Error: Could not connect to Node!");
                node.addErrorToThread(COULD_NOT_SEND_TRANSACTION);
            }
        } else {
            try {
                Properties configFile = NodeFileReader.getPropertyFile();
                api = iotaJammer.isLocalPOW() ? new IotaAPI.Builder()
                        .config(configFile)
                        .localPoW(new PearlDiverLocalPoW())
                        .build() :
                        new IotaAPI.Builder()
                                .config(configFile)
                                .build();

                response = api.getNodeInfo();
                if (iotaJammer.isDEBUG_MODE()) {
                    logger.log(Level.WARNING, response.toString());
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, threadName + ": Error: Could not connect to Node!");
                node.addErrorToThread(COULD_NOT_SEND_TRANSACTION);
            }
        }
    }

    /**
     * Send Transcation with Message and Tag to Adress
     */
    public void sendZeroValueTransaction() {

        String message = TrytesConverter.asciiToTrytes("Transaktion am " + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(System.currentTimeMillis()));
        String tag = iotaJammer.getTag();
        int securityLevel = 2;
        int value = 0;

        Transfer zeroValueTransaction = new Transfer(iotaJammer.getAddress(), value, message, tag);

        @SuppressWarnings("Convert2Diamond")
        ArrayList<Transfer> transfers = new ArrayList<Transfer>();

        transfers.add(zeroValueTransaction);


        int depth = 4;
        int minimumWeightMagnitude = 14;

        try {
            SendTransferResponse response = api.sendTransfer(iotaJammer.getSeed(), securityLevel, depth, minimumWeightMagnitude, transfers, null, null, false, false, null);
            node.raiseTps();
            if (iotaJammer.isDEBUG_MODE()) {
                logger.log(Level.INFO, threadName + response.getTransactions());
            }
            logger.log(Level.INFO, GREEN + threadName + ": Transaction complete!" + RESET);
        } catch (Exception e) {
            logger.log(Level.WARNING, threadName + " Error: Could not send transaction!");
            node.addErrorToThread(COULD_NOT_SEND_TRANSACTION);
        }
    }

    @Override
    public void run() {
        connect();
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