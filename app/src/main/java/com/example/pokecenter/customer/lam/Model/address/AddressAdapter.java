package com.example.pokecenter.customer.lam.Model.address;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.Interface.AddressRecyclerViewInterface;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private Context mContext;

    private List<Address> mAddresses;

    private final AddressRecyclerViewInterface addressRecyclerViewInterface;

    public AddressAdapter(Context context, List<Address> addresses, AddressRecyclerViewInterface addressRecyclerViewInterface) {
        mContext = context;
        mAddresses = addresses;
        this.addressRecyclerViewInterface = addressRecyclerViewInterface;
    }

    public void setData(List<Address> addresses) {
        mAddresses = addresses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lam_customer_address_item, parent, false);
        return new AddressViewHolder(view, addressRecyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {

        Address address = mAddresses.get(position);

        holder.receiverName.setText(address.getReceiverName());
        holder.phoneNumber.setText(address.getReceiverPhoneNumber());
        holder.numberStreetAddress.setText(address.getNumberStreetAddress());
        holder.address_2.setText(address.getAddress2());
        holder.isDeliveryAddress.setVisibility(address.getDeliveryAddress() ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        if (mAddresses == null ) {
            return 0;
        }
        return mAddresses.size();
    }

    public class AddressViewHolder  extends RecyclerView.ViewHolder {

        private TextView receiverName;
        private TextView phoneNumber;
        private TextView numberStreetAddress;
        private TextView address_2;
        private TextView isDeliveryAddress;
        private ImageButton deleteButton;

        public AddressViewHolder(@NonNull View itemView, AddressRecyclerViewInterface addressRecyclerViewInterface) {
            super(itemView);

            receiverName = itemView.findViewById(R.id.receiverName);
            phoneNumber = itemView.findViewById(R.id.phoneNumber);
            numberStreetAddress = itemView.findViewById(R.id.number_street_address);
            address_2 = itemView.findViewById(R.id.address_2);
            isDeliveryAddress = itemView.findViewById(R.id.isDeliveryAddress);
            deleteButton = itemView.findViewById(R.id.delete_button);

            deleteButton.setOnClickListener(view -> {

                if (addressRecyclerViewInterface != null) {
                    int pos = getAbsoluteAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        addressRecyclerViewInterface.onAddressDeleteButtonClick(pos);
                    }
                }

            });

            itemView.setOnClickListener(view -> {
                if (addressRecyclerViewInterface != null) {
                    int pos = getAbsoluteAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        addressRecyclerViewInterface.onAddressItemClick(pos);
                    }
                }
            });
        }
    }
}
