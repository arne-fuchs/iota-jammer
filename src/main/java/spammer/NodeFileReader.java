package spammer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Read properties file and nodeList file
 */
public class NodeFileReader {

    /**
     * DEV MODE CHANGES PATH
     **/
    private static boolean DEV_MODE = false;

    /**
     * Read nodeList File
     *
     * @return Get all nodes in nodeList file as String Array
     */
    public static String[] getNodesArray() {
        java.util.ArrayList<String> nodeList = new java.util.ArrayList<>();

        try {
            FileInputStream fileInputStream;
            if (DEV_MODE) {
                fileInputStream = new FileInputStream(System.getProperty("user.dir") + "/target/nodeList");
            } else {
                fileInputStream = new FileInputStream(System.getProperty("user.dir") + "/nodeList");
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String NodeListLine;
            while ((NodeListLine = bufferedReader.readLine()) != null) {
                nodeList.add(NodeListLine);
            }
            bufferedReader.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] nodeArray = new String[nodeList.size()];
        nodeArray = nodeList.toArray(nodeArray);
        return nodeArray;
    }

    /**
     * Read properties file
     *
     * @return Properties file (node_config.properties)
     * @throws IOException If file is missing
     */
    public static Properties getPropertyFile() throws IOException {
        Properties configFile = new java.util.Properties();
        FileInputStream inputStream;

        if (DEV_MODE) {
            inputStream = new FileInputStream(System.getProperty("user.dir") + "/target/node_config.properties");
        } else {
            inputStream = new FileInputStream(System.getProperty("user.dir") + "/node_config.properties");
        }

        configFile.load(inputStream);

        return configFile;
    }
}
