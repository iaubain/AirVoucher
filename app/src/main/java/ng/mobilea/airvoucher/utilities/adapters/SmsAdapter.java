package ng.mobilea.airvoucher.utilities.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.everything.providers.android.telephony.Sms;
import ng.mobilea.airvoucher.R;
import ng.mobilea.airvoucher.entities.MyVoucher;
import ng.mobilea.airvoucher.services.WorkerService;
import ng.mobilea.airvoucher.utilities.DataFactory;
import ng.mobilea.airvoucher.utilities.dbs.DbHandler;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 12/11/2017.
 */

public class SmsAdapter extends RecyclerView.Adapter<SmsAdapter.MyViewHolder> {
    private OnSmsInteraction mListener;
    private Context context;
    private List<Sms> mSmses;
    private List<Sms> tempList;
    private boolean addedList = false;

    public SmsAdapter(OnSmsInteraction mListener, Context context, List<Sms> mSmses) {
        this.mListener = mListener;
        this.context = context;
        this.mSmses = mSmses;
        this.tempList = new ArrayList<>();
        this.tempList.addAll(mSmses);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.style_sms, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Sms sms = mSmses.get(position);

        holder.smsPreview.setText(sms.body);
        try {
            holder.time.setText(DataFactory.formatDate(new Date(sms.receivedDate)));
        } catch (Exception e) {
            e.printStackTrace();
            holder.time.setText("" + sms.receivedDate);
        }
        holder.holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onSmsInteraction(true, sms);
                }
            }
        });

        try {
            if (sms.body.toLowerCase().contains("voucher")) {
                String[] mData = DataFactory.splitString(sms.body, "\n");
                for (String data : mData) {
                    Log.d("SMS", data);
                }
                if (mData.length >= 6) {
                    //make SMS data object
                    String ref = DataFactory.splitString(mData[0], ":")[1].trim();
                    if (DbHandler.getPerReference(ref) == null) {
                        String time = mData[1].trim();
                        String amount = DataFactory.splitString(mData[2], ":")[1].trim();
                        String from = mData[3].trim();
                        String voucher = DataFactory.splitString(mData[4], ":")[1].trim();
                        String serialNumber = DataFactory.splitString(mData[5], ":")[1].trim();

                        Calendar sentDate = Calendar.getInstance();
                        sentDate.setTimeInMillis(sms.sentDate);

                        Calendar receivedDate = Calendar.getInstance();
                        receivedDate.setTimeInMillis(sms.receivedDate);

                        String sent, received;
                        try {
                            sent = DataFactory.formatDateTime(sentDate.getTime());
                            received = DataFactory.formatDateTime(receivedDate.getTime());
                        } catch (Exception e) {
                            e.printStackTrace();
                            sent = sms.sentDate + "";
                            received = sms.receivedDate + "";
                        }

                        MyVoucher myVoucher = new MyVoucher(ref, time, amount, sms.address, voucher, serialNumber, Build.SERIAL, sent, received, false);
                        long id = myVoucher.save();
                        //WorkerService.uploadVoucher(context, DataFactory.objectToString(myVoucher));
                        Log.d("SMSDATA", "DB ID: " + id + " = " + DataFactory.objectToString(myVoucher));
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mSmses.size();
    }

    public void refreshAdapter(List<Sms> mSmses) {
        this.mSmses.clear();
        this.tempList.clear();
        this.mSmses.addAll(mSmses);
        this.tempList.addAll(mSmses);
        notifyDataSetChanged();
    }

    public void filter(String charText) {
        try {
            mSmses.clear();
            if (charText.trim().length() == 0) {
                mSmses.addAll(tempList);
            } else {
                charText = charText.toLowerCase(Locale.getDefault());
                for (Sms sms : tempList) {
                    if (sms.toString().toLowerCase(Locale.getDefault()).contains(charText)) {
                        mSmses.add(sms);
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
        TextView smsPreview;
        @BindView(R.id.timeStamp)
        TextView time;
        @BindView(R.id.icon)
        ImageView icon;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnSmsInteraction {
        void onSmsInteraction(boolean isClicked, Object object);
    }
}
