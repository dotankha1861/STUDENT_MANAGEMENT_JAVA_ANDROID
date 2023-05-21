package com.example.studentmanagement.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.studentmanagement.R;

import java.util.function.Predicate;


//Builder pattern
public class CustomDialog {
    private final Dialog dialog;

    public Dialog getDialog() {
        return dialog;
    }

    private CustomDialog(Dialog dialog) {
        this.dialog = dialog;
    }
    public void show() {
        dialog.show();
    }
    public void dismiss() {
        dialog.dismiss();
    }

    public static class Builder {
        private final Dialog dialog;
        private ImageView ivDialog;
        private TextView tvMessage;
        private Button btnPositive;
        private Button btnNegative;

        private EditText editText;

        public Builder(Context context) {
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.layout_dialog);
            dialog.setCancelable(false);
        }

        public Builder setImage(int resId) {
            ivDialog = dialog.findViewById(R.id.iv_dialog);
            ivDialog.setImageResource(resId);
            return this;
        }

        public Builder setMessage(String message) {
            tvMessage = dialog.findViewById(R.id.tv_message);
            tvMessage.setText(message);
            return this;
        }
        public Builder setEditText(String hint, String text){
            editText = dialog.findViewById(R.id.editText);
            editText.setText(text);
            editText.setHint(hint);
            return this;
        }

        public Builder setPositiveButton(String text, View.OnClickListener listener, Predicate<Boolean> hasDismiss) {
            btnPositive = dialog.findViewById(R.id.btn_positive);
            btnPositive.setText(text);
            btnPositive.setOnClickListener(v->{
                if(listener!=null) listener.onClick(v);
                if(hasDismiss.test(true)) dialog.dismiss();
            });
            return this;
        }

        public Builder setNegativeButton(String text, View.OnClickListener listener, Predicate<Boolean> hasDismiss) {
            btnNegative = dialog.findViewById(R.id.btn_negative);
            btnNegative.setText(text);
            btnNegative.setOnClickListener(v -> {
                if (listener!= null) listener.onClick(v);
                if(hasDismiss.test(true)) dialog.dismiss();
            });
            return this;
        }

        public CustomDialog build() {
            if(ivDialog == null) dialog.findViewById(R.id.iv_dialog).setVisibility(View.GONE);
            if(tvMessage == null) dialog.findViewById(R.id.tv_message).setVisibility(View.GONE);
            if(btnPositive == null) dialog.findViewById(R.id.btn_positive).setVisibility(View.GONE);
            if(btnNegative == null) dialog.findViewById(R.id.btn_negative).setVisibility(View.GONE);
            if(editText == null) dialog.findViewById(R.id.editText).setVisibility(View.GONE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(1400, 1200);
            return new CustomDialog(dialog);
        }
    }
}

