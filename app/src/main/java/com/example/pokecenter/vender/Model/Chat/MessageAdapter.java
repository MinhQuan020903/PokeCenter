package com.example.pokecenter.vender.Model.Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokecenter.R;
import com.example.pokecenter.vender.Model.ChatRoom.ChatRoom;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Message> messages;
    private Context mContext;
    String currentId= FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",");
    public MessageAdapter(Context t,List<Message> messages) {
        mContext = t;
        this.messages = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 1)
            return new SentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_chat, null));
        else return new ReceiveViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.receive_chat, null));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if(holder.getClass()==SentViewHolder.class)
        {
            ((SentViewHolder) holder).sendMessage.setText(message.getMessageText());
            ((SentViewHolder) holder).timeSendMessage.setText(getLastMessageTimeStampFormat(message.getSendingTime()));
        }
        else {
            ((ReceiveViewHolder) holder).receiveMessage.setText(message.getMessageText());
            ((ReceiveViewHolder) holder).timeReceiveMessage.setText(getLastMessageTimeStampFormat(message.getSendingTime()));
        }


    }

    @Override
    public int getItemCount() {
        return messages == null ? 0 : messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if(currentId.equals(message.getSenderId()))
            return 1;
        else return 2;
    }

    public class SentViewHolder extends RecyclerView.ViewHolder {
        private TextView sendMessage;
        private TextView timeSendMessage;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            sendMessage = itemView.findViewById(R.id.txt_sent_message);
            timeSendMessage = itemView.findViewById(R.id.txt_sent_message_time);
        }
    }
    public class ReceiveViewHolder extends RecyclerView.ViewHolder {
        private TextView receiveMessage;
        private TextView timeReceiveMessage;
        public ReceiveViewHolder(@NonNull View itemView) {
            super(itemView);
            receiveMessage = itemView.findViewById(R.id.txt_receive_message);
            timeReceiveMessage = itemView.findViewById(R.id.txt_receive_message_time);
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
}

