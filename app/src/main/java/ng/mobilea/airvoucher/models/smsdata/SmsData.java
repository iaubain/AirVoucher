package ng.mobilea.airvoucher.models.smsdata;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 12/11/2017.
 */

public class SmsData {
    String ref;
    String date;
    String amount;
    String from;
    String voucher;
    String serialNumber;

    public SmsData() {
    }

    public SmsData(String ref, String date, String amount, String from, String voucher, String serialNumber) {

        this.ref = ref;
        this.date = date;
        this.amount = amount;
        this.from = from;
        this.voucher = voucher;
        this.serialNumber = serialNumber;
    }

    @Override
    public String toString() {
        return "SmsData{" +
                "ref='" + ref + '\'' +
                ", date='" + date + '\'' +
                ", amount='" + amount + '\'' +
                ", from='" + from + '\'' +
                ", voucher='" + voucher + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                '}';
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getVoucher() {
        return voucher;
    }

    public void setVoucher(String voucher) {
        this.voucher = voucher;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
}
