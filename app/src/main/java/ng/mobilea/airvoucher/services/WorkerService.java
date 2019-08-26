package ng.mobilea.airvoucher.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import ng.mobilea.airvoucher.entities.MyVoucher;
import ng.mobilea.airvoucher.fragments.SmsFrag;
import ng.mobilea.airvoucher.utilities.DataFactory;
import ng.mobilea.airvoucher.utilities.dbs.DbHandler;
import ng.mobilea.airvoucher.utilities.loaders.BulkVoucherLoader;
import ng.mobilea.airvoucher.utilities.loaders.CallbackLoader;
import ng.mobilea.airvoucher.utilities.loaders.VoucherLoader;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class WorkerService extends IntentService implements VoucherLoader.OnVoucherLoader,
        BulkVoucherLoader.OnBulkVoucherLoader, CallbackLoader.OnCallbackLoader {
    private static final String ACTION_SMS_RECEIVED = "ng.mobilea.airvoucher.services.action.ACTION_SMS_RECEIVED";
    private static final String ACTION_UPLOAD_VOUCHER = "ng.mobilea.airvoucher.services.action.ACTION_UPLOAD_VOUCHER";
    public static final String ACTION_UPLOAD_PENDING = "ng.mobilea.airvoucher.services.action.ACTION_UPLOAD_PENDING";
    private static final String ACTION_SCHEDULE = "ng.mobilea.airvoucher.services.action.ACTION_SCHEDULE";
    public static final String VOUCHER_DATA = "VOUCHER_DATA";

    private MyVoucher mVoucher;

    public WorkerService() {
        super("WorkerService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionSmsReceived(Context context) {
        Intent intent = new Intent(context, WorkerService.class);
        intent.setAction(ACTION_SMS_RECEIVED);
        context.startService(intent);
    }
    public static void uploadVoucher(Context context, String mVoucher) {
        Intent intent = new Intent(context, WorkerService.class);
        intent.setAction(ACTION_UPLOAD_VOUCHER);
        Bundle bundle = new Bundle();
        bundle.putString(VOUCHER_DATA, mVoucher);
        intent.putExtras(bundle);
        context.startService(intent);
    }
    private void scheduleAlarm() {
        Calendar cal = Calendar.getInstance();
        Intent alarmIntent = new Intent(getApplicationContext(), WorkerService.class);
        alarmIntent.setAction(WorkerService.ACTION_UPLOAD_PENDING);

        PendingIntent pIntent = PendingIntent.getService(getApplicationContext(),
                999,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        cal.add(Calendar.SECOND, 30);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 21 * 1000, pIntent);
    }

    public static void startSchedule(Context context) {
        Intent intent = new Intent(context, WorkerService.class);
        intent.setAction(ACTION_SCHEDULE);
        context.startService(intent);
    }

    public static void uploadPending(Context context) {
        Intent intent = new Intent(context, WorkerService.class);
        intent.setAction(ACTION_UPLOAD_PENDING);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SMS_RECEIVED.equals(action)) {
                handleActionSmsReceived();
            }else if (ACTION_UPLOAD_VOUCHER.equals(action)) {
                if(intent.getExtras() != null){
                    String vData = intent.getExtras().getString(VOUCHER_DATA);
                    handleUploadVoucher(vData);
                }
            }else if (ACTION_UPLOAD_PENDING.equals(action)) {
                handleUploadPending();
            }else if(ACTION_SCHEDULE.equals(action)){
                scheduleAlarm();
            }
        }
    }

    private void handleActionSmsReceived() {
        Intent broadcastIntent = new Intent(SmsFrag.SMS_BROADCAST_FILTER).setAction(SmsFrag.SMS_BROADCAST_FILTER);
        getApplicationContext().sendBroadcast(broadcastIntent);
    }

    private void handleUploadVoucher(String mVoucher){
        MyVoucher myVoucher = (MyVoucher) DataFactory.stringToObject(MyVoucher.class, mVoucher);
        if(myVoucher == null)
            return;
        this.mVoucher = myVoucher;
        VoucherLoader voucherLoader = new VoucherLoader(WorkerService.this, getApplicationContext(), myVoucher);
        voucherLoader.startLoading();
    }

    private void handleUploadPending(){
        List<MyVoucher> voucherList = DbHandler.getAllPending();
        if(voucherList != null && !voucherList.isEmpty()){
            BulkVoucherLoader bulkVoucherLoader = new BulkVoucherLoader(WorkerService.this, getApplicationContext(), voucherList);
            bulkVoucherLoader.startLoading();
        }
    }

    @Override
    public void onVoucherLoader(boolean isLoaded, Object object) {
        if(!isLoaded)
            Toast.makeText(getApplicationContext(), object+"", Toast.LENGTH_SHORT).show();
        else{
            MyVoucher myVoucher = DbHandler.getPerReference(((MyVoucher) object).getRef());
            if(myVoucher != null){
                myVoucher.setUploaded(true);
                myVoucher.save();
            }
        }

        //Make a Callback
        if(mVoucher != null){
            CallbackLoader callbackLoader = new CallbackLoader(WorkerService.this, mVoucher);
            callbackLoader.startLoading();
        }
    }

    @Override
    public void onBulkVoucherLoader(boolean isLoaded, String object, List<MyVoucher> myVouchers) {
        if(!isLoaded && object != null)
            Toast.makeText(getApplicationContext(), object+"", Toast.LENGTH_SHORT).show();
        else{
            if(myVouchers == null || myVouchers.isEmpty())
                return;
            for(MyVoucher myVoucher : myVouchers){
                MyVoucher mVoucher = DbHandler.getPerReference(myVoucher.getRef());
                if(mVoucher != null){
                    mVoucher.setUploaded(true);
                    mVoucher.save();
                }
            }
        }
    }

    @Override
    public void onCallbackLoader(boolean isLoaded, Object object) {
        Toast.makeText(getApplicationContext(),"Callback: "+object.toString(), Toast.LENGTH_SHORT).show();
    }
}
