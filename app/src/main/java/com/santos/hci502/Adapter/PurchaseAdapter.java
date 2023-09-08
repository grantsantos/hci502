package com.santos.hci502.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.santos.hci502.Model.PurchaseHistoryModel;
import com.santos.hci502.R;
import com.santos.hci502.View.customer.PurchaseHistory;
import com.santos.hci502.View.customer.PurchaseIntent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.santos.hci502.Util.Constants.dateFormat;
import static com.santos.hci502.Util.Constants.dateFormatOnly;
import static com.santos.hci502.Util.Constants.dayFormat;
import static com.santos.hci502.Util.Constants.timeFormat12hr;

public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.PurchaseViewHolder> {
    Context context;
    ArrayList<PurchaseHistoryModel> inboxList;

    public PurchaseAdapter(Context context) {
        this.context = context;
        this.inboxList = new ArrayList<>();
    }

    public void addAll(List<PurchaseHistoryModel> newInbox) {
        int initSize = inboxList.size();
        inboxList.addAll(newInbox);
        notifyItemRangeChanged(initSize, newInbox.size());
    }

    public void removeAllItems() {
        inboxList.clear();
    }

    @NonNull
    @Override
    public PurchaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_purchase_history, parent, false);
        PurchaseViewHolder purchaseViewHolder = new PurchaseViewHolder(itemView, context, inboxList);
        return purchaseViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseAdapter.PurchaseViewHolder holder, int position) {
        Calendar thisYear = Calendar.getInstance();// Today's Year
        thisYear.getTimeInMillis();
        Calendar yesterdayCalendar = Calendar.getInstance(); //Today
        yesterdayCalendar.add(Calendar.DAY_OF_YEAR, -1);//
        yesterdayCalendar.getTimeInMillis();      //This becomes yesterday
        final long time = Long.parseLong(String.valueOf(inboxList.get(position).getTimeStamp())) * -1;
        Calendar timeStampFromDatabase = Calendar.getInstance();
        timeStampFromDatabase.setTimeInMillis(time);
        Date dateTobeFormatted = timeStampFromDatabase.getTime();
        String timeClock = timeFormat12hr.format(dateTobeFormatted);
        String date = dayFormat.format(dateTobeFormatted);
        if (date.equals(dayFormat.format(Calendar.getInstance().getTime()))) {
            date = "Today";

        } else if (yesterdayCalendar.get(Calendar.YEAR) == timeStampFromDatabase.get(Calendar.YEAR) &&
                yesterdayCalendar.get(Calendar.DAY_OF_YEAR) == timeStampFromDatabase.get(Calendar.DAY_OF_YEAR)) {
            date = "Yesterday";
        } else if (thisYear.get(Calendar.YEAR) == timeStampFromDatabase.get(Calendar.YEAR)) {
            date = dayFormat.format(dateTobeFormatted) + " " + dateFormatOnly.format(dateTobeFormatted);
        } else {
            date = dayFormat.format(dateTobeFormatted) + " " + dateFormat.format(dateTobeFormatted);
        }
        holder.tvDate.setText(date + "\n" + timeClock);
        holder.tvTotal.setText(inboxList.get(position).getPurchaseTotal());
    }


    @Override
    public int getItemCount() {
        return inboxList.size();
    }

    public class PurchaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvDate, tvTotal;
        ArrayList<PurchaseHistoryModel> inboxList = new ArrayList<>();
        Context ctx;


        public PurchaseViewHolder(View itemView, Context ctx, ArrayList<PurchaseHistoryModel> inboxList) {
            super(itemView);
            this.inboxList = inboxList;
            this.ctx = ctx;
            itemView.setOnClickListener(this);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTotal = itemView.findViewById(R.id.tvTotal);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            PurchaseHistoryModel inboxModel = this.inboxList.get(position);
            Intent intent = new Intent(this.ctx, PurchaseIntent.class);
            intent.putExtra("timeStamp", String.valueOf(inboxModel.getTimeStamp() * -1));
            intent.putExtra("date", tvDate.getText().toString());
            intent.putExtra("total", tvTotal.getText().toString());
            context.startActivity(intent);



        }
    }
}
