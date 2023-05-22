package com.example.studentmanagement.ui;

import android.content.Context;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.core.content.ContextCompat;

import com.example.studentmanagement.R;

public class CustomSearchView {
    public static void customSearchView(Context context, SearchView searchView) {
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Tìm kiếm...");
        searchView.setIconified(false);

        // change close Icon
        int closeButtonId = searchView.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = (ImageView) searchView.findViewById(closeButtonId);
        closeButton.setImageResource(R.drawable.baseline_close_24);

        //change Hint Icon
        int magId = context.getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView magImage = searchView.findViewById(magId);
        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));

        // change searchText
        int idSrc = context.getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText searchEditText = (EditText) searchView.findViewById(idSrc);
        searchEditText.setTextColor(ContextCompat.getColor(context, R.color.white));
        searchEditText.setHintTextColor(ContextCompat.getColor(context, R.color.third_color));
    }
}
