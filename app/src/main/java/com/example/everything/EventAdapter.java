package com.example.everything;
import com.example.everything.utils.AnimationUtils;

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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
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
        this.eventList=new ArrayList<>(eventList);
        this.listener=listener;
        
        SharedPreferences prefs = context.getSharedPreferences("EverythingSession", Context.MODE_PRIVATE);
        currentUserId = String.valueOf(prefs.getInt("userId", 0));
    }
    
    public void updateCurrentUserRole(String role) {
        this.currentUserRole = role;
        notifyDataSetChanged();
    }
    
    public void updateList(List<Event> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() { return eventList.size(); }
            
            @Override
            public int getNewListSize() { return newList.size(); }
            
            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return eventList.get(oldItemPosition).getId().equals(newList.get(newItemPosition).getId());
            }
            
            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                Event oldE = eventList.get(oldItemPosition);
                Event newE = newList.get(newItemPosition);
                return oldE.isRsvped() == newE.isRsvped() &&
                       oldE.getAttendeeCount() == newE.getAttendeeCount() &&
                       oldE.getTitle().equals(newE.getTitle());
            }
        });
        this.eventList.clear();
        this.eventList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
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
        holder.tvAuthor.setText("Organized by " + event.getAuthorName());
        holder.tvDate.setText(event.getDate() != null && !event.getDate().isEmpty() ? event.getDate() : "TBD");
        holder.tvTime.setText(event.getTime() != null && !event.getTime().isEmpty() ? event.getTime() : "TBD");
        holder.tvLocation.setText(event.getLocation() != null && !event.getLocation().isEmpty() ? event.getLocation() : "Location TBD");
        holder.tvDescription.setText(event.getDescription());
        
        int count = event.getAttendeeCount();
        holder.tvAttendeeCount.setText(count == 1 ? "1 attending" : count + " attending");

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
                            android.graphics.Color.parseColor("#1E1B4B")));
            holder.btnRsvp.setTextColor(android.graphics.Color.parseColor("#A78BFA"));
        } else {
            holder.btnRsvp.setText("Join");
            holder.btnRsvp.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            android.graphics.Color.parseColor("#7C3AED")));
            holder.btnRsvp.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
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
        
        AnimationUtils.addBounceEffect(holder.itemView);
        AnimationUtils.addBounceEffect(holder.btnRsvp);
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
