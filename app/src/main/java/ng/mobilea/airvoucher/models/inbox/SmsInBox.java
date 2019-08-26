package ng.mobilea.airvoucher.models.inbox;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 12/11/2017.
 */

public class SmsInBox {
    String smsId;
    String from;
    String type;
    String timeSent;
    String timeReceived;
    String serviceCenter;
    String message;

    public SmsInBox() {

    }

    public SmsInBox(String smsId, String from, String type, String timeSent, String timeReceived, String serviceCenter, String message) {
        this.smsId = smsId;
        this.from = from;
        this.type = type;
        this.timeSent = timeSent;
        this.timeReceived = timeReceived;
        this.serviceCenter = serviceCenter;
        this.message = message;
    }

    public String getSmsId() {
        return smsId;
    }

    public void setSmsId(String smsId) {
        this.smsId = smsId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(String timeSent) {
        this.timeSent = timeSent;
    }

    public String getTimeReceived() {
        return timeReceived;
    }

    public void setTimeReceived(String timeReceived) {
        this.timeReceived = timeReceived;
    }

    public String getServiceCenter() {
        return serviceCenter;
    }

    public void setServiceCenter(String serviceCenter) {
        this.serviceCenter = serviceCenter;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
