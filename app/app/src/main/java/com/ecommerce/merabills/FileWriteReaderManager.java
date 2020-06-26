package com.ecommerce.merabills;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class FileWriteReaderManager extends Worker {
    Context mContext;
    WorkerParameters workerParams;
    Gson mGson;
    Data.Builder mDataForPayments;

    public FileWriteReaderManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.mContext = context;
        this.workerParams = workerParams;
    }

    @NonNull
    @Override
    public Result doWork() {
        mGson = new Gson();
        boolean isFileWriter = workerParams.getInputData().getBoolean("isFilewriter", false);
        String mTotalAmount = workerParams.getInputData().getString("totalAmount");
        if (isFileWriter) {
            try {
                ArrayList<PaymentModel> mListOfPaymentModels = PaymentPersitance.getInstance().getmPaymentListDetails();
                mListOfPaymentModels.size();
                mListOfPaymentModels.get(mListOfPaymentModels.size() - 1).setmTotalAmount(mTotalAmount);
                String mPaymentsJson = mGson.toJson(mListOfPaymentModels);
                File mFileDir = new File(mContext.getFilesDir() + "/payments");
                if (!mFileDir.exists()) {
                    mFileDir.mkdirs();

                }

                File mLastPaymentFile = new File(mFileDir, "lastPayment.txt");
                FileWriter mFileWriter = new FileWriter(mLastPaymentFile);
                BufferedWriter mBufferWriter = new BufferedWriter(mFileWriter);
                mBufferWriter.write(mPaymentsJson);
                mBufferWriter.close();
                mFileWriter.close();
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent("success"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }


        } else {

            try {
                File mFile = null;
                String mInputLine = "";
                mFile = new File("/data/user/0/com.ecommerce.merabills/files/payments/lastPayment.txt");
                if (mFile.exists()) {
                    FileReader mFileReader = new FileReader(mFile.getAbsolutePath());
                    BufferedReader mBufferedReader = new BufferedReader(mFileReader);
                    while ((mInputLine = mBufferedReader.readLine()) != null) {
                        PaymentModel[] mPaymentArray = mGson.fromJson(mInputLine, PaymentModel[].class);
                        PaymentModel mPaymentModel = new PaymentModel();
                        mPaymentModel.setmPaymentModel(Arrays.asList(mPaymentArray));

                        mPaymentArray[0].getmTotalAmount();
                        mBufferedReader.close();

                        mDataForPayments = new Data.Builder();

                        ArrayList<String> mPaymentDetails = new ArrayList<>();

                        for (int i = 0; i < mPaymentArray.length; i++) {

                            mPaymentDetails.add(mPaymentArray[i].toString());

                            if (mPaymentArray[mPaymentArray.length - 1].getmTotalAmount() != "" &&
                                    mPaymentArray[mPaymentArray.length - 1].getmTotalAmount() != null
                            ) {
                                mPaymentDetails.add(mPaymentArray[i].getmTotalAmount());
                            }


                        }

                        Object[] mpaymentDetial = mPaymentDetails.toArray();

                        String[] mPaymentObjects = Arrays.copyOf(mpaymentDetial, mpaymentDetial.length, String[].class);
                        Intent mIntent = new Intent("mPaymentDetails");
                        mIntent.putExtra("paymentDetailsObject", mPaymentModel);
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(mIntent);

                        return Result.success();
                    }

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        return Result.success();
    }
}
