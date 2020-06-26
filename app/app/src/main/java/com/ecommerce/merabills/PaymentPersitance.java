package com.ecommerce.merabills;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PaymentPersitance {
    String mWorkId;
    static ArrayList<PaymentModel> mPaymentList = new ArrayList<>();
    private String mTypeOfTransaction;
    private double mAmount;
    private File mLaypaymentFile;
    List<PaymentModel> mArrayOfPaymentModel;


    private static PaymentPersitance mPaymentPersistance;


    private ArrayList<String> mPaymentTypes;


    private PaymentPersitance() {

    }

    public static PaymentPersitance getInstance() {
        if (mPaymentPersistance == null) {
            mPaymentPersistance = new PaymentPersitance();
            return mPaymentPersistance;
        } else {
            return mPaymentPersistance;
        }
    }


    public void setPredefinedPaymentObjects(List<PaymentModel> mPaymentList2) {
        for (PaymentModel mPaymentModel : mPaymentList2) {
            mPaymentList.add(new PaymentModel(mPaymentModel.getmPaymentAmount(), mPaymentModel.getmTransactionType(), mPaymentModel.getmTransactionDescriptiona(), mPaymentModel.getmTransactionDescriptionb()));
        }
    }


    public void setPaymentTypeOnLaunch(boolean isComingFromApplicationLaunch, List<PaymentModel> mPaymentList) {

        mPaymentTypes = new ArrayList<>();
        mPaymentTypes.add("cash");
        mPaymentTypes.add("creditcard");
        mPaymentTypes.add("banktransfer");

        if (isComingFromApplicationLaunch) {
            ArrayList<String> mPaymentTypesReplica = new ArrayList<>();
            for (int i = 0; i < mPaymentList.size(); i++) {
                mPaymentTypesReplica.add(mPaymentList.get(i).getmTransactionType());
            }
            mPaymentTypes.removeAll(mPaymentTypesReplica);
        }

    }

    public ArrayList<String> getmPaymentTypes() {
        return mPaymentTypes;
    }

    public ArrayList<PaymentModel> setTransactionObject(int position, String mPaymentAmount, String mProviderDescription, String mTransactionDescription) {
        mTypeOfTransaction = mPaymentTypes.get(position);
        mAmount = Double.parseDouble(mPaymentAmount);
        mPaymentList.add(new PaymentModel(mAmount, mTypeOfTransaction, mProviderDescription, mTransactionDescription));
        return mPaymentList;
    }

    public ArrayList<PaymentModel> getmPaymentListDetails() {
        return mPaymentList;
    }

    public void setDifferentTransactions(int position) {
        mPaymentTypes.remove(position);
    }


    public String totalAmount() {
        Double mTotalSum = 0.0;
        for (int i = 0; i < mPaymentList.size(); i++) {
            mTotalSum += mPaymentList.get(i).getmPaymentAmount();
        }

        return String.valueOf(mTotalSum);


//String.format()
    }
    public String reduceAmount(String paymentType) {

        for (int i = 0; i < getmPaymentListDetails().size(); i++) {
            if (getmPaymentListDetails().get(i).getmTransactionType().equalsIgnoreCase(paymentType)) {
                getmPaymentListDetails().remove(i);
                mPaymentTypes.add(paymentType);
            }
        }


        return totalAmount();
    }


    public void saveDataToFile(Context mContext, String mTotalAmount) {
        ArrayList<PaymentModel> mPaymentList = getmPaymentListDetails();
        mPaymentList.get(mPaymentList.size() - 1).setmTotalAmount(mTotalAmount);
        if (addDataToFile(mContext, mPaymentList)) {
            getDataFromFile();
        }

        return;
    }


    @SuppressLint("RestrictedApi")
    public void getTransactionDetailsIfAny(Context mContext) {

        Data.Builder data = new Data.Builder();
        data.putBoolean("isFilewriter", false);
        WorkRequest mReadFileRequest = new OneTimeWorkRequest.Builder(FileWriteReaderManager.class)
                .setInputData(data.build())
                .build();

        mWorkId = mReadFileRequest.getStringId();

        WorkManager mWorkManager = WorkManager.getInstance(mContext);
        mWorkManager.enqueue(mReadFileRequest);


    }


    public void getTransction(Context mContext, String mTotalAmount) {
        Data.Builder data = new Data.Builder();
        data.putBoolean("isFilewriter", true);
        data.putString("totalAmount", mTotalAmount);
        WorkRequest mWriteFileRequest = new OneTimeWorkRequest
                .Builder(FileWriteReaderManager.class)
                .setInputData(data.build())
                .build();
        WorkManager.getInstance(mContext).enqueue(mWriteFileRequest);
    }


    private boolean getDataFromFile() {

        BufferedReader mBufferedReader = null;
        try {
            String inputLine = "";
            mBufferedReader = new BufferedReader(new FileReader(mLaypaymentFile.getAbsolutePath()));
            while ((inputLine = mBufferedReader.readLine()) != null) {
                Gson mGson = new Gson();
                PaymentModel[] mPaymentModelArray = mGson.fromJson(inputLine, PaymentModel[].class);
                mArrayOfPaymentModel = Arrays.asList(mPaymentModelArray);
                mArrayOfPaymentModel.size();
            }


        } catch (Exception ex) {
            return false;
        }

        return true;
    }


    private List<PaymentModel> getmPaymentDetails() {
        return mArrayOfPaymentModel;
    }


    private boolean addDataToFile(Context mContext, ArrayList<PaymentModel> mPaymentModel) {
        Gson mGson = new Gson();
        String json = mGson.toJson(mPaymentModel);
        try {
            File mFile = new File(mContext.getFilesDir() + "/payments");

            if (!mFile.exists()) {
                mFile.mkdirs();
            } else {
                mLaypaymentFile = new File(mFile, "LastPayment.txt");
                FileWriter mFileWriter = new FileWriter(mLaypaymentFile);
                BufferedWriter mBufferWriter = new BufferedWriter(mFileWriter);
                mBufferWriter.write(json);
                mBufferWriter.close();
                mFileWriter.close();

            }
            return true;

        } catch (Exception ex) {
            ex.getMessage();

            return false;
        }

    }
}
