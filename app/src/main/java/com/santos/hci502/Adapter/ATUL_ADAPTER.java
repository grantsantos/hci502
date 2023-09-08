package com.santos.hci502.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.santos.hci502.Model.ATUL_MODEL;
import com.santos.hci502.R;
import com.santos.hci502.Util.CircleTransform;
import com.santos.hci502.View.admin.activity.TopUpIntentActivity;
import com.santos.hci502.View.admin.fragment.AdminTopUpListFragment;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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

public class ATUL_ADAPTER extends RecyclerView.Adapter<ATUL_ADAPTER.ATUL__VIEWHOLDER> {

    ArrayList<ATUL_MODEL> inboxList;
    Context context;

    public ATUL_ADAPTER(Context context) {
        this.inboxList = new ArrayList<>();
        this.context = context;
    }

    public void addAll(List<ATUL_MODEL> newInbox) {
        int initSize = inboxList.size();
        inboxList.addAll(newInbox);
        notifyItemRangeChanged(initSize, newInbox.size());
    }

    public void removeAllItems(){
        inboxList.clear();
    }

    @NonNull
    @Override
    public ATUL__VIEWHOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_atul, parent, false);
        ATUL__VIEWHOLDER atul__viewholder = new ATUL__VIEWHOLDER(itemView, context, inboxList);
        return atul__viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull ATUL__VIEWHOLDER holder, int position) {
        Calendar thisYear = Calendar.getInstance();// Today's Year
        thisYear.getTimeInMillis();
        Calendar yesterdayCalendar = Calendar.getInstance(); //Today
        yesterdayCalendar.add(Calendar.DAY_OF_YEAR, -1);//
        yesterdayCalendar.getTimeInMillis();      //This becomes yesterday
        final long time = Long.parseLong(String.valueOf(inboxList.get(position).getTimeStamp()));
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


        holder.tvDate.setText(date);
        holder.tvTime.setText(timeClock);
        holder.tvName.setText(inboxList.get(position).getName());
        holder.tvAmount.setText("Top up amount: " + inboxList.get(position).getTopUpValue());

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                holder.ivProfPic.setVisibility(View.VISIBLE);
                holder.ivProfPic.setImageBitmap(bitmap);
                //progressBarProfPic.setVisibility(View.GONE);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                //progressBar.setVisibility(View.VISIBLE);
            }
        };
        holder.ivProfPic.setTag(target);
        Picasso.get().load(inboxList.get(position).getProfilePicUrl()).transform(new CircleTransform()).into(target);


    }

    @Override
    public int getItemCount() {
        return inboxList.size();
    }

    public class ATUL__VIEWHOLDER extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvName, tvAmount, tvDate, tvTime;
        ImageView ivProfPic;
        ArrayList<ATUL_MODEL> inboxList = new ArrayList<>();
        Context ctx;

        public ATUL__VIEWHOLDER(View itemView, Context ctx, ArrayList<ATUL_MODEL> inboxList) {
            super(itemView);
            this.inboxList = inboxList;
            this.ctx = ctx;
            itemView.setOnClickListener(this);

            tvName = itemView.findViewById(R.id.tvName);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivProfPic = itemView.findViewById(R.id.ivProfilePic);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            ATUL_MODEL inboxModel = this.inboxList.get(position);
            Intent intent = new Intent(this.ctx, TopUpIntentActivity.class);
            intent.putExtra("date", tvDate.getText().toString());
            intent.putExtra("time", tvTime.getText().toString());
            intent.putExtra("pic", inboxList.get(position).getProfilePicUrl());
            intent.putExtra("name", inboxList.get(position).getName());
            intent.putExtra("amount", inboxList.get(position).getTopUpValue());
            intent.putExtra("uid", inboxList.get(position).getUserUid());
            this.ctx.startActivity(intent);
            //((Activity)ctx).finish();

        }
    }
}
