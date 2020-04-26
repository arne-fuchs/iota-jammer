package spammer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import org.json.*;

/**
 * Read properties file and nodeList file
 */
public class NodeFileReader {

    /**
     * DEV MODE CHANGES PATH
     **/
    private static boolean DEV_MODE = true;

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

    /**
     * Reads the nodes.json file and throws back an Array List with JSON Objects, which were in the nodes.json
     * If some keys are missing the default variable will be used for that key.
     * Minimum keys which are required are protocol(https/http),host(any URL to an node) and port(mostly 14265 or 443) so the spammer can connect to the node.
     * @return Array list of JSON Objects, where the parameters for the spammer are located.
     */
    public static ArrayList<JSONObject> getJsonNodes() {
        String jsonString;
        try {
            if (DEV_MODE) {
                jsonString = Files.readString(Paths.get(System.getProperty("user.dir") + "/target/nodes.json")).toString();
            } else {
                jsonString = Files.readString(Paths.get(System.getProperty("user.dir") + "/nodes.json")).toString();
            }
            JSONObject jsonObject = new JSONObject(jsonString);
            Iterator<String> iterator = jsonObject.keys();
            java.util.ArrayList<JSONObject> jsonObjectArrayList = new java.util.ArrayList<JSONObject>();
            while (iterator.hasNext()) {
                jsonObjectArrayList.add(jsonObject.getJSONObject(iterator.next()));
            }
            return jsonObjectArrayList;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
