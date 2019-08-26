package ng.mobilea.airvoucher.utilities.loaders;

import java.util.List;

import ng.mobilea.airvoucher.entities.MyVoucher;
import ng.mobilea.airvoucher.utilities.dbs.DbHandler;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 12/14/2017.
 */

public class LocalVoucherLoader {
    private OnLocalVoucherLoader mListener;

    public LocalVoucherLoader(OnLocalVoucherLoader mListener) {
        this.mListener = mListener;
    }

    public void startLoading(){
        mListener.onLocalVoucher(true, DbHandler.getTodayVouchers(), DbHandler.getAllPending());
    }

    public interface OnLocalVoucherLoader{
        void onLocalVoucher(boolean isLoaded, List<MyVoucher> myVouchers, List<MyVoucher> pending);
    }
}
