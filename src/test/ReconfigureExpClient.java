package test;

import edu.umass.cs.gigapaxos.PaxosConfig;
import edu.umass.cs.gigapaxos.interfaces.Request;
import edu.umass.cs.gigapaxos.interfaces.RequestCallback;
import edu.umass.cs.reconfiguration.http.HttpActiveReplicaPacketType;
import edu.umass.cs.reconfiguration.http.HttpActiveReplicaRequest;
import edu.umass.cs.reconfiguration.reconfigurationpackets.ReplicableClientRequest;
import edu.umass.cs.xdn.XDNConfig;
import edu.umass.cs.xdn.deprecated.XDNAgentClient;

import java.io.IOException;
import java.util.Random;

/**
 * java -ea -cp jar/XDN-1.0.jar -Djava.util.logging.config.file=conf/logging.properties -Dlog4j.configuration=conf/log4j.properties -DgigapaxosConfig=conf/xdn.properties test.ReconfigurableServices
 */
public class ReconfigureExpClient {
    static int received = 0;
    final static long interval = 1000;

    public static void main(String[] args) throws IOException, InterruptedException {
        String node = args[0];

        XDNAgentClient client = new XDNAgentClient();

        String testServiceName = "xdn-demo-app"+ XDNConfig.xdnServiceDecimal+"Alvin";


        int total = 30;

        int id = (new Random()).nextInt();

        // System.out.println("Start testing... ");
        for (int i=0; i<total; i++) {
            HttpActiveReplicaRequest req = new HttpActiveReplicaRequest(HttpActiveReplicaPacketType.EXECUTE,
                    testServiceName,
                    id++,
                    "1",
                    true,
                    false,
                    0
            );
            // AppRequest request = new AppRequest(testServiceName, json.toString(), AppRequest.PacketType.DEFAULT_APP_REQUEST, false);
            // System.out.println("About to send "+i+"th request.");
            long start = System.currentTimeMillis();

            try {
                // coordinate request through GigaPaxos
                client.sendRequest(ReplicableClientRequest.wrap(req),
                        PaxosConfig.getActives().get(node)
                );

            } catch (IOException e) {
                e.printStackTrace();
                // request coordination failed
            }


            long elapsed = System.currentTimeMillis() - start;
            if(interval > elapsed )
                Thread.sleep(interval - elapsed);
            System.out.println(elapsed);

        }

        client.close();

    }
}
