package test;

import edu.umass.cs.gigapaxos.interfaces.Request;
import edu.umass.cs.gigapaxos.interfaces.RequestCallback;
import edu.umass.cs.xdn.XDNConfig;
import edu.umass.cs.xdn.deprecated.XDNAgentClient;

import java.io.IOException;

public class DeleteServices {
    static int received = 0;

    public static void main(String[] args) throws IOException, InterruptedException {

        XDNAgentClient client = new XDNAgentClient();
        String testServiceName = XDNConfig.generateServiceName("xdn-demo-app", "Alvin");

        final int sent = 1;

        client.sendRequest(new edu.umass.cs.reconfiguration.reconfigurationpackets.DeleteServiceName(testServiceName),
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

        System.out.println("Service name deleted successfully.");

        client.close();
        // System.exit(0);
    }
}
