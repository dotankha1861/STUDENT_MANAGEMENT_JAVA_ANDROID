package com.example.studentmanagement.ui;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class TextWatcherWrapper implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Không cần triển khai
    }
    @Override
    public abstract void onTextChanged(CharSequence s, int start, int before, int count);

    @Override
    public void afterTextChanged(Editable s) {
        // Không cần triển khai
    }
}
