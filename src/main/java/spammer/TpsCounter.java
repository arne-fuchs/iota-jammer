package spammer;

/**
 * Counts the TPS
 *
 * @author Arne Fuchs
 */
public class TpsCounter implements Runnable {

    private String line = "---------------------------------------------------------------------------------------------------------------------------------------------------------";
    private float totalTps;
    private float highestTps;
    private boolean endThread = false;
    private IOTAJammer iotaJammer;
    private boolean firstrun = true;

    /**
     * TpsCounter()
     */
    public TpsCounter(IOTAJammer iotaJammer) {
        this.iotaJammer = iotaJammer;
    }

    @Override
    public void run() {
        int waitTimeInMillis = 10000;
        highestTps = 0.00f;
        java.util.ArrayList<NodeDataPair> nodeDataPairs;
        while (!endThread) {
            try {
                if (!firstrun) {
                    nodeDataPairs = collectNodeTps();
                    for (int i = 0; i < iotaJammer.getNodeListSize(); i++) {
                        iotaJammer.getNode(i).resetTps();
                    }
                    System.out.println(line);
                    if (nodeDataPairs.size() >= 2) {
                        int n = 0;
                        for (NodeDataPair nodeDataPair : nodeDataPairs) {
                            totalTps += nodeDataPair.tps;
                            System.out.printf("%-40s %1.2f %3s %5d %11s %2s", nodeDataPair.url, (float) nodeDataPair.tps / 10, "Tps",nodeDataPair.totalTransactions, "Transactions","| ");
                            n++;
                            if (n == 2) {
                                System.out.print("\n");
                                n = 0;
                            }
                        }
                    } else {
                        for (NodeDataPair nodeDataPair : nodeDataPairs) {
                            System.out.printf("%-40s %1.2f %3s %5d %11s %2s", nodeDataPair.url, (float) nodeDataPair.tps / 10, "Tps",nodeDataPair.totalTransactions, "Transactions","| ");
                        }
                        System.out.println("\n");
                    }
                    totalTps = totalTps / 10;
                    if (highestTps < totalTps)
                        highestTps = totalTps;
                    System.out.println(line);
                    System.out.printf("%-19s %1.2f %3s %2s", "Total average tps:", totalTps, "Tps", "| ");
                    System.out.printf("%-19s %1.2f %3s %2s", "Highest tps:", highestTps, "Tps", "| ");
                    System.out.printf("%-19s %1.1f %2s", "IOTA-Jammer Version", 1.4f, "| \n");
                    totalTps = 0;
                    System.out.println(line);
                }
                firstrun = false;
                Thread.sleep(waitTimeInMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns ArrayList of Datatype with URL and TPS
     *
     * @return NodeDataPair with URL and TPS
     */
    private java.util.ArrayList<NodeDataPair> collectNodeTps() {

        java.util.ArrayList<NodeDataPair> nodeDataPairs = new java.util.ArrayList<NodeDataPair>();

        for (int i = 0; i < iotaJammer.getNodeListSize(); i++) {
            nodeDataPairs.add(new NodeDataPair(iotaJammer.getNode(i).getNodeURL(), iotaJammer.getNode(i).getTps(),iotaJammer.getNode(i).getTotalTransactions()));
        }
        return nodeDataPairs;
    }
}

/**
 * Just to save the tps (transactions per seconds) per node
 */
class NodeDataPair {
    public String url;
    public int tps;
    public int totalTransactions;

    public NodeDataPair(String url, int tps,int totalTransactions) {
        this.url = url == null ? Thread.currentThread().getName() : url;
        this.tps = tps;
        this.totalTransactions = totalTransactions;
    }
}
