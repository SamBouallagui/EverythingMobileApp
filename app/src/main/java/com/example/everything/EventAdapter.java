package com.example.everything;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private Context context;
    private List<Event> eventList;
    public interface  OnEventClickListener{
        void onRsvpClick(Event event, int position);
        void onEventClick(Event event);
    }
    private OnEventClickListener listener;
    public EventAdapter(Context context, List<Event> eventList, OnEventClickListener listener){
        this.context =context;
        this.eventList=eventList;
        this.listener=listener;
    }
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view= LayoutInflater.from(context).inflate(R.layout.item_event,parent,false);
        return new EventViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.tvTitle.setText(event.getTitle());
        holder.tvDate.setText("📅 " + event.getDate());
        holder.tvTime.setText("🕐 " + event.getTime());
        holder.tvLocation.setText("📍 " + event.getLocation());
        holder.tvDescription.setText(event.getDescription());
        holder.tvAttendeeCount.setText(event.getAttendeeCount() + " attending");

        // change  button based on whether user already joined
        if (event.isRsvped()) {
            holder.btnRsvp.setText("Going ✓");
            holder.btnRsvp.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            android.graphics.Color.parseColor("#2A2A4A")));
        } else {
            holder.btnRsvp.setText("Join");
            holder.btnRsvp.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            android.graphics.Color.parseColor("#7B61FF")));
        }

        // open event detail when card is tapped
        holder.itemView.setOnClickListener(v ->
                listener.onEventClick(event));

        // handle join button tap
        holder.btnRsvp.setOnClickListener(v ->
                listener.onRsvpClick(event, holder.getAdapterPosition()));
    }
    public void updateItem(int position){
        notifyItemChanged(position);
    }
    @Override
    public int getItemCount(){
        return eventList.size();
    }
    public static class EventViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle, tvDate, tvTime, tvLocation, tvDescription, tvAttendeeCount;
        Button btnRsvp;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvEventTitle);
            tvDate = itemView.findViewById(R.id.tvEventDate);
            tvTime = itemView.findViewById(R.id.tvEventTime);
            tvLocation = itemView.findViewById(R.id.tvEventLocation);
            tvDescription = itemView.findViewById(R.id.tvEventDescription);
            tvAttendeeCount = itemView.findViewById(R.id.tvAttendeeCount);
            btnRsvp = itemView.findViewById(R.id.btnRsvp);
    }
}}
