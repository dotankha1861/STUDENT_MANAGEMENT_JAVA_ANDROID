package com.example.studentmanagement.activities.customactivity;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.example.studentmanagement.R;
import com.example.studentmanagement.ui.CustomSearchView;

public class CustomAppCompactActivitySearchAdd extends CustomAppCompactActivity{
    SearchView searchView;
    MenuItem searchItem;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_create, menu);
        searchItem = menu.findItem(R.id.itSearch);
        searchView = (SearchView) searchItem.getActionView();
        CustomSearchView.customSearchView(this, searchView);
        return super.onCreateOptionsMenu(menu);
    }

    public SearchView getSearchView() {
        return searchView;
    }

    public MenuItem getSearchItem() {
        return searchItem;
    }
}
