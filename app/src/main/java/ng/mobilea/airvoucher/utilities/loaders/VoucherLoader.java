package ng.mobilea.airvoucher.utilities.loaders;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ng.mobilea.airvoucher.client.AgentService;
import ng.mobilea.airvoucher.client.ServiceGenerator;
import ng.mobilea.airvoucher.entities.MyVoucher;
import ng.mobilea.airvoucher.fragments.SmsFrag;
import ng.mobilea.airvoucher.utilities.DataFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 12/13/2017.
 */

public class VoucherLoader {
    private OnVoucherLoader mListener;
    private Context context;
    private String message;
    private MyVoucher vResponse = null;
    private MyVoucher vRequest;

    public VoucherLoader(OnVoucherLoader mListener, Context context, MyVoucher vRequest) {
        this.mListener = mListener;
        this.context = context;
        this.vRequest = vRequest;
    }

    public void startLoading(){
        BackLoading backLoading = new BackLoading();
        backLoading.execute();
    }

    public interface OnVoucherLoader {
        void onVoucherLoader(boolean isLoaded, Object object);
    }

    private class BackLoading{
        void execute(String... parms) {
            try {

                Log.d("Request", DataFactory.objectToString(vRequest));
                AgentService agentService = ServiceGenerator.createService(AgentService.class, AgentService.BASE_URL);
                Call<MyVoucher> callService = agentService.uploadVoucher(AgentService.CREATE_VOUCHER, vRequest);
                callService.enqueue(new Callback<MyVoucher>() {
                    @Override
                    public void onResponse(Call<MyVoucher> call, Response<MyVoucher> response) {
                        int statusCode = response.code();

                        if(statusCode == 500){
                            message = "internal server error";
                            onPostExecute(vResponse);
                            return;
                        }else if(statusCode != 200 && statusCode != 201){
                            message = response.message();
                            onPostExecute(vResponse);
                            return;
                        }

                        if(response.body() == null){
                            message = "Empty server response";
                            onPostExecute(vResponse);
                            return;
                        }
                        onPostExecute(response.body());
                    }

                    @Override
                    public void onFailure(Call<MyVoucher> call, Throwable t) {
                        message = "Network failure. "+t.getMessage();
                        onPostExecute(vResponse);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                onPostExecute(vResponse);
            }
        }

        private void sendBroadcast(){
            Intent broadcastIntent = new Intent(SmsFrag.SMS_BROADCAST_FILTER).setAction(SmsFrag.SMS_BROADCAST_FILTER);
            context.sendBroadcast(broadcastIntent);
        }

        protected void onPostExecute(MyVoucher myVoucher) {
            try{
                if(myVoucher == null)
                    mListener.onVoucherLoader(false, message);
                else{
                    mListener.onVoucherLoader(true, myVoucher);
                }
            }catch (Exception e){
                e.printStackTrace();
                mListener.onVoucherLoader(false, "Error: "+e.getMessage());
            }
            if(context != null){
                sendBroadcast();
            }
        }
    }
}
