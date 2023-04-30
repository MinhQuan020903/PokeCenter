package com.example.pokecenter.customer.lam.Model.address;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pokecenter.R;

import org.w3c.dom.Text;

import java.util.List;

public class AddressAdapter extends ArrayAdapter<Address> {

    private Context mContext;

    private List<Address> mAddresses;

    public AddressAdapter(Context context, List<Address> addresses) {
        super(context, R.layout.lam_customer_address_item, addresses);
        mContext = context;
        mAddresses = addresses;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.lam_customer_address_item, null);

        Address address = mAddresses.get(position);

        TextView receiverName = view.findViewById(R.id.receiverName);
        TextView phoneNumber = view.findViewById(R.id.phoneNumber);
        TextView numberStreetAddress = view.findViewById(R.id.number_street_address);
        TextView address_2 = view.findViewById(R.id.address_2);
        TextView isDeliveryAddress = view.findViewById(R.id.isDeliveryAddress);

        receiverName.setText(address.getReceiverName());
        phoneNumber.setText(address.getReceiverPhoneNumber());
        numberStreetAddress.setText(address.getNumberStreetAddress());
        address_2.setText(address.getAddress2());

        isDeliveryAddress.setVisibility(address.getDeliveryAddress() ? View.VISIBLE : View.GONE);

        return view;
    }
}
