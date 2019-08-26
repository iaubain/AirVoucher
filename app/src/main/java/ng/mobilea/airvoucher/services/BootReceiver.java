package ng.mobilea.airvoucher.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ng.mobilea.airvoucher.activities.Home;
import ng.mobilea.airvoucher.utilities.dbs.DbHandler;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 12/13/2017.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent i = new Intent(context, Home.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

            //Start worker service's schedule
            WorkerService.startSchedule(context);

            //clean the DB
            deleteOldVouchers();
        }
    }

    private void deleteOldVouchers(){
        try {
            DbHandler.deleteOldSMS();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
