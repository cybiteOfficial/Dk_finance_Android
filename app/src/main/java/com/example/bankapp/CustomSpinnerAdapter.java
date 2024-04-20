package com.example.bankapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private List<String> mValues;
    private List<Integer> mIcons;

    public CustomSpinnerAdapter(Context context, List<String> values, List<Integer> icons) {
        super(context, 0, values);
        mContext = context;
        mValues = values;
        mIcons = icons;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        View spinnerView = convertView;
        if (spinnerView == null) {
            spinnerView = LayoutInflater.from(mContext).inflate(R.layout.spinner_item_layout, parent, false);
        }

        TextView textView = spinnerView.findViewById(R.id.spinner_item_text);
        textView.setText(mValues.get(position));

        ImageView iconView = spinnerView.findViewById(R.id.spinner_item_icon);
        iconView.setImageResource(mIcons.get(position));

        return spinnerView;
    }
}
