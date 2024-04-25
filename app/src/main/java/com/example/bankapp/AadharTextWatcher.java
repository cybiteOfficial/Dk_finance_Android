package com.example.bankapp;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class AadharTextWatcher implements TextWatcher {
    private EditText editText;

    public AadharTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Not used
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Not used
    }

    @Override
    public void afterTextChanged(Editable s) {
        String text = s.toString().replaceAll("\\s", ""); // Remove any existing spaces
        if (text.length() > 0 && (text.length() % 5) == 0) {
            String formattedText = "";
            for (int i = 0; i < text.length(); i++) {
                if (i % 4 == 0 && i > 0) {
                    formattedText += " ";
                }
                formattedText += text.charAt(i);
            }
            editText.removeTextChangedListener(this);
            editText.setText(formattedText);
            editText.setSelection(formattedText.length());
            editText.addTextChangedListener(this);
        }
    }
}

