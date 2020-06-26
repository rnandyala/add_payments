package com.ecommerce.merabills;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class PaymentModel  implements Parcelable {

    private List<PaymentModel> mPaymentModel;
    private double mPaymentAmount;
    private String mTransactionType;
    private String mTransactionDescriptiona;
    private String mTransactionDescriptionb;




    protected PaymentModel(Parcel in) {
        mPaymentAmount = in.readDouble();
        mTransactionType = in.readString();
        mTransactionDescriptiona = in.readString();
        mTransactionDescriptionb = in.readString();
        mTotalAmount = in.readString();
    }


    public static final Creator<PaymentModel> CREATOR = new Creator<PaymentModel>() {
        @Override
        public PaymentModel createFromParcel(Parcel in) {
            return new PaymentModel(in);
        }

        @Override
        public PaymentModel[] newArray(int size) {
            return new PaymentModel[size];
        }
    };

    public void setmTotalAmount(String mTotalAmount) {
        this.mTotalAmount = mTotalAmount;
    }

    private String mTotalAmount;

    public double getmPaymentAmount() {
        return mPaymentAmount;
    }

    public void setmPaymentAmount(double mPaymentAmount) {
        this.mPaymentAmount = mPaymentAmount;
    }

    public String getmTransactionType() {
        return mTransactionType;
    }

    public void setmTransactionType(String mTransactionType) {
        this.mTransactionType = mTransactionType;
    }

    public String getmTransactionDescriptiona() {
        return mTransactionDescriptiona;
    }

    public void setmTransactionDescriptiona(String mTransactionDescriptiona) {
        this.mTransactionDescriptiona = mTransactionDescriptiona;
    }

    public String getmTransactionDescriptionb() {
        return mTransactionDescriptionb;
    }

    public void setmTransactionDescriptionb(String mTransactionDescriptionb) {
        this.mTransactionDescriptionb = mTransactionDescriptionb;
    }


    public PaymentModel() {
    }
    public PaymentModel(double mPaymentAmount, String mTransactionType,
                        String mTransactionDescriptiona,
                        String mTransactionDescriptionb){
        this.mPaymentAmount = mPaymentAmount;
        this.mTransactionType = mTransactionType;
        this.mTransactionDescriptiona = mTransactionDescriptiona;
        this.mTransactionDescriptionb = mTransactionDescriptionb;
    }


    @NonNull
    @Override
    public String toString() {
        return mTransactionType+"=Rs. "+mPaymentAmount;
    }

    public String getmTotalAmount() {
        return mTotalAmount;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mPaymentModel);
        dest.writeDouble(mPaymentAmount);
        dest.writeString(mTransactionType);
        dest.writeString(mTransactionDescriptiona);
        dest.writeString(mTransactionDescriptionb);
        dest.writeString(mTotalAmount);
    }

    public void setmPaymentModel(List<PaymentModel> mPaymentModel) {
        this.mPaymentModel = mPaymentModel;
    }

    public  List<PaymentModel> getmPaymentModel(){
        return  mPaymentModel;
    }
}
