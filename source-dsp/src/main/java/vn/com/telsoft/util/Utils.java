package vn.com.telsoft.util;

import vn.com.telsoft.ws.DspApiClient;
import vn.com.telsoft.ws.domain.CheckIsdnRequest;
import vn.com.telsoft.ws.domain.CheckIsdnResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author TungLM
 */
public class Utils {

    public static String fixIsdn(String input) {
        return input == null ? null : "84" + input.trim().replaceAll("^0|^84(?!84)(?=\\d{8,})|^84(?=84)(?=\\d{8,})", "");
    }

    public static String fixIsdnWithout0and84(String input) {
        return input == null ? null : input.trim().replaceAll("^0|^84(?!84)(?=\\d{8,})|^84(?=84)(?=\\d{8,})", "");
    }

    public static boolean validateIsdn(String in_isdn) {
//        Pattern sChar = Pattern.compile("^((3[2-9]\\d{7})|(5[6|8|9]\\d{7})|(7[0|6-9]\\d{7})|(8[1-6|8|9]\\d{7})|(9[0-4|6-9]\\d{7}))$");
        Pattern sChar = Pattern.compile("\\d+");
        if (in_isdn == null || "".equals(in_isdn)) {
            return false;
        } else {
            Matcher msChar = sChar.matcher(in_isdn);
            return msChar.matches() && in_isdn.length() <= 14 && in_isdn.length() >= 9;
        }
    }

    public static boolean validateIsdnSub(String in_isdn) {
//        Pattern sChar = Pattern.compile("^((3[2-9]\\d{7})|(5[6|8|9]\\d{7})|(7[0|6-9]\\d{7})|(8[1-6|8|9]\\d{7})|(9[0-4|6-9]\\d{7}))$");
        Pattern sChar = Pattern.compile("\\d+");
        if (in_isdn == null || "".equals(in_isdn)) {
            return false;
        } else {
            Matcher msChar = sChar.matcher(in_isdn);
            return msChar.matches() && in_isdn.length() <= 11 && in_isdn.length() >= 9;
        }
    }

    public static String formatISDN(String isdn) {
        if (isdn.startsWith("84") && isdn.length() == 11) {
            return isdn.substring(2);
        } else if (isdn.startsWith("0") && isdn.length() == 10) {
            return isdn.substring(1);
        } else
            return isdn;
    }

    public static boolean validateSerialDataCode(String in_serial) {
        if (in_serial == null || (in_serial.length() != 15 && in_serial.length() != 12 && in_serial.length() != 16)) {
            return false;
        }
        Pattern sChar = Pattern.compile("\\d+");
        Matcher msChar = sChar.matcher(in_serial);
        return msChar.matches();
    }

    public static boolean validateDataCode(String in_datacode) {
        if (in_datacode == null || "".equals(in_datacode) || in_datacode.length() != 15) {
            return false;
        }
        Pattern sChar = Pattern.compile("^[V|A|N]\\d+");
        Matcher msChar = sChar.matcher(in_datacode.toUpperCase());
        return msChar.matches();
    }

    public static boolean validateDataCode(String in_datacode, String regex) {
        return in_datacode == null || !in_datacode.matches(regex);
    }

    public static boolean checkVasMobile(String isdn) throws Exception {
        if (isdn == null || "".equals(isdn)) {
            return false;
        }
        CheckIsdnRequest request = new CheckIsdnRequest();
        request.setIsdn(isdn);
        DspApiClient dspApiClient = new DspApiClient();
        CheckIsdnResponse response = dspApiClient.checkVasMobile(request);
        if (response != null && "0".equals(response.getCode())) {
            return response.getVasIsdn();
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        String in_test = "N47939713953827";
        String in_serial = "23002300230023421";
        String in_isdn = "792240323";
//        System.out.println("validateDataCode: " + Utils.validateDataCode(in_test));
        System.out.println("validateSerial: " + Utils.validateSerialDataCode(in_serial));
//        System.out.println("validateIsdn: " + Utils.validateIsdn(in_isdn));
    }

}
