package ng.mobilea.airvoucher.utilities.dbs;

import com.orm.util.NamingHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ng.mobilea.airvoucher.entities.MyVoucher;
import ng.mobilea.airvoucher.utilities.AppFlow;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 12/13/2017.
 */

public class DbHandler {
    public static void deleteOldSMS(){
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, (-1* AppFlow.MAX_HISTORY));
            DateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd", Locale.getDefault());
            MyVoucher.deleteAll(MyVoucher.class, NamingHelper.toSQLNameDefault("receivedDate")+" <= Datetime(?) ",dateFormat.format(calendar.getTime()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static List<MyVoucher> getTodayVouchers(){
        try {
            Calendar calendar = Calendar.getInstance();
            DateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd", Locale.getDefault());

            List<MyVoucher> mVoucher = MyVoucher.findWithQuery(MyVoucher.class, "SELECT * FROM "+NamingHelper.toSQLName(MyVoucher.class)+
                    " ORDER BY "+NamingHelper.toSQLNameDefault("id")+" DESC");
            List<MyVoucher> todayVoucher = new ArrayList<>();
            if(mVoucher.isEmpty())
                return new ArrayList<>();
            else{
                for(MyVoucher myVoucher : mVoucher){
                    if(dateFormat.parse(myVoucher.getReceivedDate()).getTime() >= dateFormat.parse(dateFormat.format(calendar.getTime())).getTime() &&
                            dateFormat.parse(myVoucher.getReceivedDate()).getTime() <= dateFormat.parse(dateFormat.format(calendar.getTime())).getTime())
                        todayVoucher.add(myVoucher);
                }
                return todayVoucher;
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static List<MyVoucher> getAllPending(){
        return MyVoucher.findWithQuery(MyVoucher.class, "SELECT * FROM "+NamingHelper.toSQLName(MyVoucher.class)+
                " WHERE "+
                NamingHelper.toSQLNameDefault("isUploaded") +" = 0 ORDER BY "+NamingHelper.toSQLNameDefault("id")+" DESC");

    }

    public static MyVoucher getPerReference(String ref){
        List<MyVoucher> voucherList = MyVoucher.findWithQuery(MyVoucher.class, "SELECT * FROM "+NamingHelper.toSQLName(MyVoucher.class)+" WHERE "+
                        NamingHelper.toSQLNameDefault("ref") +" = ?  ORDER BY "+NamingHelper.toSQLNameDefault("id")+" DESC LIMIT 1",
                ref);
        return voucherList.isEmpty() ? null : voucherList.get(0);
    }

}
