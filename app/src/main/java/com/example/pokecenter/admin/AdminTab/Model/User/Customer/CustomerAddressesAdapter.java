package com.example.pokecenter.admin.AdminTab.Model.User.Customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.Model.address.Address;

import java.util.ArrayList;

public class CustomerAddressesAdapter extends RecyclerView.Adapter<CustomerAddressesAdapter.ViewHolder> {
    private ArrayList<Address> addressList;
    private Context context;
    private int resource;

    public CustomerAddressesAdapter(ArrayList<Address> addressList, Context context, int resource) {
        this.addressList = addressList;
        this.context = context;
        this.resource = resource;
    }

    public void setAddressList(ArrayList<Address> addressList) {
        this.addressList = addressList;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvAddress2;
        private TextView tvNumberStreetAddress;
        private TextView tvReceiverName;
        private TextView tvReceiverPhoneNumber;
        private TextView tvType;
        private CheckBox cbIsDeliveryAddress;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAddress2 = itemView.findViewById(R.id.tvAddress2);
            tvNumberStreetAddress = itemView.findViewById(R.id.tvNumberStreetAddress);
            tvReceiverName = itemView.findViewById(R.id.tvReceiverName);
            tvReceiverPhoneNumber = itemView.findViewById(R.id.tvReceiverPhoneNumber);
            tvType = itemView.findViewById(R.id.tvType);
            cbIsDeliveryAddress = itemView.findViewById(R.id.cbIsDeliveryAddress);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(resource, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Address address = addressList.get(position);
        if (address != null) {
            try {
                holder.tvAddress2.setText(address.getAddress2());
                holder.tvReceiverName.setText(address.getReceiverName());
                holder.tvReceiverPhoneNumber.setText(address.getReceiverPhoneNumber());
                holder.tvNumberStreetAddress.setText(address.getNumberStreetAddress());
                holder.tvType.setText(address.getType());
                holder.cbIsDeliveryAddress.setChecked(address.getDeliveryAddress());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

}
