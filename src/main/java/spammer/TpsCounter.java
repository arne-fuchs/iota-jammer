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
                    if (nodeDataPairs.size() >= 3) {
                        int n = 0;
                        for (NodeDataPair nodeDataPair : nodeDataPairs) {
                            totalTps += nodeDataPair.tps;
                            System.out.printf("%-40s %1.2f %3s %2s", nodeDataPair.url, (float) nodeDataPair.tps / 10, "Tps", "| ");
                            n++;
                            if (n == 3) {
                                System.out.print("\n");
                                n = 0;
                            }
                        }
                    } else {
                        for (NodeDataPair nodeDataPair : nodeDataPairs) {
                            System.out.printf("%-40s %1.2f %3s %2s", nodeDataPair.url, (float) nodeDataPair.tps / 10, "Tps", "| ");
                        }
                        if (nodeDataPairs.size() % 3 != 0)
                            System.out.println("\n \n");
                    }
                    totalTps = totalTps / 10;
                    if (highestTps < totalTps)
                        highestTps = totalTps;
                    System.out.println(line);
                    System.out.printf("%-40s %1.2f %3s %2s", "Total average tps:", totalTps, "Tps", "| ");
                    System.out.printf("%-40s %1.2f %3s %2s", "Highest tps:", highestTps, "Tps", "| ");
                    System.out.printf("%-40s %1.1f %3s %2s", "IOTA-Jammer Version", 1.1f, "", "| \n");
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
            nodeDataPairs.add(new NodeDataPair(iotaJammer.getNode(i).getNodeURL(), iotaJammer.getNode(i).getTps()));
        }
        return nodeDataPairs;
    }
}

class NodeDataPair {
    public String url;
    public int tps;

    public NodeDataPair(String url, int tps) {
        this.url = url == null ? Thread.currentThread().getName() : url;
        this.tps = tps;
    }
}
