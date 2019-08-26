package ng.mobilea.airvoucher.utilities.loaders;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import me.everything.providers.android.telephony.Sms;
import ng.mobilea.airvoucher.utilities.SmsRetriever;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 12/11/2017.
 */

public class LocalSmsLoader {
    private OnLocalSms mListener;
    private Context context;
    private String errorMessage;

    public LocalSmsLoader(OnLocalSms mListener, Context context) {
        this.mListener = mListener;
        this.context = context;
    }

    public void startLoading(){
        new Fetcher().execute(context);
    }

    private class Fetcher extends AsyncTask<Context, String, List<Sms>>{

        @Override
        protected List<Sms> doInBackground(Context... params) {
            Context context = params[0];
            try {
                errorMessage = "success";
                return SmsRetriever.mSmsInbox(context);
            }catch (Exception e){
                errorMessage = e.getMessage();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Sms> smses){
            if(smses == null){
                mListener.onLocalSms(false, errorMessage, null);
            }else{
                mListener.onLocalSms(true, errorMessage, smses);
            }
        }
    }

    public interface OnLocalSms{
        void onLocalSms(boolean isLoaded,String errorMessage, List<Sms> mSmses);
    }
}
