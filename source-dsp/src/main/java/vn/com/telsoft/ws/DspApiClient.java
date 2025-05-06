package vn.com.telsoft.ws;

import com.faplib.lib.SystemConfig;
import com.squareup.okhttp.*;
import org.primefaces.shaded.json.JSONArray;
import org.primefaces.shaded.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.com.telsoft.entity.DSPSubService;
import vn.com.telsoft.entity.DspDipSubServiceInfo;
import vn.com.telsoft.ws.domain.CheckIsdnRequest;
import vn.com.telsoft.ws.domain.CheckIsdnResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class DspApiClient {
    public static final Logger logger = LoggerFactory.getLogger(DspApiClient.class);
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    private String local;
    private String wsAddress;
    private String wsUser;

    /*static final String wsAddress1;
    static {
        wsAddress1 = SystemConfig.getConfig("dsp.ws.address");
    }*/
    private String wsPass;
    private String authorizationHeaderValue;

    public DspApiClient() {
        local = SystemConfig.getConfig("dsp.ws.local");
        wsAddress = SystemConfig.getConfig("dsp.ws.address");
        wsUser = SystemConfig.getConfig("dsp.ws.user");
        wsPass = SystemConfig.getConfig("dsp.ws.password");
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

    // post request code here

    public static void main(String[] args) throws Exception {
        try {
            String wsAddress = "http://10.11.10.111:5555/rest/cancelservice";
            String wsUser = "webapi";
            String wsPass = "webapi@123";
            String isdn = "977770240";
            String serviceType = "DC";
            String reason = "";
            String usernameAndPassword = wsUser + ":" + wsPass;
            String authorizationHeaderValue = "Basic " + Base64.getEncoder().encodeToString(usernameAndPassword.getBytes());
//            String authorizationHeaderValue = "Basic " + wsUser + " " + wsPass;
            System.out.println("authorizationHeaderValue: " + authorizationHeaderValue);

            String jsonRequest = "" +
                    "{\n" +
                    "  \"isdn\": \"" + isdn + "\",\n" +
                    "  \"serviceType\": \"" + serviceType + "\",\n" +
                    "  \"reason\": \"" + reason + "\"\n" +
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

 /*           DspApiClient service = new DspApiClient();
            String responseJSON = service.testGetServiceInfo();
            JSONObject obj = new JSONObject(responseJSON);
            String code = obj.getInt("code") + "";
            String description = obj.getString("description");
            String transId = obj.getString("transId");
            List<DSPSubService> lstSubServices = new ArrayList<>();
            DSPSubService subService = null;
            JSONArray listServices = obj.getJSONArray("listServices");
            if (listServices != null) {
                Long dataAmt;
                Date expDate;
                String serviceName;
                Date startDate;
                SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                for (int i = 0; i < listServices.length(); i++) {
                    JSONObject serviceJson = listServices.getJSONObject(i);
                    dataAmt = serviceJson.getLong("dataAmt");
                    expDate = sd.parse(serviceJson.getString("expDate"));
                    serviceName = serviceJson.getString("serviceName");
                    startDate = sd.parse(serviceJson.getString("startDate"));
                    subService = new DSPSubService();
                    subService.setDataAmount(dataAmt);
                    subService.setExpTime(expDate);
                    subService.setService(serviceName);
                    subService.setStartTime(startDate);
                    lstSubServices.add(subService);
                }
//                 "dataAmt": "2097152",
//      "expDate": "01/11/2020 00:00:00",
//      "serviceName": "DSP1",
//      "startDate": "01/01/2018 00:00:00"
            }
            System.out.println("code: " + code);
            System.out.println("description: " + description);
            System.out.println("transId: " + transId);
            System.out.println("listServices: " + lstSubServices);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    //tra cuu the Data
//    public DspApiResponse viewdc(DspApiRequest request) throws Exception {
//        String jsonRequest = "" +
//                "{\n" +
//                "  \"serial\": \"" + request.getSerial() + "\"\n" +
//                "}";
//        DspApiResponse dspApiResponse;
//        String response;
//        if (local != null && "1".equals(local)) {
//            response = testViewDCResponse();
//        } else {
//            response = doPostRequest(wsAddress + "/viewdc", jsonRequest);
//        }
//        JSONObject obj = new JSONObject(response);
//        dspApiResponse = new DspApiResponse();
//        dspApiResponse.setResultCode(obj.getInt("code") + "");
//        dspApiResponse.setComment(obj.isNull("description") ? "" : obj.getString("description"));
//        return dspApiResponse;
//    }

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

    public DcApiResponse viewdc(DspApiRequest request) throws Exception {
        String jsonRequest = "" +
                "{\n" +
                "  \"serial\": \"" + request.getSerial() + "\"\n" +
                "}";
        DcApiResponse dcApiResponse;
        String response;
        if (local != null && "1".equals(local)) {
            response = testViewDCResponse();
        } else {
            response = doPostRequest(wsAddress + "/viewdc", jsonRequest);
        }
        JSONObject obj = new JSONObject(response);
        dcApiResponse = new DcApiResponse();
        dcApiResponse.setTransactionId(obj.isNull("transId") ? "" : obj.getString("transId"));
        dcApiResponse.setCode(obj.getInt("code") + "");
        dcApiResponse.setDescription(obj.isNull("description") ? "" : obj.getString("description"));
        dcApiResponse.setStatus(obj.isNull("status") ? "" : obj.getInt("status") + "");
        dcApiResponse.setDataAmount(obj.isNull("data_amount") ? "" : obj.getInt("data_amount") + "");
        dcApiResponse.setActiveDays(obj.isNull("active_days") ? "" : obj.getInt("active_days") + "");
        dcApiResponse.setExpDate(obj.isNull("exp_date") ? "" : obj.getString("exp_date"));
        dcApiResponse.setCreDate(obj.isNull("cre_date") ? "" : obj.getString("cre_date"));
        dcApiResponse.setActDate(obj.isNull("act_date") ? "" : obj.getString("act_date"));
        dcApiResponse.setSuspDate(obj.isNull("susp_date") ? "" : obj.getString("susp_date"));
        dcApiResponse.setUseDate(obj.isNull("use_date") ? "" : obj.getString("use_date"));
        dcApiResponse.setRef(obj.isNull("ref") ? "" : obj.getString("ref"));
        dcApiResponse.setAddon(obj.isNull("addon") ? "" : obj.getInt("addon") + "");
        dcApiResponse.setReseller(obj.isNull("reseller") ? "" : obj.getString("reseller"));
        dcApiResponse.setOrderCode(obj.isNull("order_code") ? "" : obj.getString("order_code"));
        return dcApiResponse;
    }

    //nap the Data
    public DspApiResponse usedc(DspApiRequest request) throws Exception {
        String jsonRequest = "" +
                "{\n" +
                "  \"isdn\": \"" + request.getMsisdn() + "\",\n" +
                "  \"pin\": \"" + request.getDatacode() + "\",\n" +
                "  \"serial\": \"" + request.getSerial() + "\",\n" +
                "  \"ref\": \"" + request.getComment() + "\",\n" +
                "  \"webUsername\": \"" + request.getWebUsername() + "\"\n" +
                "}";
        DspApiResponse dspApiResponse;
        String response;
        if (local != null && "1".equals(local)) {
            response = testUseDCResponse();
        } else {
            response = doPostRequest(wsAddress + "/wusedc", jsonRequest);
        }
        JSONObject obj = new JSONObject(response);
        dspApiResponse = new DspApiResponse();
        dspApiResponse.setResultCode(obj.getInt("code") + "");
        dspApiResponse.setComment(obj.isNull("description") ? "" : obj.getString("description"));
        return dspApiResponse;
    }

    //kich hoat the
    public DcApiResponse activedc(DcApiRequest request) throws Exception {
        String jsonRequest = "" +
                "{\n" +
                "  \"transId\": \"" + request.getTransactionId() + "\",\n" +
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
        dcApiResponse.setTransactionId(obj.isNull("transId") ? "" : obj.getString("transId"));
        dcApiResponse.setJobId(obj.isNull("job_id") ? "" : obj.getString("job_id"));
        return dcApiResponse;
    }

    //gia han lo the
    public DcApiResponse extenddc(DcApiRequest request) throws Exception {
        String jsonRequest = "" +
                "{\n" +
                "  \"transId\": \"" + request.getTransactionId() + "\",\n" +
                "  \"order_id\": \"" + request.getOrderId() + "\",\n" +
                "  \"extend_days\": " + request.getExtendDays() + "\n" +
                "}";
        DcApiResponse dcApiResponse;
        String response;
        if (local != null && "1".equals(local)) {
            response = testActiveDCResponse();
        } else {
            response = doPostRequest(wsAddress + "/extenddc", jsonRequest);
        }
        JSONObject obj = new JSONObject(response);
        dcApiResponse = new DcApiResponse();
        dcApiResponse.setCode(obj.getInt("code") + "");
        dcApiResponse.setDescription(obj.isNull("description") ? "" : obj.getString("description"));
        dcApiResponse.setTransactionId(obj.isNull("transId") ? "" : obj.getString("transId"));
        dcApiResponse.setJobId(obj.isNull("job_id") ? "" : obj.getString("job_id"));
        return dcApiResponse;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Service Info
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public DspApiSerivceInfoResponse getServiceInfo(DspApiRequest request) throws Exception {
        DspApiSerivceInfoResponse response = new DspApiSerivceInfoResponse();
        JSONArray requestSub = new JSONArray(request.getServices());
        List<DSPSubService> lstSubServices = new ArrayList<>();
        DSPSubService subService = null;
        String jsonRequest = "" +
                "{\n" +
                "  \"isdn\": \"" + request.getMsisdn() + "\",\n" +
                "  \"services\": " + requestSub.toString() + "\n" +
                "}";
        String responseJSON;
        if (local != null && "1".equals(local)) {
            responseJSON = testGetServiceInfo();
        } else {
            responseJSON = doPostRequest(wsAddress + "/serviceinfo", jsonRequest);
        }
        JSONObject obj = new JSONObject(responseJSON);
        response.setCode(obj.getInt("code") + "");
        response.setDescription(obj.isNull("description") ? "" : obj.getString("description"));
        if ("0".equals(response.getCode())) {
            JSONArray listServices = obj.getJSONArray("listServices");
            SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            if (listServices != null) {
                for (int i = 0; i < listServices.length(); i++) {
                    JSONObject serviceJson = listServices.getJSONObject(i);
                    subService = new DSPSubService();
                    subService.setInitialAmount(serviceJson.getLong("dataAmt"));
                    subService.setEndTime(sd.parse(serviceJson.getString("expDate")));
                    subService.setService(serviceJson.getString("serviceName"));
                    subService.setStartTime(sd.parse(serviceJson.getString("startDate")));
                    subService.setIsdn(request.getMsisdn());
                    lstSubServices.add(subService);
                }
            }
            response.setListServices(lstSubServices);
        }
        return response;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Hủy dịch vụ đăng ký
    public DspApiSerivceInfoResponse checkCancelService(DspApiCancelServiceRequest request, String url) throws Exception {
        String jsonRequest = "" +
                "{\n" +
                "  \"isdn\": \"" + request.getIsdn() + "\",\n" +
                "  \"serviceType\": \"" + request.getServiceType() + "\",\n" +
                "  \"reason\": \"" + request.getReason() + "\"\n" +
                "}";
        DspApiSerivceInfoResponse dspApiSerivceInfoResponse;
        String response;
        if ("1".equals(local)) {
            response = testCheckCancelService();
        } else {
            response = doPostRequest(wsAddress + url, jsonRequest);
        }
        JSONObject obj = new JSONObject(response);
        dspApiSerivceInfoResponse = new DspApiSerivceInfoResponse();
        dspApiSerivceInfoResponse.setCode(obj.getInt("code") + "");
        dspApiSerivceInfoResponse.setDescription(obj.isNull("description") ? "" : obj.getString("description"));
        return dspApiSerivceInfoResponse;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Check thue bao vas
    public CheckIsdnResponse checkVasMobile(CheckIsdnRequest request) throws Exception {
        String jsonRequest = "" +
                "{\n" +
                "  \"isdn\": \"" + request.getIsdn() + "\"\n" +
                "}";
        CheckIsdnResponse checkIsdnResponse;
        String response;
        if (local != null && "1".equals(local)) {
            response = testCheckCancelService();
        } else {
            response = doPostRequest(wsAddress + "/checkVasIsdn", jsonRequest);
        }
        JSONObject obj = new JSONObject(response);
        checkIsdnResponse = new CheckIsdnResponse();
        checkIsdnResponse.setCode(obj.getInt("code") + "");
        checkIsdnResponse.setDescription(obj.isNull("description") ? "" : obj.getString("description"));
        checkIsdnResponse.setTransId(obj.isNull("transId") ? "" : obj.getString("transId"));
        checkIsdnResponse.setIsdn(obj.isNull("isdn") ? "" : obj.getString("isdn"));
        checkIsdnResponse.setVasIsdn(obj.isNull("vasIsdn") ? null : obj.getBoolean("vasIsdn"));
        return checkIsdnResponse;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //nap the Data C90N
    public DspApiResponse wusedc_khcn(DspApiRequest request) throws Exception {
        String jsonRequest = "" +
                "{\n" +
                "  \"isdn\": \"" + request.getMsisdn() + "\",\n" +
                "  \"pin\": \"" + request.getDatacode() + "\",\n" +
                "  \"serial\": \"" + request.getSerial() + "\",\n" +
                "  \"ref\": \"" + request.getComment() + "\",\n" +
                "  \"webUsername\": \"" + request.getWebUsername() + "\"\n" +
                "}";
        DspApiResponse dspApiResponse;
        String response;
        if (local != null && "1".equals(local)) {
            response = testUseDCResponse();
        } else {
            response = doPostRequest(wsAddress + "/wusedc_khcn", jsonRequest);
        }
        JSONObject obj = new JSONObject(response);
        dspApiResponse = new DspApiResponse();
        dspApiResponse.setResultCode(obj.getInt("code") + "");
        dspApiResponse.setComment(obj.isNull("description") ? "" : obj.getString("description"));
        return dspApiResponse;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Check thue bao co du dki dki goi cuoc
    public CheckIsdnResponse checkIsdn(CheckIsdnRequest request) throws Exception {
        String jsonRequest = "" +
                "{\n" +
                "  \"isdn\": \"" + request.getIsdn() + "\"\n" +
                "}";
        CheckIsdnResponse checkIsdnResponse;
        String response;
        if (local != null && "1".equals(local)) {
            response = testCheckCancelService();
        } else {
            response = doPostRequest(wsAddress + "/cancelservice", jsonRequest);
        }
        JSONObject obj = new JSONObject(response);
        checkIsdnResponse = new CheckIsdnResponse();
        checkIsdnResponse.setCode(obj.getInt("code") + "");
        checkIsdnResponse.setDescription(obj.isNull("description") ? "" : obj.getString("description"));
        return checkIsdnResponse;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //nap the ClipTV
    public DspApiResponse registerClipTV(DspApiRequest request) throws Exception {
        String jsonRequest = "" +
                "{\n" +
                "  \"isdn\": \"" + request.getMsisdn() + "\",\n" +
                "  \"pin\": \"" + request.getDatacode() + "\",\n" +
                "  \"serial\": \"" + request.getSerial() + "\",\n" +
                "  \"ref\": \"" + request.getComment() + "\",\n" +
                "  \"transaction_id\": \"" + request.getTransactionId() + "\"\n" +
                "}";
        DspApiResponse dspApiResponse;
        String response;
        if (local != null && "1".equals(local)) {
            response = testUseDCResponse();
        } else {
            response = doPostRequest(wsAddress + "/register-cliptv", jsonRequest);
        }
        JSONObject obj = new JSONObject(response);
        dspApiResponse = new DspApiResponse();
        dspApiResponse.setResultCode(obj.getInt("code") + "");
        dspApiResponse.setComment(obj.isNull("description") ? "" : obj.getString("description"));
        return dspApiResponse;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    String testViewDCResponse() {
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

    String testUseDCResponse() {
        return "{\n" +
                "  \"code\": 0,\n" +
                "  \"description\": \"Success\",\n" +
                "  \"transId\": \"admin_1599721632302\"\n" +
                "}";
    }

    String testActiveDCResponse() {
        return "{\n" +
                "  \"code\": 0,\n" +
                "  \"description\": \"Success\",\n" +
                "  \"transId\": \"123456789\",\n" +
                "  \"job_id\": \"123123\"\n" +
                "}";
    }

    String testGetServiceInfo() {
        return "{\n" +
                "  \"code\": 0,\n" +
                "  \"description\": \"Success\",\n" +
                "  \"transId\": \"trang123_1614673727499\",\n" +
                "  \"listServices\": [\n" +
                "    {\n" +
                "      \"dataAmt\": \"2097152\",\n" +
                "      \"expDate\": \"01/11/2021 00:00:22\",\n" +
                "      \"serviceName\": \"DSP1\",\n" +
                "      \"startDate\": \"01/01/2018 00:00:00\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"dataAmt\": \"2097160\",\n" +
                "      \"expDate\": \"01/11/2021 00:00:00\",\n" +
                "      \"serviceName\": \"DSP3\",\n" +
                "      \"startDate\": \"01/01/2019 00:00:00\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }

    String testCheckCancelService() {
        return "{\n" +
                "  \"code\": 0,\n" +
                "  \"description\": \"Success\",\n" +
                "}";
    }


    //HuyNQ 11/05/2021
    //Cộng bù data
    public DcApiResponse nonVoucherRecharge(DspApiRechargeRequest request) throws Exception {
        String jsonRequest = "" +
                "{\n" +
                "  \"transaction_id\": \"" + request.getTransactionId() + "\",\n" +
                "  \"isdn\": \"" + request.getIsdn() + "\",\n" +
                "  \"serial\": \"" + request.getSerial() + "\",\n" +
                "  \"ref\": \"" + request.getRef() + "\",\n" +
                "  \"amount\": " + request.getAmount() + ",\n" +
                "  \"days\": " + request.getDays() + ",\n" +
                "  \"addon\": \"" + request.getAddon() + "\",\n" +
//                "  \"webUsername\": \"" + request.getWebUsername() + "\",\n" +
                "  \"profile_code\": \"" + request.getProfileCode() + "\"\n" +
                "}";
        DcApiResponse dcApiResponse;
        String response;
        if (local != null && "1".equals(local)) {
            response = testNonVoucherRechargeResponse();
        } else {
            response = doPostRequest(wsAddress + "/nonvoucherRecharge", jsonRequest);
        }
        JSONObject obj = new JSONObject(response);
        dcApiResponse = new DcApiResponse();
        dcApiResponse.setCode(obj.getInt("code") + "");
        dcApiResponse.setDescription(obj.isNull("description") ? "" : obj.getString("description"));
        dcApiResponse.setTransactionId(obj.isNull("transaction_id") ? "" : obj.getString("transaction_id"));
        return dcApiResponse;
    }

    public DspDipSubApiResponse getDspDipSubServiceInfo(DspApiRequest request) throws Exception {
        DspDipSubApiResponse response = new DspDipSubApiResponse();
        List<DspDipSubServiceInfo> lstSubServices = new ArrayList<>();
        DspDipSubServiceInfo subService = null;
        String jsonRequest = "" +
                "{\n" +
                "  \"isdn\": \"" + request.getMsisdn() + "\"\n" +
                "}";
        String responseJSON;
        if (local != null && "1".equals(local)) {
            responseJSON = testGetDspDipSubServiceInfo();
        } else {
            responseJSON = doPostRequest(wsAddress + "/view-data-ip", jsonRequest);
        }

        JSONObject obj = new JSONObject(responseJSON);
        response.setCode(obj.getInt("code") + "");
        response.setDescription(obj.isNull("description") ? "" : obj.getString("description"));
        if ("0".equals(response.getCode())) {
            JSONArray listServices = obj.getJSONArray("listServices");
            SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            if (listServices != null) {
                for (int i = 0; i < listServices.length(); i++) {
                    JSONObject serviceJson = listServices.getJSONObject(i);
                    subService = new DspDipSubServiceInfo();
                    subService.setServiceName(serviceJson.isNull("serviceName") ? "" : serviceJson.getString("serviceName"));
                    subService.setDataAmt(serviceJson.isNull("dataAmt") ? null : serviceJson.getLong("dataAmt"));
                    subService.setChannel(serviceJson.isNull("channel") ? "" : serviceJson.getString("channel"));
                    subService.setUserId(serviceJson.isNull("userId") ? null : serviceJson.getLong("userId"));
                    if (serviceJson.isNull("startDate")) {
                        subService.setStartDate(null);
                    } else {
                        subService.setStartDate(sd.parse(serviceJson.getString("startDate")));
                    }
                    if (serviceJson.isNull("expDate")) {
                        subService.setExpDate(null);
                    } else {
                        subService.setExpDate(sd.parse(serviceJson.getString("expDate")));
                    }
//                    subService.setIsdn(request.getMsisdn());
                    lstSubServices.add(subService);
                }
            }
            response.setListServices(lstSubServices);
        }
        return response;
    }

    String testGetDspDipSubServiceInfo() {
        return "{\n" +
                "  \"code\": 0,\n" +
                "  \"description\": \"Success\",\n" +
                "  \"transId\": \"trang123_1614673727499\",\n" +
                "  \"listServices\": [\n" +
                "    {\n" +
                "      \"totalAmount\": \"2097152\",\n" +
                "      \"expDate\": \"01/11/2021 00:00:22\",\n" +
                "      \"serviceName\": \"SEV2\",\n" +
                "      \"startDate\": \"01/01/2023 00:00:00\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"totalAmount\": \"2097160\",\n" +
                "      \"expDate\": \"01/11/2021 00:00:00\",\n" +
                "      \"serviceName\": \"SEV2\",\n" +
                "      \"startDate\": \"01/01/2024 00:00:00\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }

    String testNonVoucherRechargeResponse() {
        return "{\n" +
                "  \"code\": 0,\n" +
                "  \"description\": \"Success\",\n" +
                "  \"transaction_id\": \"admin_1599721632302\"\n" +
                "}";
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}






