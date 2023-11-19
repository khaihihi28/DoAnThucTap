package com.example.do_an_thuc_tap_main.ui.oders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OdersViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public OdersViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is orders fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}