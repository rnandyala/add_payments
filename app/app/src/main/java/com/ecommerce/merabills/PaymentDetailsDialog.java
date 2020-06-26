package com.ecommerce.merabills;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class PaymentDetailsDialog extends DialogFragment {
    ArrayList<String> payments;
    private int mAdapterPosition;
    AppCompatSpinner mTypeOfPayments;
    View v;
    String mAmountValue = "";
    EditText mEnterAmount;
    Button mOk;
    Button mcancel;
    EditText mProvider;
    EditText mTransaction;

    ICommunicatorPayments mICommunicatorPayments;
    private String mProviderMessage;
    private String mTransactionMessage;


    public static PaymentDetailsDialog newInstance(ICommunicatorPayments mICommunicatorPayments) {

        PaymentDetailsDialog mPaymentDetailsDialog = new PaymentDetailsDialog(mICommunicatorPayments);

        return mPaymentDetailsDialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog mDialog = getDialog();

        if (mDialog != null) {
            mDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    public PaymentDetailsDialog(ICommunicatorPayments mICommunicatorPayment) {
        // Required empty public constructor
        this.mICommunicatorPayments = mICommunicatorPayment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog mDialog = super.onCreateDialog(savedInstanceState);

        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return mDialog;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.payment_details, container, false);
        initViews();
        return v;
    }

    private void initViews() {
        mTypeOfPayments = v.findViewById(R.id.mChoosetrans);
        mEnterAmount = v.findViewById(R.id.enter_amount);
        mOk = v.findViewById(R.id.mOk);
        mcancel = v.findViewById(R.id.mCancel);
        mProvider = v.findViewById(R.id.provider);
        mTransaction = v.findViewById(R.id.transaction);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        mTypeOfPayments.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mAdapterPosition = position;

                        if (payments.get(mTypeOfPayments.getSelectedItemPosition()).equals("cash")) {
                            mTransaction.setVisibility(View.GONE);
                            mProvider.setVisibility(View.GONE);


                        } else {
                            mTransaction.setVisibility(View.VISIBLE);
                            mProvider.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Toast.makeText(PaymentDetailsDialog.this.getContext(), "test on nothing selected call", Toast.LENGTH_LONG).show();

                    }
                }

        );


        mEnterAmount.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAmountValue = mEnterAmount.getText().toString();
                    }
                }
        );


        mOk.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAmountValue = mEnterAmount.getText().toString();
                        mProviderMessage = mProvider.getText().toString();
                        mTransactionMessage = mTransaction.getText().toString();
                        PaymentPersitance.getInstance().setTransactionObject(mAdapterPosition, mAmountValue, mProviderMessage, mTransactionMessage);
                        PaymentPersitance.getInstance().setDifferentTransactions(mAdapterPosition);

                        mICommunicatorPayments.onSuccessfulTransaction();
                        dismiss();

                    }
                }
        );

        mcancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                }
        );


    }


    private void initView(final View view) {
        payments = PaymentPersitance.getInstance().getmPaymentTypes();

        if (payments != null) {

            ArrayAdapter<String> mListOfpayment = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, payments);
            mListOfpayment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            mTypeOfPayments.setAdapter(mListOfpayment);
        }
    }


    @Override
    public void onResume() {
        super.onResume();


    }
}
