package com.example.pokecenter.admin.AdminTab.Model.AdminSupportTicket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.example.pokecenter.admin.AdminTab.Model.AdminRequest.AdminRequest;
import com.example.pokecenter.admin.AdminTab.Model.AdminRequest.AdminRequestAdapter;
import com.example.pokecenter.admin.AdminTab.Utils.OnItemClickListener;

import java.util.ArrayList;

public class AdminSupportTicketAdapter extends RecyclerView.Adapter<AdminSupportTicketAdapter.ViewHolder>{

    private ArrayList<AdminSupportTicket> supportTickets;
    private Context context;
    private int resource;
    private OnItemClickListener<AdminSupportTicket> onItemClickListener;

    public AdminSupportTicketAdapter(ArrayList<AdminSupportTicket> supportTickets, Context context, int resource) {
        this.supportTickets = supportTickets;
        this.context = context;
        this.resource = resource;
    }

    public void setSupportTickets(ArrayList<AdminSupportTicket> supportTickets) {
        this.supportTickets = supportTickets;
    }

    public void setOnItemClickListener(OnItemClickListener<AdminSupportTicket> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvSupportTicketProblemName;
        private TextView tvSupportTicketId;
        private TextView tvCustomerId;
        private TextView tvCreateDate;
        private RadioButton rbSupportResolved;
        private RadioButton rbSupportNotResolved;
        private RadioButton rbSupportPending;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSupportTicketProblemName = itemView.findViewById(R.id.tvSupportTicketProblemName);
            tvSupportTicketId = itemView.findViewById(R.id.tvSupportTicketId);
            tvCustomerId = itemView.findViewById(R.id.tvCustomerId);
            tvCreateDate = itemView.findViewById(R.id.tvCreateDate);
            rbSupportNotResolved = itemView.findViewById(R.id.rbSupportNotResolved);
            rbSupportResolved = itemView.findViewById(R.id.rbSupportResolved);
            rbSupportPending = itemView.findViewById(R.id.rbSupportPending);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        int position = getBindingAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(supportTickets.get(position), position);
                        }
                    }
                }
            });
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
        AdminSupportTicket supportTicket = supportTickets.get(position);
        if (supportTicket != null) {
            try {
                holder.tvSupportTicketProblemName.setText(supportTicket.getProblemName());
                holder.tvSupportTicketId.setText(supportTicket.getId());
                if (supportTicket.getCustomerId().contains(",")) {
                    holder.tvCustomerId.setText(supportTicket.getCustomerId().replace(",", "."));
                } else {
                    holder.tvCustomerId.setText(supportTicket.getCustomerId());
                }

                holder.tvCreateDate.setText(supportTicket.getCreateDate());
                switch (supportTicket.getStatus()) {
                    case "Not resolved" : {
                        holder.rbSupportNotResolved.setChecked(true);
                        break;
                    }
                    case "Resolved" : {
                        holder.rbSupportResolved.setChecked(true);
                        break;
                    }
                    case "Pending" : {
                        holder.rbSupportPending.setChecked(true);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return supportTickets.size();
    }

}
