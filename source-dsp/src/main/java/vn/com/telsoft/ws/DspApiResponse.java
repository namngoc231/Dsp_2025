package vn.com.telsoft.ws;

/**
 * @author TUNGLM
 */
public class DspApiResponse {

    private String resultCode;
    private String comment;

    public DspApiResponse() {
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
