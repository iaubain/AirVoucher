package ng.mobilea.airvoucher.utilities.loaders;

import ng.mobilea.airvoucher.client.AgentService;
import ng.mobilea.airvoucher.client.ServiceGenerator;
import ng.mobilea.airvoucher.entities.MyVoucher;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 12/13/2017.
 */

public class CallbackLoader {
    private OnCallbackLoader mListener;
    private String message;
    private MyVoucher vResponse = null;
    private MyVoucher vRequest;

    public CallbackLoader(OnCallbackLoader mListener, MyVoucher vRequest) {
        this.mListener = mListener;
        this.vRequest = vRequest;
    }

    public void startLoading(){
        BackLoading backLoading = new BackLoading();
        backLoading.execute();
    }

    public interface OnCallbackLoader {
        void onCallbackLoader(boolean isLoaded, Object object);
    }

    private class BackLoading{
        void execute(String... parms) {
            try {

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
                        }else if(statusCode != 200){
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

        protected void onPostExecute(MyVoucher myVoucher) {
            try{
                if(myVoucher == null)
                    mListener.onCallbackLoader(false, message);
                else{
                    mListener.onCallbackLoader(true, myVoucher);
                }
            }catch (Exception e){
                e.printStackTrace();
                mListener.onCallbackLoader(false, "Error: "+e.getMessage());
            }

        }
    }
}
