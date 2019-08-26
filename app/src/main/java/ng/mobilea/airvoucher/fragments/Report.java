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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ng.mobilea.airvoucher.R;
import ng.mobilea.airvoucher.entities.MyVoucher;
import ng.mobilea.airvoucher.utilities.adapters.VoucherAdapter;
import ng.mobilea.airvoucher.utilities.dbs.DbHandler;
import ng.mobilea.airvoucher.utilities.loaders.BulkVoucherLoader;
import ng.mobilea.airvoucher.utilities.loaders.LocalVoucherLoader;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnReport} interface
 * to handle interaction events.
 * Use the {@link Report#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Report extends Fragment implements
        VoucherAdapter.OnVoucherInteraction,
        LocalVoucherLoader.OnLocalVoucherLoader,
        BulkVoucherLoader.OnBulkVoucherLoader {

    private OnReport mListener;

    public static final String SMS_BROADCAST_FILTER = "ng.mobilea.airvoucher.REFRESH_SMSES";
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private ProgressDialog progressDialog;
    private VoucherAdapter adapter;

    @BindView(R.id.swipeHolder)
    SwipeRefreshLayout swipe;
    @BindView(R.id.mVoucher)
    RecyclerView mVouchers;

    @BindView(R.id.totalReceived)
    TextView totalReceived;
    @BindView(R.id.totalPending)
    TextView totalPending;
    @BindView(R.id.totalSync)
    TextView totalSync;
    @BindView(R.id.sync)
    ImageView sync;

    private List<MyVoucher> pendingVouchers;

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
        LocalVoucherLoader vLoader = new LocalVoucherLoader(Report.this);
        vLoader.startLoading();
    }

    public Report() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Report.
     */
    public static Report newInstance() {
        Report fragment = new Report();
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
        return inflater.inflate(R.layout.report_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mProgress("Loading sms");
        LocalVoucherLoader vLoader = new LocalVoucherLoader(Report.this);
        vLoader.startLoading();

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LocalVoucherLoader vLoader = new LocalVoucherLoader(Report.this);
                vLoader.startLoading();
            }
        });

        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pendingVouchers != null && !pendingVouchers.isEmpty()) {
                    mProgress("Synchronizing...");
                    BulkVoucherLoader bulkVoucherLoader = new BulkVoucherLoader(Report.this, getContext(), pendingVouchers);
                    bulkVoucherLoader.startLoading();
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnReport) {
            mListener = (OnReport) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
    public void onVoucher(boolean isClicked, Object object) {
        if (isClicked && object != null) {
            popBox(object.toString());
        }
    }

    @Override
    public void onLocalVoucher(boolean isLoaded, List<MyVoucher> myVouchers, List<MyVoucher> pending) {
        dismissProgress();
        if (swipe != null && swipe.isRefreshing())
            swipe.setRefreshing(false);
        if (!isLoaded) {
            popBox("Error occurred while syncing with local repository");
            return;
        }

        Log.d("Statistics", " Total received: " + myVouchers.size() + " Total pending: " + pending.size() + " Total synced: " + (myVouchers.size() - pending.size()));
        this.pendingVouchers = pending;
        totalReceived.setText("Total received: " + myVouchers.size());
        totalPending.setText("Total pending: " + pending.size());
        totalSync.setText("Total synced: " + (myVouchers.size() - pending.size()));
        //populate the front view
        if(adapter != null && adapter.getItemCount() > 0){
            adapter.refreshAdapter(myVouchers);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            mVouchers.setLayoutManager(mLayoutManager);
            mVouchers.setHasFixedSize(true);
            mVouchers.setItemAnimator(new DefaultItemAnimator());
            mVouchers.setAdapter(adapter);
        }else{
            adapter = new VoucherAdapter(Report.this, getContext(), myVouchers);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            mVouchers.setLayoutManager(mLayoutManager);
            mVouchers.setHasFixedSize(true);
            mVouchers.setItemAnimator(new DefaultItemAnimator());
            mVouchers.setAdapter(adapter);
        }
    }

    @Override
    public void onBulkVoucherLoader(boolean isLoaded, String object, List<MyVoucher> myVouchers) {
        dismissProgress();
        if (object.isEmpty()) {
            popBox("Successfully synced ");
        } else {
            popBox("Results:\n" + object);
        }

        mProgress("Reloading Smses");
           if(myVouchers != null && !myVouchers.isEmpty())
            for(MyVoucher myVoucher : myVouchers){
                MyVoucher mVoucher = DbHandler.getPerReference(myVoucher.getRef());
                if(mVoucher != null){
                    mVoucher.setUploaded(true);
                    mVoucher.save();
                }
            }

        LocalVoucherLoader vLoader = new LocalVoucherLoader(Report.this);
        vLoader.startLoading();
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
    public interface OnReport {
        void onReportInteraction(Object object);
    }
}
