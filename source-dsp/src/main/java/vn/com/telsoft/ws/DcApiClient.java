package vn.com.telsoft.ws;

import com.faplib.lib.SystemConfig;
import com.squareup.okhttp.*;
import org.primefaces.shaded.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

public class DcApiClient {
    public static final Logger logger = LoggerFactory.getLogger(DcApiClient.class);
    private String local;
    private String wsAddress;
    private String wsUser;
    private String wsPass;
    private String authorizationHeaderValue;

    /*static final String wsAddress1;
    static {
        wsAddress1 = SystemConfig.getConfig("dc.ws.address");
    }*/

    OkHttpClient client = new OkHttpClient();

    public DcApiClient() {
        local = SystemConfig.getConfig("dc.ws.local");
        wsAddress = SystemConfig.getConfig("dc.ws.address");
        wsUser = SystemConfig.getConfig("dc.ws.user");
        wsPass = SystemConfig.getConfig("dc.ws.password");
        String usernameAndPassword = wsUser + ":" + wsPass;
        authorizationHeaderValue = "Basic " + Base64.getEncoder().encodeToString(usernameAndPassword.getBytes());
        if (wsAddress == null || "".equals(wsAddress)) {
            local = "1";
        }
        logger.info("local: " + local);
        logger.info("wsAddress: " + wsAddress);
        logger.info("wsUser: " + wsUser);
        logger.info("wsPass: " + wsPass);
        logger.info("authorizationHeaderValue: " + authorizationHeaderValue);
    }

    // get request code here
    String doGetRequest(String url, Map<String, String> params) throws IOException {
        logger.info("url: " + url);
        HttpUrl.Builder httpBuider = HttpUrl.parse(url).newBuilder();
        logger.info("params: ");
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                httpBuider.addQueryParameter(param.getKey(), param.getValue());
                logger.info(param.getKey() + "=" + param.getValue());
            }
        }
        Request request = new Request.Builder()
                .url(httpBuider.build())
                .addHeader("Authorization", authorizationHeaderValue)
                .build();
        Response response = client.newCall(request).execute();
        String strRes = response.body().string();
        logger.info("response: " + strRes);
        return strRes; //strRes.replace("null", "\"\"")
    }

    // post request code here

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    String doPostRequest(String url, String json) throws IOException {
        logger.info("url: " + url);
        RequestBody body = RequestBody.create(JSON, json);
        logger.info("body: " + json);
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", authorizationHeaderValue)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        String strRes = response.body().string();
        logger.info("response: " + strRes);
        return strRes; //strRes.replace("null", "\"\"")
    }

    //kich hoat the
    public DcApiResponse activedc(DcApiRequest request) throws Exception {
        String jsonRequest = "" +
                "{\n" +
                "  \"transaction_id\": \"" + request.getTransactionId() + "\",\n" +
                "  \"order_id\": \"" + request.getOrderId() + "\"\n" +
                "}";
        DcApiResponse dcApiResponse;
        String response;
        if (local != null && "1".equals(local)) {
            response = testActiveDCResponse();
        } else {
            response = doPostRequest(wsAddress + "/activedc", jsonRequest);
        }
        JSONObject obj = new JSONObject(response);
        dcApiResponse = new DcApiResponse();
        dcApiResponse.setCode(obj.getInt("code") + "");
        dcApiResponse.setDescription(obj.isNull("description") ? "" : obj.getString("description"));
        dcApiResponse.setTransactionId(obj.isNull("transaction_id") ? "" : obj.getString("transaction_id"));
        return dcApiResponse;
    }

    String testActiveDCResponse() {
        return "{\n" +
                "  \"code\": 0,\n" +
                "  \"description\": \"Success\",\n" +
                "  \"transaction_id\": \"123456789\"\n" +
                "}";
    }

    public static void main(String[] args) throws Exception {
        try {
            String wsAddress = "http://10.11.10.111:8339/rest/dc/activedc";
            String wsUser = "admin";
            String wsPass = "admin";
            String transaction_id = "100000037029776";
            String order_id = "100000037029776";
            String usernameAndPassword = wsUser + ":" + wsPass;
            String authorizationHeaderValue = "Basic " + Base64.getEncoder().encodeToString(usernameAndPassword.getBytes());
            System.out.println("authorizationHeaderValue: " + authorizationHeaderValue);

            String jsonRequest = "" +
                    "{\n" +
                    "  \"transaction_id\": \"" + transaction_id + "\",\n" +
                    "  \"order_id\": \"" + order_id + "\"\n" +
                    "}";

            RequestBody body = RequestBody.create(JSON, jsonRequest);
            System.out.println("body: " + jsonRequest);
            Request request = new Request.Builder()
                    .url(wsAddress)
                    .header("Authorization", authorizationHeaderValue)
                    .post(body)
                    .build();
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            String strRes = response.body().string();
            strRes = strRes.replace("null", "\"\"");
            System.out.println("response: " + strRes);

            /*DcApiClient service = new DcApiClient();
            String responseJSON = service.testActiveDCResponse();
            JSONObject obj = new JSONObject(responseJSON);
            String code = obj.getInt("code") + "";
            String description = obj.getString("description");
            System.out.println("code: " + code);
            System.out.println("description: " + description);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
