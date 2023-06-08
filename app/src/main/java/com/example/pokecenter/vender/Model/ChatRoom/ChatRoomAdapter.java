package com.example.pokecenter.vender.Model.ChatRoom;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.Model.account.Account;
import com.example.pokecenter.customer.lam.Model.pokemon.Pokemon;
import com.example.pokecenter.vender.API.FirebaseSupportVender;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder> {
    Account currentAccount;
    private Context mContext;
    private ArrayList<ChatRoom> list = new ArrayList<>();

    private final ChatRoomInterface chatRoomInterface;

    public ChatRoomAdapter(Context context, ChatRoomInterface chatRoomInterface) {
        this.mContext  = context;
        this.chatRoomInterface = chatRoomInterface;
    }
    
    String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",");
    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inbox_item, parent, false);
        return new ChatRoomViewHolder(view, chatRoomInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomViewHolder holder, int position) {
        ChatRoom ib = list.get(position);
        if (ib == null) {
            return;
        }
        if ( ib.getLastMessage()==null){
            ib.setLastMessage("Các bạn đã được kết nối với nhau.");
        }
        else if(currentUserUID.equals(ib.getSenderId())) {
            String tt = ib.getLastMessage();
            ib.setLastMessage( "You: " + tt);
        }
            holder.tvRecipientName.setText(ib.getSenderAccount().getUsername());
            holder.tvLastMessage.setText(ib.getLastMessage());
            Picasso.get().load(ib.getSenderAccount().getAvatar()).into(holder.siImage);
            holder.tvLastMessageTimestamp.setText(getLastMessageTimeStampFormat(ib.getLastMessageTimeStamp()));
    }
    public void addData(ArrayList<ChatRoom> t) {
        list.clear();
        t.sort(Comparator.comparing(ChatRoom::getLastMessageTimeStamp).reversed());
        list.addAll(t);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ChatRoomViewHolder extends RecyclerView.ViewHolder{
        private ShapeableImageView siImage;
        private TextView tvRecipientName, tvLastMessage, tvLastMessageTimestamp;

        public ChatRoomViewHolder(@NonNull View itemView, ChatRoomInterface chatRoomInterface) {
            super(itemView);
            siImage = itemView.findViewById(R.id.profileImageInChatItem);
            tvRecipientName = itemView.findViewById(R.id.tvRecipientName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvLastMessageTimestamp = itemView.findViewById(R.id.tvLastMessageTimestamp);
            // implement button click and pass value to new fragment through navController bundle
            itemView.setOnClickListener(view -> {
                if (chatRoomInterface != null) {
                    int pos = getAbsoluteAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        chatRoomInterface.onChatRoomItemClick(list.get(pos));
                    }
                }
            });
        }
    }

    public String getLastMessageTimeStampFormat(long timeStamp) {
        Calendar calendar = Calendar.getInstance();

        int today = calendar.get(Calendar.DAY_OF_MONTH);
        int thisMonth = calendar.get(Calendar.MONTH);
        int thisYear = calendar.get(Calendar.YEAR);

        calendar.setTimeInMillis(timeStamp);

        int messageDay = calendar.get(Calendar.DAY_OF_MONTH);
        int messageMonth = calendar.get(Calendar.MONTH);
        int messageYear = calendar.get(Calendar.YEAR);

        String formattedTime;
        if (today == messageDay && thisMonth == messageMonth && thisYear == messageYear) {
            //Today
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            formattedTime = dateFormat.format(new Date(timeStamp));
        } else if (thisMonth == messageMonth && thisYear == messageYear) {
            // this month
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
            formattedTime = dateFormat.format(new Date(timeStamp));
        } else {
            // This year
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            formattedTime = dateFormat.format(new Date(timeStamp));
        }
        return formattedTime;
    }
    private void fetchingAndSetupData(String id) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            Account fetchedAccountInfo = null;
            boolean isSuccessful = true;

            try {
                currentAccount = new FirebaseSupportVender().fetchingCurrentAccount(id);
            } catch (IOException e) {
                isSuccessful = false;
            }
        });
    }
}

