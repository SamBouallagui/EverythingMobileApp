package com.example.everything;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private Context context;
    private List<Event> eventList;
    private String currentUserId;
    private String currentUserRole = "member";
    public interface  OnEventClickListener{
        void onRsvpClick(Event event, int position);
        void onEventClick(Event event);
        void onDeleteClick(Event event, int position);
    }
    private OnEventClickListener listener;
    public EventAdapter(Context context, List<Event> eventList, OnEventClickListener listener){
        this.context=context;
        this.eventList=eventList;
        this.listener=listener;
        
        SharedPreferences prefs = context.getSharedPreferences("EverythingSession", Context.MODE_PRIVATE);
        currentUserId = String.valueOf(prefs.getInt("userId", 0));
    }
    
    public void updateCurrentUserRole(String role) {
        this.currentUserRole = role;
        notifyDataSetChanged();
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
        holder.tvAuthor.setText("Created by " + event.getAuthorName());
        holder.tvDate.setText("📅 " + event.getDate());
        holder.tvTime.setText("🕐 " + event.getTime());
        holder.tvLocation.setText("📍 " + event.getLocation());
        holder.tvDescription.setText(event.getDescription());
        holder.tvAttendeeCount.setText(event.getAttendeeCount() + " attending");

        // Show delete button only for event creator
        boolean isCreator = currentUserId.equals(event.getAuthorId());
        
        // Only admins can delete events
        boolean isAdmin = currentUserRole.equals("admin");
        boolean canDelete = isAdmin;
        
        holder.btnDeleteEvent.setVisibility(canDelete ? View.VISIBLE : View.GONE);

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
        
        // handle delete button tap
        holder.btnDeleteEvent.setOnClickListener(v -> {
            listener.onDeleteClick(event, holder.getAdapterPosition());
        });
    }
    public void updateItem(int position){
        notifyItemChanged(position);
    }
    @Override
    public int getItemCount(){
        return eventList.size();
    }
    public static class EventViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle, tvAuthor, tvDate, tvTime, tvLocation, tvDescription, tvAttendeeCount;
        Button btnRsvp;
        ImageButton btnDeleteEvent;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvEventTitle);
            tvAuthor = itemView.findViewById(R.id.tvEventAuthor);
            tvDate = itemView.findViewById(R.id.tvEventDate);
            tvTime = itemView.findViewById(R.id.tvEventTime);
            tvLocation = itemView.findViewById(R.id.tvEventLocation);
            tvDescription = itemView.findViewById(R.id.tvEventDescription);
            tvAttendeeCount = itemView.findViewById(R.id.tvAttendeeCount);
            btnRsvp = itemView.findViewById(R.id.btnRsvp);
            btnDeleteEvent = itemView.findViewById(R.id.btnDeleteEvent);
        }
}}
