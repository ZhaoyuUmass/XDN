package edu.umass.cs.xdn.docker;

import edu.umass.cs.xdn.interfaces.XDNContainer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DockerContainer implements XDNContainer {

    final String name;

    /**
     * Docker hub url
     */
    final String imageUrl;

    /**
     * The port number exposed to the public network
     */
    final int port;

    /**
     *
     */
    final int exposePort;

    /**
     * This is long unique id generated by docker.
     */
    String id;

    /**
     *
     */
    String addr;

    /**
     *
     */
    final private List<String> serviceNames;

    JSONArray env;

    /**
     * Indicate whether the docker uses a volume
     * Volume is the docker volume attached to the running instance
     * We let volume name be same as name, which is unique across all apps.
     */
    final private String volume;

    /**
     *
     * @param name
     * @param imageUrl
     * @param port
     * @param exposePort
     * @param env
     * @param volume
     */
    public DockerContainer(String name, String imageUrl, int port, int exposePort, JSONArray env, String volume) {
        this.name = name;
        this.volume = volume;
        this.imageUrl = imageUrl;
        this.port = port;
        this.exposePort = exposePort;
        this.env = env;
        this.serviceNames = new ArrayList<>();
    }


    public DockerContainer(JSONObject json) throws JSONException {
        this.name = json.getString(DockerKeys.NAME.toString());
        this.volume = json.getString(DockerKeys.VOL.toString());
        this.env = json.getJSONArray(DockerKeys.ENV.toString());
        this.imageUrl = json.getString(DockerKeys.IMAGE_URL.toString());
        this.port = json.getInt(DockerKeys.PORT.toString());
        this.exposePort = json.getInt(DockerKeys.PUBLIC_EXPOSE_PORT.toString());
        JSONArray users = json.getJSONArray(DockerKeys.SERVICE_NAMES.toString());
        this.serviceNames = new ArrayList<>();
        if (users != null) {
            for (int i=0; i<users.length(); i++){
                serviceNames.add(users.getString(i));
            }
        }
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject json = new JSONObject();

        json.put(DockerKeys.NAME.toString(), name);
        json.put(DockerKeys.ENV.toString(), env);
        json.put(DockerKeys.IMAGE_URL.toString(), imageUrl);
        json.put(DockerKeys.PORT.toString(), port);

        JSONArray snJSON = new JSONArray(serviceNames);
        json.put(DockerKeys.SERVICE_NAMES.toString(), snJSON);

        return json;
    }


    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getAddr() {
        return addr;
    }

    public int getPort() {
        return port;
    }

    public int getExposePort(){
        return exposePort;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getID() {
        return id;
    }

    @Override
    public List<String> getStartCommand(String name) {
        return null;
    }

    @Override
    public List<String> getCheckpointCommand(String name) {
        return null;
    }

    @Override
    public List<String> getRestoreCommand(String name) {
        return null;
    }

    @Override
    public List<String> getStopCommand(String name) {
        return null;
    }

    @Override
    public List<String> getPullCommand(String name, boolean exists) {
        return null;
    }

    public void addServiceName(String name) {
        serviceNames.add(name);
    }

    public boolean removeServiceName(String name){
        return serviceNames.remove(name);
    }

    public boolean isEmpty() {
        return serviceNames.isEmpty();
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return imageUrl;
    }

    public String getVolume() {
        return volume;
    }

    public JSONArray getEnv() {
        return env;
    }

    public static JSONObject dockerToJsonState(DockerContainer container) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(DockerKeys.NAME.toString(), container.name);
        json.put(DockerKeys.IMAGE_URL.toString(), container.imageUrl);
        // json.put(DockerKeys.SERVICE_NAMES.toString(), container.serviceNames);
        json.put(DockerKeys.VOL.toString(), container.volume);
        json.put(DockerKeys.PORT.toString(), container.port);
        json.put(DockerKeys.PUBLIC_EXPOSE_PORT.toString(), container.exposePort);
        json.put(DockerKeys.ENV.toString(), container.env);

        return json;
    }

    public static DockerContainer stateToDockerContainer(JSONObject json) throws JSONException {
        String appName = json.getString(DockerKeys.NAME.toString());
        int port = json.has(DockerKeys.PORT.toString()) ? json.getInt(DockerKeys.PORT.toString()) : -1;
        String url = json.has(DockerKeys.IMAGE_URL.toString()) ? json.getString(DockerKeys.IMAGE_URL.toString()) : null;
        JSONArray jEnv = json.has(DockerKeys.ENV.toString()) ? json.getJSONArray(DockerKeys.ENV.toString()) : null;
        String vol = json.has(DockerKeys.VOL.toString()) ? json.getString(DockerKeys.VOL.toString()) : null;
        int exposePort = json.has(DockerKeys.PUBLIC_EXPOSE_PORT.toString()) ? json.getInt(DockerKeys.PUBLIC_EXPOSE_PORT.toString()) : 80;

        return new DockerContainer(appName, url, port, exposePort, jEnv, vol);
    }

    public static void main(String[] args) {

    }

}
