package com.ognjen.fleetforge.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ognjen.fleetforge.R;

public class ReportInconsistencyDialog extends Dialog {

    public interface OnReportSubmitListener {
        void onSubmit(String comment);
    }

    private final OnReportSubmitListener listener;

    private EditText commentEditText;
    private TextView charCountTextView;

    public ReportInconsistencyDialog(@NonNull Context context, OnReportSubmitListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_report_inconsistency);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        commentEditText = findViewById(R.id.et_comment);
        charCountTextView = findViewById(R.id.tv_char_count);
    }

    private void setupListeners() {
        // Character counter
        commentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                charCountTextView.setText(s.length() + "/500");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Close button (X)
        ImageButton btnClose = findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> dismiss());

        // Close button (text)
        Button btnCloseDialog = findViewById(R.id.btn_close_dialog);
        btnCloseDialog.setOnClickListener(v -> dismiss());

        // Submit button
        Button btnSubmit = findViewById(R.id.btn_submit_report);
        btnSubmit.setOnClickListener(v -> {
            if (validateInput()) {
                String comment = commentEditText.getText().toString().trim();
                if (listener != null) {
                    listener.onSubmit(comment);
                }
                dismiss();
            }
        });
    }

    private boolean validateInput() {
        String comment = commentEditText.getText().toString().trim();

        if (comment.isEmpty()) {
            Toast.makeText(getContext(), "Please describe the issue", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (comment.length() > 500) {
            Toast.makeText(getContext(), "Comment must not exceed 500 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}

