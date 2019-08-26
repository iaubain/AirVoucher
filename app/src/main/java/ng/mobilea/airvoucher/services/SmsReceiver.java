package ng.mobilea.airvoucher.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import ng.mobilea.airvoucher.entities.MyVoucher;
import ng.mobilea.airvoucher.fragments.SmsFrag;
import ng.mobilea.airvoucher.models.smsdata.SmsData;
import ng.mobilea.airvoucher.utilities.DataFactory;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 12/11/2017.
 */

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            /* Get Messages */
            Object[] sms = (Object[]) bundle.get("pdus");

            for (int count = 0; count < sms.length; ++count) {
                /* Parse Each Message */
                SmsMessage smsMessage;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    String format = bundle.getString("format");
                    smsMessage = SmsMessage.createFromPdu((byte[]) sms[count], format);
                }else{
                    smsMessage = SmsMessage.createFromPdu((byte[]) sms[count]);
                }

                Calendar calendar = Calendar.getInstance();

                String phone = smsMessage.getOriginatingAddress();
                String message = smsMessage.getMessageBody().toString();
                Date date = new Date();
                String receivedDate = date.toString();
                String sentDate = date.toString();
                /*
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
                 */
                try{
                    receivedDate = DataFactory.formatDateTime(calendar.getTime());
                    calendar.setTimeInMillis(smsMessage.getTimestampMillis());
                    sentDate = DataFactory.formatDateTime(calendar.getTime());
                }catch (Exception e){
                    e.printStackTrace();
                }
                Log.d("SMS", "Sender: "+phone + " Message: " + message);
                //Toast.makeText(context, phone + ": " + message, Toast.LENGTH_SHORT).show();

                if(message.toLowerCase().contains("voucher")){
                    String[] mData = DataFactory.splitString(message, "\n");
                    for(String data : mData){
                        Log.d("SMS", data);
                    }
                    if(mData.length >= 6){
                        //make SMS data object
                        String ref = DataFactory.splitString(mData[0], ":")[1].trim();
                        String time = mData[1].trim();
                        String amount = DataFactory.splitString(mData[2], ":")[1].trim();
                        String from = mData[3].trim();
                        String voucher = DataFactory.splitString(mData[4], ":")[1].trim();
                        String serialNumber = DataFactory.splitString(mData[5], ":")[1].trim();

                        MyVoucher myVoucher = new MyVoucher(ref, time, amount, phone, voucher, serialNumber, Build.SERIAL, sentDate, receivedDate, false);
                        long id = myVoucher.save();
                        WorkerService.uploadVoucher(context, DataFactory.objectToString(myVoucher));
                        Log.d("SMSDATA", "DB ID: "+id+" = "+DataFactory.objectToString(myVoucher));
                    }
                }
                WorkerService.startActionSmsReceived(context);
            }
        }
    }
}
