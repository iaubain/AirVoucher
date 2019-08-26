package ng.mobilea.airvoucher.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.everything.providers.android.telephony.Sms;
import ng.mobilea.airvoucher.R;
import ng.mobilea.airvoucher.utilities.DataFactory;
import ng.mobilea.airvoucher.utilities.adapters.SmsAdapter;
import ng.mobilea.airvoucher.utilities.loaders.LocalSmsLoader;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnSmsFragment} interface
 * to handle interaction events.
 * Use the {@link SmsFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SmsFrag extends Fragment implements LocalSmsLoader.OnLocalSms,
        SmsAdapter.OnSmsInteraction {
    public static final String SMS_BROADCAST_FILTER = "ng.mobilea.airvoucher.REFRESH_SMSES";
    private OnSmsFragment mListener;

    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private ProgressDialog progressDialog;

    private SmsAdapter adapter;

    @BindView(R.id.swipeHolder)
    SwipeRefreshLayout swipe;
    @BindView(R.id.mSms)
    RecyclerView mSms;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // now call fragments method here
            String action = intent.getAction();
            if (action == null)
                return;
            if (action.equals(SMS_BROADCAST_FILTER)) {
                refreshRecycler();
            }
        }
    };

    private void refreshRecycler() {
        LocalSmsLoader smsLoader = new LocalSmsLoader(SmsFrag.this, getContext());
        smsLoader.startLoading();
    }

    public SmsFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SmsFrag.
     */
    public static SmsFrag newInstance() {
        SmsFrag fragment = new SmsFrag();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            getActivity().registerReceiver(mReceiver, new IntentFilter(SMS_BROADCAST_FILTER));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.sms_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mProgress("Loading sms");
        LocalSmsLoader smsLoader = new LocalSmsLoader(SmsFrag.this, getContext());
        smsLoader.startLoading();

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LocalSmsLoader smsLoader = new LocalSmsLoader(SmsFrag.this, getContext());
                smsLoader.startLoading();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSmsFragment) {
            mListener = (OnSmsFragment) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            getActivity().unregisterReceiver(mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void mProgress(String message) {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

        progressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.setMessage(message != null ? message : "NO_MESSAGE");
        progressDialog.show();
    }

    private void dismissProgress() {
        if (progressDialog != null)
            if (progressDialog.isShowing())
                progressDialog.dismiss();
    }

    public void filter(String charSequence) {
        if (adapter != null) {
            adapter.filter(charSequence);
        }
    }

    private void popBox(String message) {
        try {
            builder = new AlertDialog.Builder(getContext(), R.style.SimpleBlackDialog);
            builder.setMessage(message != null ? message : "NO_MESSAGE")
                    .setTitle(R.string.dialog_title);
            // Add the buttons
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            dialog = builder.create();
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), message != null ? message : "NO_MESSAGE", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocalSms(boolean isLoaded, String errorMessage, List<Sms> mSmses) {
        dismissProgress();
        if (swipe != null && swipe.isRefreshing())
            swipe.setRefreshing(false);
        if (!isLoaded) {
            popBox("" + errorMessage);
            return;
        }
        //populate the front view
        if(adapter != null && adapter.getItemCount() > 0){
            adapter.refreshAdapter(mSmses);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            mSms.setLayoutManager(mLayoutManager);
            mSms.setHasFixedSize(true);
            mSms.setItemAnimator(new DefaultItemAnimator());
            mSms.setAdapter(adapter);
        } else {
            adapter = new SmsAdapter(SmsFrag.this, getContext(), mSmses);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            mSms.setLayoutManager(mLayoutManager);
            mSms.setHasFixedSize(true);
            mSms.setItemAnimator(new DefaultItemAnimator());
            mSms.setAdapter(adapter);
        }
    }

    @Override
    public void onSmsInteraction(boolean isClicked, Object object) {
        if (isClicked && object != null) {
            Sms sms = (Sms) object;
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

            popBox(sms.body + "\n" +
                    "Type:" + sms.type + "\n" +
                    "From:" + sms.address + "\n" +
                    "Sent: " + sent + "\n" +
                    "Received: " + received);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnSmsFragment {
        void onSmsFragment(Object object);
    }
}
