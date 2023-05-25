package com.example.pokecenter.customer.lam.Model.address;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.Model.option.Option;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AddressArrayAdapter  extends ArrayAdapter<Address> {

    private Context mContext;
    private List<Address> mAddresses;

    public AddressArrayAdapter(Context context, List<Address> addresses) {
        super(context, R.layout.lam_customer_address_item, addresses);

        mContext = context;
        mAddresses = addresses;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.lam_customer_address_item, null);

        TextView receiverName = view.findViewById(R.id.receiverName);
        TextView phoneNumber = view.findViewById(R.id.phoneNumber);
        TextView numberStreetAddress = view.findViewById(R.id.number_street_address);
        TextView address_2 = view.findViewById(R.id.address_2);
        TextView isDeliveryAddress = view.findViewById(R.id.isDeliveryAddress);
        ImageButton deleteButton = view.findViewById(R.id.delete_button);

        Address address = mAddresses.get(position);

        receiverName.setText(address.getReceiverName());
        phoneNumber.setText(address.getReceiverPhoneNumber());
        numberStreetAddress.setText(address.getNumberStreetAddress());
        address_2.setText(address.getAddress2());
        receiverName.setText(address.getReceiverName());

        if (!address.getDeliveryAddress()) {
            isDeliveryAddress.setVisibility(View.GONE);
        }

        deleteButton.setVisibility(View.GONE);
        return view;
    }
}
