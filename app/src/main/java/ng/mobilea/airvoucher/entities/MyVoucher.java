package ng.mobilea.airvoucher.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 12/13/2017.
 */

public class MyVoucher extends SugarRecord {
    private String ref;
    private String date;
    private String amount;
    private String sourceMsisdn;
    private String voucher;
    private String serialNumber;
    private String device;
    private String sentDate;
    private String receivedDate;
    @JsonIgnore
    private boolean isUploaded;

    public MyVoucher() {
    }

    public MyVoucher(String ref, String date, String amount, String sourceMsisdn, String voucher, String serialNumber, String device, String sentDate, String receivedDate, boolean isUploaded) {
        this.ref = ref;
        this.date = date;
        this.amount = amount;
        this.sourceMsisdn = sourceMsisdn;
        this.voucher = voucher;
        this.serialNumber = serialNumber;
        this.device = device;
        this.sentDate = sentDate;
        this.receivedDate = receivedDate;
        this.isUploaded = isUploaded;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
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

    public String getSourceMsisdn() {
        return sourceMsisdn;
    }

    public void setSourceMsisdn(String sourceMsisdn) {
        this.sourceMsisdn = sourceMsisdn;
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

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getSentDate() {
        return sentDate;
    }

    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }

    public String getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(String receivedDate) {
        this.receivedDate = receivedDate;
    }

    @Override
    public String toString() {
        return "MyVoucher{" +
                "ref='" + ref + '\'' +
                ", date='" + date + '\'' +
                ", amount='" + amount + '\'' +
                ", sourceMsisdn='" + sourceMsisdn + '\'' +
                ", voucher='" + voucher + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", device='" + device + '\'' +
                ", sentDate='" + sentDate + '\'' +
                ", receivedDate='" + receivedDate + '\'' +
                ", isUploaded=" + isUploaded +
                '}';
    }
}
