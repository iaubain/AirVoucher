package ng.mobilea.airvoucher.utilities.loaders;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import ng.mobilea.airvoucher.client.AgentService;
import ng.mobilea.airvoucher.client.ServiceGenerator;
import ng.mobilea.airvoucher.entities.MyVoucher;
import ng.mobilea.airvoucher.fragments.SmsFrag;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 12/13/2017.
 */

public class BulkVoucherLoader {
    private OnBulkVoucherLoader mListener;
    private Context context;
    private String message;
    private List<MyVoucher> vRequest;
    private List<MyVoucher> vResponse = new ArrayList<>();

    public BulkVoucherLoader(OnBulkVoucherLoader mListener, Context context, List<MyVoucher> vRequest) {
        this.mListener = mListener;
        this.context = context;
        this.vRequest = vRequest;
    }

    public void startLoading(){
        BackLoading backLoading = new BackLoading();
        backLoading.execute();
    }

    public interface OnBulkVoucherLoader {
        void onBulkVoucherLoader(boolean isLoaded, String object, List<MyVoucher> myVouchers);
    }

    private class BackLoading{
        void execute(String... parms) {
            try {

                AgentService agentService = ServiceGenerator.createService(AgentService.class, AgentService.BASE_URL);
                Call<List<MyVoucher>> callService = agentService.uploadBulkVoucher(AgentService.CREATE_BULK_VOUCHER, vRequest);
                callService.enqueue(new Callback<List<MyVoucher>>() {
                    @Override
                    public void onResponse(Call<List<MyVoucher>> call, Response<List<MyVoucher>> response) {
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
                    public void onFailure(Call<List<MyVoucher>> call, Throwable t) {
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

        protected void onPostExecute(List<MyVoucher> myVouchers) {
            try{
                if(myVouchers == null || myVouchers.isEmpty())
                    mListener.onBulkVoucherLoader(false, message, myVouchers);
                else{
                    mListener.onBulkVoucherLoader(true, "Success", myVouchers);
                }
                if(context != null){
                    sendBroadcast();
                }
            }catch (Exception e){
                e.printStackTrace();
                mListener.onBulkVoucherLoader(false, "Error: "+e.getMessage(), myVouchers);
            }

        }
    }
}
