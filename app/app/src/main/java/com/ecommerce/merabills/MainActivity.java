package com.ecommerce.merabills;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ICommunicatorPayments {


    TextView mTotalAmount;
    ChipGroup mChipPayments;
    Button mSavePayments;
    TextView mAddPayment;
    Chip mChip;
    Chip mChip2;
    Chip mChip3;
    ArrayList<Chip> mChipList = new ArrayList<>();
    boolean isSaveEnabled = false;
    IntentFilter mIntentFilter;

    ProgressBar mProgressBar;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();


        WorkManager.getInstance(this).getWorkInfosByTagLiveData(PaymentPersitance
                .getInstance().mWorkId).observe(
                this, new Observer<List<WorkInfo>>() {
                    @Override
                    public void onChanged(List<WorkInfo> workInfos) {
                        workInfos.size();
                        if (workInfos.size() > 0) {
                            String[] mResult = workInfos.get(0).getOutputData().getStringArray("mPaymentDetails");

                            mResult[0].toCharArray();
                        }
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        initViews();

        addPayments();
        savePayments();
        chipPaymentsGroup();
        getDataFromViewModel();
        setChipListeners();
        getLiveDataForView();
        mIntentFilter = new IntentFilter("mPaymentDetails");
        mIntentFilter.addAction("success");
        mIntentFilter.addAction("applicationstart");
        LocalBroadcastManager.getInstance(this).registerReceiver(mPaymentsBroadCast, mIntentFilter);

    }

    private void getLiveDataForView() {
        PaymentPersitance.getInstance().getTransactionDetailsIfAny(this);


    }


    private void setChipListeners() {
        mChip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentPersitance.getInstance().reduceAmount(mChip.getTag().toString());
                mTotalAmount.setText(PaymentPersitance.getInstance().totalAmount());
                mChip.setVisibility(View.GONE);
                isSaveEnabled = true;
                isSaveEnabled = true;

                mAddPayment.setVisibility(View.VISIBLE);
                setSaveState(isSaveEnabled);

            }
        });


        mChip2.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentPersitance.getInstance().reduceAmount(mChip2.getTag().toString());
                mTotalAmount.setText(PaymentPersitance.getInstance().totalAmount());
                mChip2.setVisibility(View.GONE);
                isSaveEnabled = true;
                mAddPayment.setVisibility(View.VISIBLE);
                setSaveState(isSaveEnabled);

            }
        });


        mChip3.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentPersitance.getInstance().reduceAmount(mChip3.getTag().toString());
                mTotalAmount.setText(PaymentPersitance.getInstance().totalAmount());
                mChip3.setVisibility(View.GONE);

                mAddPayment.setVisibility(View.VISIBLE);
                isSaveEnabled = true;
                setSaveState(isSaveEnabled);
            }
        });


    }

    private void setSaveState(boolean isSaveEnabled) {

        if (isSaveEnabled && !mTotalAmount.getText().toString().equals("") && !mTotalAmount.getText().toString().equals("0")) {
            mSavePayments.setEnabled(isSaveEnabled);
        } else {
            mSavePayments.setVisibility(View.GONE);
        }
    }


    private void getDataFromViewModel() {

    }


    private void chipPaymentsGroup() {


        mChipPayments.setOnCheckedChangeListener(
                new ChipGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(ChipGroup group, int checkedId) {


                        String mPaymentDetials = mChip.getText().toString();
                        String mPaymentDetails2 = mChip2.getText().toString();
                        String mPaymentDetails3 = mChip3.getText().toString();

                    }
                }
        );
    }

    private void savePayments() {
        mSavePayments.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mProgressBar.setVisibility(View.VISIBLE);

                        String mTotalAmountValue = mTotalAmount.getText().toString();
                        PaymentPersitance.getInstance().getTransction(MainActivity.this, mTotalAmountValue);
                        mSavePayments.setVisibility(View.GONE);
                    }
                }
        );
    }

    private void addPayments() {

        mAddPayment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PaymentDetailsDialog mPaymentDetailsDialog = PaymentDetailsDialog.newInstance(MainActivity.this);
                PaymentDetailsDialog mPayementDetailsDialogPrevious = (PaymentDetailsDialog) getSupportFragmentManager().findFragmentByTag("dialog");
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                if (mPayementDetailsDialogPrevious != null) {
                    ft.remove(mPayementDetailsDialogPrevious);
                    ft.addToBackStack(null);
                }
                mPaymentDetailsDialog.show(ft, "dialog");


            }
        });

    }

    private void initViews() {
        mChipPayments = findViewById(R.id.chipGroup);
        mSavePayments = findViewById(R.id.mSave);
        mAddPayment = findViewById(R.id.add_payments);
        mTotalAmount = findViewById(R.id.total_amount);
        mChip = mChipPayments.findViewById(R.id.sample_chip);
        mChip2 = mChipPayments.findViewById(R.id.sample_chip2);
        mChip3 = mChipPayments.findViewById(R.id.sample_chip3);
        mChipList.add(mChip);
        mChipList.add(mChip2);
        mChipList.add(mChip3);

        mProgressBar = findViewById(R.id.mProgress_save);

        PaymentPersitance.getInstance().setPaymentTypeOnLaunch(false, null);
    }

    @Override
    public void onSuccessfulTransaction() {


        ArrayList<PaymentModel> mPaymentList = PaymentPersitance.getInstance().getmPaymentListDetails();
        for (int i = 0; i < mPaymentList.size(); i++) {
            mChipList.get(i).invalidate();
            mChipList.get(i).setText(mPaymentList.get(i).toString());
            mChipList.get(i).setTag(mPaymentList.get(i).getmTransactionType());
            mChipList.get(i).setVisibility(View.VISIBLE);
        }

        mTotalAmount.setText(PaymentPersitance.getInstance().totalAmount());

        if (mPaymentList.size() == 3) {
            mAddPayment.setVisibility(View.GONE);
        }
        mSavePayments.setVisibility(View.VISIBLE);

    }

    private BroadcastReceiver mPaymentsBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("mPaymentDetails")) {
                PaymentModel mPaymentModel = intent.getParcelableExtra("paymentDetailsObject");
                mPaymentModel.getmPaymentModel().size();

                PaymentPersitance.getInstance().setPaymentTypeOnLaunch(true, mPaymentModel.getmPaymentModel());
                PaymentPersitance.getInstance().setPredefinedPaymentObjects(mPaymentModel.getmPaymentModel());


                for (int i = 0; i < mPaymentModel.getmPaymentModel().size(); i++) {
                    mChipList.get(i).setText(mPaymentModel.getmPaymentModel().get(i).toString());
                    mChipList.get(i).setTag(mPaymentModel.getmPaymentModel().get(i).getmTransactionType());
                    mChipList.get(i).setVisibility(View.VISIBLE);
                    mTotalAmount.setText(mPaymentModel.getmPaymentModel().get(mPaymentModel.getmPaymentModel().size() - 1).getmTotalAmount());
                }


                if (mPaymentModel.getmPaymentModel().size() == 3) {
                    mAddPayment.setVisibility(View.GONE);
                }


            }
            if (intent.getAction().equals("success")) {
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "save successful", Toast.LENGTH_LONG).show();

            }


        }

    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mPaymentsBroadCast);
    }
}
