package spammer;

import org.iota.jota.utils.SeedRandomGenerator;

import java.util.ArrayList;
import java.util.Arrays;

import static spammer.ConsoleColors.*;

/**
 * MainClass
 *
 * @author Arne Fuchs, Robin Schumacher
 */
public class IOTAJammer {

    /**
     * DEBUG MODE
     **/
    private boolean DEBUG_MODE = false;
    private ArrayList<Node> nodes = new ArrayList<Node>();

    private boolean nodeListEnabled = false;
    private boolean localPOW = false;
    private int threadAmount = 1;
    private long delay = 0;

    private int reconnect = 0;
    private int mwm = 0;
    private int depth = 0;

    private String seed = null;
    private String address = null;
    private String tag = null;
    private String message = null;


    /**
     * Starts IOTAJammer
     */
    public static void main(String[] args) {

        IOTAJammer iotaJammer = new IOTAJammer();

        System.out.println("Arguments: " + Arrays.toString(args));
        for (String argument : args) {
            switch (argument.split(" ")[0]) {
                case "EnableNodeList":
                    iotaJammer.nodeListEnabled = true;
                    break;
                case "EnableLocalPOW":
                    iotaJammer.localPOW = true;
                    break;
                case "EnableDebug":
                    iotaJammer.DEBUG_MODE = true;
                    break;
                case "seed":
                    iotaJammer.seed = argument.split(" ")[1];
                    break;
                case "address":
                    iotaJammer.address = argument.split(" ")[1];
                    break;
                case "tag":
                    iotaJammer.tag = argument.split(" ")[1];
                    break;
                case "message":
                    iotaJammer.message = argument.split(" ")[1];
                    break;
                case "threads":
                    iotaJammer.threadAmount = Integer.parseInt(argument.split(" ")[1]);
                    break;
                case "reconnect":
                    iotaJammer.reconnect = Integer.parseInt(argument.split(" ")[1]);
                    break;
                case "mwm":
                    iotaJammer.mwm = Integer.parseInt(argument.split(" ")[1]);
                    break;
                case "depth":
                    iotaJammer.depth = Integer.parseInt(argument.split(" ")[1]);
                    break;
                case "delay":
                    iotaJammer.delay = Integer.parseInt(argument.split(" ")[1]);
                    break;
                default:
                    System.out.println(RED + "Invalid Argument: " + argument);
            }
        }
        try {
            iotaJammer.threadManager();
        }
        catch(InterruptedException e){
            System.out.println("Failed starting threads with delay! Try removing the \"delay\" argument!");
        }
    }

    /***
     * Start all threads for the IOTAJammer
     *
     */
    private void threadManager() throws InterruptedException {
        if (threadAmount > 1 && localPOW || localPOW && nodeListEnabled) {
            System.out.println(RED + "Warning! " + YELLOW + "Having more than 1 Thread or node list enabled and local proof of work enabled can lead the pc to be laggy!" + RESET);
        }
        if (nodeListEnabled) {
            String[] nodeList = NodeFileReader.getNodesArray();
            for (String url : nodeList) {
                nodes.add(new Node(url, this, threadAmount));//Creates Nodes with url from nodeList and thread amount
            }
            for (int i = 0; i < nodeList.length; i++) {
                if(delay == 0)
                    nodes.get(i).initThreads();//Starts all Threads from Nodes
                else
                    nodes.get(i).initThreads(delay);//Starts all Threads from Nodes with delay
            }
        } else {
            for (int i = 0; i < threadAmount; i++) {
                nodes.add(new Node(null, this, threadAmount));//Creates Nodes from .properties and thread amount
                if(delay == 0)
                    nodes.get(i).initThreads();
                else
                    nodes.get(i).initThreads(delay);
            }
        }
        new Thread(new TpsCounter(this), "tpsCounter").start();
        System.out.println("Finished starting " + threadAmount + " Threads");
    }

    /**
     * isLocalPOW Enabled
     *
     * @return isLocalPOW
     */
    public boolean isLocalPOW() {
        return localPOW;
    }

    /***
     * Getter seed
     * @return seed
     */
    public String getSeed() {
        return seed == null ? SeedRandomGenerator.generateNewSeed() : seed;
    }

    /**
     * Getter Adress
     *
     * @return address
     */
    public String getAddress() {
        return address == null ? "9FNJWLMBECSQDKHQAGDHDPXBMZFMQIMAFAUIQTDECJVGKJBKHLEBVU9TWCTPRJGYORFDSYENIQKBVSYKW9NSLGS9UW" : address;
    }

    public String getTag() {
        return tag == null ? "IOTAJAMMER" : tag;
    }

    /**
     * isDEBUG_MODE
     *
     * @return DEBUG_MODE
     */
    public boolean isDEBUG_MODE() {
        return DEBUG_MODE;
    }

    /**
     * Returns one Node from nodes ArrayList
     *
     * @param index
     * @return node
     */
    public Node getNode(int index) {
        return nodes.get(index);
    }

    /**
     * Returns size  from nodes ArrayList
     *
     * @return int nodes.size()
     */
    public int getNodeListSize() {
        return nodes.size();
    }

    public String getMessage() { return message == null ? "https://paesserver.de/iota-jammer.html" : message; }

    public int getReconnect() {
        return reconnect;
    }

    public int getMwm(){
        return mwm == 0 ? 14 : mwm;
    }

    public int getDepth() {
        return depth == 0 ? 4 : depth;
    }

}