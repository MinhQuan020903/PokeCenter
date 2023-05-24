package com.example.pokecenter.customer.lam.Model.option;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pokecenter.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OptionAdapter extends ArrayAdapter<Option> {

    private Context mContext;
    private List<Option> mOptions;
    private String defaultImage;
    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public OptionAdapter(Context context, List<Option> options, String defaultImage) {
        super(context, R.layout.lam_option_list_item, options);
        mContext = context;
        mOptions = options;
        this.defaultImage = defaultImage;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.lam_option_list_item, null);

        Option option = mOptions.get(position);

        ImageView optionImage = view.findViewById(R.id.option_image);
        TextView optionName = view.findViewById(R.id.option_name);
        TextView optionPrice = view.findViewById(R.id.option_price);

        if (option.getOptionImage().isEmpty()) {
            Picasso.get().load(defaultImage).into(optionImage);
        } else {
            Picasso.get().load(option.getOptionImage()).into(optionImage);
        }

        optionName.setText(option.getOptionName());
        optionPrice.setText(currencyFormatter.format(option.getPrice()));

        if (option.getCurrentQuantity() == 0) {
            optionName.setTextColor(Color.parseColor("#E3E3E3"));
            optionPrice.setTextColor(Color.parseColor("#E3E3E3"));

        } else {
            optionName.setTextColor(Color.parseColor("#000000"));
            optionPrice.setTextColor(Color.parseColor("#027B96"));
            view.setClickable(false);
        }

        return view;
    }

}
