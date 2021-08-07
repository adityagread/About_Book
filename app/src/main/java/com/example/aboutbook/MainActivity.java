package com.example.aboutbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    private EditText mbookinput;
    private TextView mBookTitle, mAuthorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //connecting with views
        mbookinput = (EditText) findViewById(R.id.BookInput);
        mBookTitle = (TextView) findViewById(R.id.book_title);
        mAuthorName = (TextView) findViewById(R.id.author_name);

        // Reconnecting loader
        if (getSupportLoaderManager().getLoader(0) != null) {
            getSupportLoaderManager().initLoader(0, null, this);
        }
    }

    public void searchBooks(View view) {
        //making string of book detail
        String BookDetail = mbookinput.getText().toString();

        InputMethodManager inputMethodManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();

        }
        if (networkInfo != null && networkInfo.isConnected() && BookDetail.length() != 0) {
            Bundle queryBundle = new Bundle();
            queryBundle.putString("queryString", BookDetail);
            getSupportLoaderManager().restartLoader(0, queryBundle, this);
            //new FetchBook(mBookTitle, mAuthorName).execute(BookDetail);
            mAuthorName.setText("");
            mBookTitle.setText(R.string.loading);
        } else {
            if (BookDetail.length() == 0) {
                mAuthorName.setText("");
                mBookTitle.setText(R.string.no_search_term);
            } else {
                mAuthorName.setText("");
                mBookTitle.setText(R.string.no_network);
            }
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        String queryString = "";

        if (args != null) {
            queryString = args.getString("queryString");
        }

        return new BookLoader(this, queryString);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray itemArray = jsonObject.getJSONArray("items");
            int i = 0;
            String title = null, authors = null;

            while (i < itemArray.length() && (authors == null) && title == null) {
                JSONObject book = itemArray.getJSONObject(i);
                JSONObject volumeinfo = book.getJSONObject("volumeInfo");

                try {
                    title = volumeinfo.getString("title");
                    authors = volumeinfo.getString("authors");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                i++;

                if (title != null && authors != null) {
                    mBookTitle.setText(title);
                    mAuthorName.setText(authors);
                } else {
                    mBookTitle.setText(R.string.no_results);
                    mAuthorName.setText("");
                }
            }
        } catch (JSONException e) {
            mBookTitle.setText(R.string.no_results);
            mAuthorName.setText("");
            e.printStackTrace();

        }


    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}