package ng.mobilea.airvoucher.client;

import java.util.List;

import ng.mobilea.airvoucher.entities.MyVoucher;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;


/**
 * Created by Owner on 7/9/2016.
 */
public interface AgentService {
    //String BASE_URL="http://41.186.53.35:8080/"; //Link to MTN server
    String BASE_URL="http://197.210.2.229:80/AirVoucherAgent/"; //Link to AOS server, production
    String UPLOAD_VOUCHER = "core/creation";
    String UPLOAD_BULK_VOUCHERS = "core/creation";

    String CREATE_VOUCHER = "CREATE_VOUCHER";
    String CREATE_BULK_VOUCHER = "CREATE_BULK_VOUCHER";

    @POST(AgentService.UPLOAD_VOUCHER)
    Call<MyVoucher> uploadVoucher(@Header("cmd")String cmd, @Body MyVoucher myVoucher);

    @POST(AgentService.UPLOAD_BULK_VOUCHERS)
    Call<List<MyVoucher>> uploadBulkVoucher(@Header("cmd")String cmd, @Body List<MyVoucher> myVouchers);
}
