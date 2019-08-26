package ng.mobilea.airvoucher.utilities;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.everything.providers.android.telephony.Sms;
import me.everything.providers.android.telephony.TelephonyProvider;
import ng.mobilea.airvoucher.models.inbox.SmsInBox;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 12/11/2017.
 */

public class SmsRetriever {
    public static final List<SmsInBox> mInBox(Context context)throws Exception{
        Uri smsUri = Uri.parse("content://sms/inbox");
        if(smsUri == null){
            throw new Exception("No inbox");
        }
        Cursor cursor = context.getContentResolver().query(smsUri, null, null, null, null);
        if(cursor == null){
            throw new Exception("No SMS Uri");
        }
        if(!cursor.moveToFirst()){
            throw new Exception("No SMS found");
        }
        int smsCount = cursor.getCount();
        if(smsCount <= 0){
            return new ArrayList<>();
        }

        List<SmsInBox> mSmses = new ArrayList<>();
        for(int count = 0; count < smsCount; count++){
        mSmses.add(new SmsInBox(cursor.getString(cursor.getColumnIndexOrThrow("_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("address")),
                    "sms",
                    cursor.getString(cursor.getColumnIndexOrThrow("date")),
                    cursor.getString(cursor.getColumnIndexOrThrow("date")),
                    "N/A",
                    cursor.getString(cursor.getColumnIndexOrThrow("body"))));

            Log.d("SMS", cursor.getString(count));
            cursor.moveToNext();
        }
        cursor.close();
        return mSmses;
    }

    public static final List<Sms> mSmsInbox(Context context)throws Exception{
        TelephonyProvider telephonyProvider = new TelephonyProvider(context);
        List<Sms> smses = telephonyProvider.getSms(TelephonyProvider.Filter.INBOX).getList();
        if(smses == null ||smses.isEmpty()){
            throw new Exception("No SMS found");
        }
        return smses;
    }
}
