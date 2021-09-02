package edu.umass.cs.xdn.tools;

import edu.umass.cs.gigapaxos.PaxosConfig;
import edu.umass.cs.gigapaxos.interfaces.Request;
import edu.umass.cs.reconfiguration.http.HttpActiveReplicaPacketType;
import edu.umass.cs.reconfiguration.http.HttpActiveReplicaRequest;
import edu.umass.cs.xdn.XDNConfig;
import edu.umass.cs.xdn.deprecated.XDNAgentClient;
import edu.umass.cs.xdn.request.XDNAppHttpRequest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

public class OwnCloudUploaderClient {

    String serviceName;
    String name;
    String imageName;
    String value;
    boolean coord;
    int numReq;
    String target;

    int id;
    static int received = 0;

    final private static long timeout = 1000;
    XDNAgentClient client;

    private OwnCloudUploaderClient() throws IOException{
        XDNConfig.load();
        name = XDNConfig.prop.getProperty(XDNConfig.XC.NAME.toString());
        imageName = XDNConfig.prop.getProperty(XDNConfig.XC.IMAGE_NAME.toString());
        value = XDNConfig.prop.getProperty(XDNConfig.XC.VALUE.toString());
        coord = Boolean.parseBoolean(XDNConfig.prop.getProperty(XDNConfig.XC.COORD.toString()));

        numReq = 5;
        if ( XDNConfig.prop.getProperty(XDNConfig.XC.NUM_REQ.toString()) != null)
            numReq = Integer.parseInt(XDNConfig.prop.getProperty(XDNConfig.XC.NUM_REQ.toString()));

        target = null;
        if( XDNConfig.prop.getProperty(XDNConfig.XC.TARGET.toString()) != null)
            target = XDNConfig.prop.getProperty(XDNConfig.XC.TARGET.toString());


        serviceName = XDNConfig.generateServiceName(imageName, name);

        id = (new Random()).nextInt();

        client = new XDNAgentClient();
    }

    private HttpActiveReplicaRequest getRequest() throws Exception {
        // User credentials for OwnCloud service
        String username = "admin";
        String password = "admin";
        String authString = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

        // Description of file that will be uploaded
        String fileName = "uploaded-file-" + UUID.randomUUID().toString() + ".png";
        byte[] fileContent = Base64.getDecoder().decode("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".getBytes("UTF-8"));

        // Http request that will be forwarded to replica
        XDNAppHttpRequest r = new XDNAppHttpRequest();
        r.setPath("/remote.php/webdav/" + fileName);
        r.setMethod("PUT");
        r.addHeader("Authorization", "Basic " + authString);
        r.addHeader("Accept", "*/*");
        r.addHeader("Content-Type", "application/x-www-form-urlencoded");
        r.addHeader("Content-Length", String.valueOf(fileContent.length));
        r.setPayload(fileContent);

        return new HttpActiveReplicaRequest(
            HttpActiveReplicaPacketType.EXECUTE,
            serviceName,
            id++,
            r.toJSONString(),
            coord,
            false,
            0
        );
    }

    private void sendRequest() {
        InetSocketAddress addr = null;
        if ( target !=null ){
            addr = PaxosConfig.getActives().get(target);
        }

        System.out.println("OwnCloudUploaderClient:>> Sending request to target: "+target+", address:"+addr);
        Request result = null;
        try {
            result = client.sendRequest(getRequest(), timeout);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result == null){
            System.out.println("OwnCloudUploaderClient:>> Request timed out");
        }
    }

    private void close() {
        client.close();
    }

    public static void main(String[] args) throws IOException {
        OwnCloudUploaderClient c = new OwnCloudUploaderClient();
        c.sendRequest();
        c.close();
    }
}