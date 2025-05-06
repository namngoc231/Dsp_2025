package vn.com.telsoft.ws;

import com.faplib.lib.SystemConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.primefaces.shaded.json.JSONObject;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DspApiJerseyClient {
    private static String local;
    private static String wsAddress;
    private static String wsUser;
    private static String wsPass;

    static {
        local = SystemConfig.getConfig("dsp.ws.local");
        wsAddress = SystemConfig.getConfig("dsp.ws.address");
        wsUser = SystemConfig.getConfig("dsp.ws.user");
        wsPass = SystemConfig.getConfig("dsp.ws.password");
    }

    private static Client createJerseyRestClient() {
        // (1) Create client config
        ClientConfig clientConfig = new ClientConfig();

        // Config logging for client side
        clientConfig.register( //
                new LoggingFeature( //
                        Logger.getLogger(LoggingFeature.DEFAULT_LOGGER_NAME), //
                        Level.INFO, //
                        LoggingFeature.Verbosity.PAYLOAD_ANY, //
                        10000));

        // (2) Create basic authentication
        HttpAuthenticationFeature authDetails = HttpAuthenticationFeature.basic(wsUser, wsPass);

        // (3) Create jersey client with authentication
        Client client = ClientBuilder.newClient(clientConfig);
        client.register(authDetails);
        return ClientBuilder.newClient(clientConfig);
    }

    private static String convertToJson(DspApiRequest request) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //tra cuu the Data
    private static DspApiResponse viewdc(DspApiRequest request) throws Exception {
        String jsonRequest = convertToJson(request);
        if (local != null && "1".equals(local)) {
            String response = testViewDCResponse();
            JSONObject obj = new JSONObject(response);
            DspApiResponse dspApiResponse = new DspApiResponse();
            dspApiResponse.setResultCode(obj.getInt("code") + "");
            dspApiResponse.setComment(obj.getString("description"));
            return dspApiResponse;
        } else {
            Client client = createJerseyRestClient();
            WebTarget target = client.target(wsAddress + "/viewdc");
            Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.entity(jsonRequest, MediaType.APPLICATION_JSON));
            return response.readEntity(DspApiResponse.class);
        }
    }

    //nap the Data
    private static DspApiResponse usedc(DspApiRequest request) throws Exception {
        String jsonRequest = convertToJson(request);
        if (local != null && "1".equals(local)) {
            String response = testUseDCResponse();
            JSONObject obj = new JSONObject(response);
            DspApiResponse dspApiResponse = new DspApiResponse();
            dspApiResponse.setResultCode(obj.getInt("code") + "");
            dspApiResponse.setComment(obj.getString("description"));
            return dspApiResponse;
        } else {
            Client client = createJerseyRestClient();
            WebTarget target = client.target(wsAddress + "/usedc");
            Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.entity(jsonRequest, MediaType.APPLICATION_JSON));
            return response.readEntity(DspApiResponse.class);
        }
    }

    //kich hoat the DATA
    private static DspApiResponse activedc(DspApiRequest request) throws Exception {
        String jsonRequest = convertToJson(request);
        if (local != null && "1".equals(local)) {
            String response = testActiveDCResponse();
            JSONObject obj = new JSONObject(response);
            DspApiResponse dspApiResponse = new DspApiResponse();
            dspApiResponse.setResultCode(obj.getInt("code") + "");
            dspApiResponse.setComment(obj.getString("description"));
            return dspApiResponse;
        } else {
            Client client = createJerseyRestClient();
            WebTarget target = client.target(wsAddress + "/activedc");
            Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.entity(jsonRequest, MediaType.APPLICATION_JSON));
            return response.readEntity(DspApiResponse.class);
        }
    }

    //gia han lo the DATA
    private static DspApiResponse extenddc(DspApiRequest request) throws Exception {
        String jsonRequest = convertToJson(request);
        if (local != null && "1".equals(local)) {
            String response = testExtendDCResponse();
            JSONObject obj = new JSONObject(response);
            DspApiResponse dspApiResponse = new DspApiResponse();
            dspApiResponse.setResultCode(obj.getInt("code") + "");
            dspApiResponse.setComment(obj.getString("description"));
            return dspApiResponse;
        } else {
            Client client = createJerseyRestClient();
            WebTarget target = client.target(wsAddress + "/extenddc");
            Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.entity(jsonRequest, MediaType.APPLICATION_JSON));
            return response.readEntity(DspApiResponse.class);
        }
    }

    static String testViewDCResponse() {
        return "{\n" +
                "  \"code\": 0,\n" +
                "  \"transId\": \"admin_1599721632302\",\n" +
                "  \"act_date\": \"2020-09-04T07:59:24Z[UTC]\",\n" +
                "  \"active_days\": 90,\n" +
                "  \"cre_date\": \"2020-09-03T17:00:00Z[UTC]\",\n" +
                "  \"data_amount\": 5242880,\n" +
                "  \"exp_date\": \"2020-09-13T17:00:00Z[UTC]\",\n" +
                "  \"susp_date\": \"2020-09-13T16:00:00Z[UTC]\",\n" +
                "  \"use_date\": \"2020-09-13T15:00:00Z[UTC]\",\n" +
                "  \"ref\": \"admin_1599721632302\",\n" +
                "  \"addon\": 1,\n" +
                "  \"reseller\": \"admin_1599721632302\",\n" +
                "  \"description\": \"Success\",\n" +
                "  \"status\": 8\n" +
                "}";
    }

    static String testUseDCResponse() {
        return "{\n" +
                "  \"code\": 0,\n" +
                "  \"description\": \"Success\",\n" +
                "  \"transId\": \"admin_1599721632302\"\n" +
                "}";
    }

    static String testActiveDCResponse() {
        return "{\n" +
                "  \"code\": 0,\n" +
                "  \"description\": \"Success\",\n" +
                "  \"transId\": \"123456789\",\n" +
                "  \"job_id\": \"123123\"\n" +
                "}";
    }

    static String testExtendDCResponse() {
        return "{\n" +
                "  \"code\": 0,\n" +
                "  \"description\": \"Success\",\n" +
                "  \"transId\": \"123456789\",\n" +
                "  \"job_id\": \"123123\"\n" +
                "}";
    }

    public static void main(String[] args) {
        try {
            DspApiJerseyClient.viewdc(new DspApiRequest());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
