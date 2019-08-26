package ng.mobilea.airvoucher.utilities.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ng.mobilea.airvoucher.R;
import ng.mobilea.airvoucher.entities.MyVoucher;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 12/11/2017.
 */

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.MyViewHolder> {
    private OnVoucherInteraction mListener;
    private Context context;
    private List<MyVoucher> myVouchers;
    private List<MyVoucher> tempList;
    private boolean addedList = false;

    public VoucherAdapter(OnVoucherInteraction mListener, Context context, List<MyVoucher> myVouchers) {
        this.mListener = mListener;
        this.context = context;
        this.myVouchers = myVouchers;
        this.tempList = new ArrayList<>();
        this.tempList.addAll(myVouchers);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.style_sms, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final MyVoucher myVoucher = myVouchers.get(position);

        holder.voucherPreview.setText(myVoucher.toString());

        holder.time.setText(myVoucher.getReceivedDate());

        holder.holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onVoucher(true, myVoucher);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return myVouchers.size();
    }

    public void refreshAdapter(List<MyVoucher> myVouchers) {
        this.myVouchers.clear();
        this.tempList.clear();
        this.myVouchers.addAll(myVouchers);
        this.tempList.addAll(myVouchers);
        notifyDataSetChanged();
    }

    public void filter(String charText) {
        try {
            myVouchers.clear();
            if (charText.trim().length() == 0) {
                myVouchers.addAll(tempList);
            }else{
                charText = charText.toLowerCase(Locale.getDefault());
                for (MyVoucher myVoucher : tempList) {
                    if (myVoucher.toString().toLowerCase(Locale.getDefault()).contains(charText)) {
                        myVouchers.add(myVoucher);
                    }
                }
            }
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.holder)
        CardView holder;
        @BindView(R.id.smsPreview)
        TextView voucherPreview;
        @BindView(R.id.timeStamp)
        TextView time;
        @BindView(R.id.icon)
        ImageView icon;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnVoucherInteraction {
        void onVoucher(boolean isClicked, Object object);
    }
}
