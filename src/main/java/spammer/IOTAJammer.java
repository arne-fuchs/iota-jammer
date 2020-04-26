package spammer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import static spammer.ConsoleColors.*;

/**
 * MainClass
 *
 * @author Arne Fuchs, Robin Schumacher
 */
public class IOTAJammer extends VariablesHolder{

    private ArrayList<NodeThreadManager> nodeThreadManagers = new ArrayList<NodeThreadManager>();


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
                case "EnableJSONList":
                    iotaJammer.jsonListEnabled = true;
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
                    iotaJammer.message = argument.split(" ",2)[1];
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
                    System.out.println(RED + "Invalid Argument: " + argument + RESET);
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
        new Thread(new TpsCounter(this), "tpsCounter").start();
        if (threadAmount > 1 && localPOW || localPOW && nodeListEnabled || jsonListEnabled) {
            System.out.println(RED + "Warning! " + YELLOW + "Having more than 1 thread and local proof of work enabled can lead the pc to be laggy!" + RESET);
        }
        if (nodeListEnabled) {
            String[] nodeList = NodeFileReader.getNodesArray();
            for (String url : nodeList) {
                nodeThreadManagers.add(new NodeThreadManager(url, this));//Creates Nodes with url from nodeList and thread amount
            }
        } else {//Creates Nodes from .properties and thread amount
            if(delay == 0 && !jsonListEnabled)
                new NodeThreadManager((String) null, this).initThreads();
            else
                new NodeThreadManager((String) null, this).initThreads(delay);
        }
        if(jsonListEnabled){
            java.util.ArrayList<JSONObject> jsonObjectsArrayList = NodeFileReader.getJsonNodes();
            if(jsonObjectsArrayList != null)
                for(JSONObject jsonObject : jsonObjectsArrayList){
                    nodeThreadManagers.add(new NodeThreadManager(jsonObject,this));
                }
        }
        for (int i = 0; i < nodeThreadManagers.size(); i++) {
            if(delay == 0)
                nodeThreadManagers.get(i).initThreads();//Starts all Threads from Nodes
            else
                nodeThreadManagers.get(i).initThreads(delay);//Starts all Threads from Nodes with delay
        }
        System.out.println("Finished starting threads");
    }



    /**
     * Returns one Node from nodes ArrayList
     *
     * @param index
     * @return node
     */
    public NodeThreadManager getNode(int index) {
        return nodeThreadManagers.get(index);
    }

    /**
     * Returns size  from nodes ArrayList
     *
     * @return int nodes.size()
     */
    public int getNodeListSize() {
        return nodeThreadManagers.size();
    }

}