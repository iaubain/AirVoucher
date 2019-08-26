package ng.mobilea.airvoucher.client;

import java.util.List;

import ng.mobilea.airvoucher.entities.MyVoucher;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;


/**
 * Created by Owner on 7/9/2016.
 */
public interface CallbackService {
    //http://localhost:8080/VouchersManager/vouchers/send
    String BASE_URL="http://197.210.2.229:80/VouchersManager/";
    String CALL_BACK = "vouchers/send";

    @POST(CallbackService.CALL_BACK)
    Call<MyVoucher> callBack(@Body MyVoucher myVoucher);
}
