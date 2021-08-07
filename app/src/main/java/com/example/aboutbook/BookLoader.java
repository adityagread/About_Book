package com.example.aboutbook;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.io.IOException;

public class BookLoader extends AsyncTaskLoader<String> {
    private String mQueryString;

    public BookLoader(@NonNull Context context) {
        super(context);
    }

    public BookLoader(Context context, String queryString){
        super(context);
        mQueryString = queryString;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
    }

    @Nullable
    @Override
    public String loadInBackground() {
        try {
            return NetworkUtils.getBookInfo(mQueryString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
