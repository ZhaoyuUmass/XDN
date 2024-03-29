package test;

import edu.umass.cs.gigapaxos.PaxosConfig;
import edu.umass.cs.gigapaxos.interfaces.Request;
import edu.umass.cs.gigapaxos.interfaces.RequestCallback;
import edu.umass.cs.xdn.XDNConfig;
import edu.umass.cs.xdn.docker.DockerKeys;
import edu.umass.cs.xdn.deprecated.XDNAgentClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CreateServices {

    static int received = 0;

    // docker related info
    private static final String dockerHubAccount = "oversky710";

    private static final String imageName = "xdn-demo-app";
    private static final int port = 3000;
    private static final int public_expose_port = 80;

    private static final String xdnServiceNameDecimal = "_xdn_";

    private static final String name = "Alvin";

    protected static String generateServiceName() {
        return imageName+xdnServiceNameDecimal+name;
    }

    public static void main(String[] args) throws IOException, InterruptedException, JSONException {



        Map<String, InetSocketAddress> servers = PaxosConfig.getActives();

        Set<InetSocketAddress> initGroup = new HashSet<>();
        for(String name: servers.keySet()){
            initGroup.add(servers.get(name));
        }

        System.out.println("InitGroup:"+initGroup);
        XDNAgentClient client = new XDNAgentClient();

        JSONObject json = new JSONObject();
        json.put(DockerKeys.NAME.toString(), imageName);
        json.put(DockerKeys.IMAGE_URL.toString(), dockerHubAccount+"/"+imageName);
        json.put(DockerKeys.PORT.toString(), port);
        json.put(DockerKeys.VOL.toString(), imageName);
        json.put(DockerKeys.PUBLIC_EXPOSE_PORT.toString(), public_expose_port);

        final int sent = 1;

        String testServiceName = generateServiceName();

        //client.sendRequest(new CreateServiceName(testServiceName,
        //                json.toString(), initGroup),
        client.sendRequest(new edu.umass.cs.reconfiguration.reconfigurationpackets.CreateServiceName(testServiceName,
                json.toString()),
                new RequestCallback() {
                    final long createTime = System.currentTimeMillis();
                    @Override
                    public void handleResponse(Request response) {
                        System.out.println("Response to create service name ="
                                + (response)
                                + " received in "
                                + (System.currentTimeMillis() - createTime)
                                + "ms");
                        received += 1;
                    }
                }
        );

        while (sent > received) {
            Thread.sleep(500);
        }

        Thread.sleep(1000);

        System.out.println("Service name created successfully.");
        client.close();
        // System.exit(0);
    }
}
