package com.example.everything;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.everything.models.api.EventDto;
import com.example.everything.models.api.MemberDto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommunityEventsFragment extends Fragment {
    private EventAdapter adapter;
    private List<Event> eventList = new ArrayList<>();
    private String communityId;
    private String currentUserId;
    private boolean isAdmin = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_events, container, false);
        RecyclerView rvEvents = view.findViewById(R.id.rvEvents);
        FloatingActionButton fabCreateEvent = view.findViewById(R.id.fabCreatePost);
        rvEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Get community ID from arguments
        communityId = getArguments() != null ? getArguments().getString("communityId") : "0";
        
        // Get current user ID
        SharedPreferences prefs = getContext().getSharedPreferences("EverythingSession", getContext().MODE_PRIVATE);
        currentUserId = String.valueOf(prefs.getInt("userId", 0));
        
        adapter = new EventAdapter(getContext(), eventList,
                new EventAdapter.OnEventClickListener() {

                    @Override
                    public void onRsvpClick(Event event, int position) {
                        toggleEventParticipation(event, position);
                    }

                    @Override
                    public void onEventClick(Event event) {
                        Toast.makeText(getContext(),
                                event.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                    
                    @Override
                    public void onDeleteClick(Event event, int position) {
                        deleteEvent(event, position);
                    }
                });
        rvEvents.setAdapter(adapter);
        
        loadEventsFromApi();
        
        checkAdminRole();
        
        // Show FAB only for admins
        fabCreateEvent.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        fabCreateEvent.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateEventActivity.class);
            intent.putExtra("communityId", communityId);
            intent.putExtra("communityName", getArguments() != null ? getArguments().getString("communityName") : "Community");
            startActivityForResult(intent, 101);
        });

        return view;
    }
    
    private void checkAdminRole() {
        if (communityId == null || communityId.equals("0")) {
            isAdmin = false;
            return;
        }
        
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int communityIdInt = Integer.parseInt(communityId);
        
        apiService.getCommunityMembers(communityIdInt).enqueue(new Callback<List<MemberDto>>() {
            @Override
            public void onResponse(Call<List<MemberDto>> call, Response<List<MemberDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MemberDto> members = response.body();
                    
                    // Find current user and check if they're admin
                    for (MemberDto member : members) {
                        if (member.getUserId() == Integer.parseInt(currentUserId)) {
                            isAdmin = "Admin".equals(member.getRole());
                            break;
                        }
                    }
                    
                    if (adapter != null) {
                        String userRole = isAdmin ? "admin" : "member";
                        adapter.updateCurrentUserRole(userRole);
                    }
                    
                    if (getView() != null) {
                        FloatingActionButton fab = getView().findViewById(R.id.fabCreatePost);
                        if (fab != null) {
                            fab.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
                        }
                    }
                } else {
                    isAdmin = false;
                }
            }

            @Override
            public void onFailure(Call<List<MemberDto>> call, Throwable t) {
                isAdmin = false;
            }
        });
    }
    
    private void loadEventsFromApi() {
        if (communityId == null || communityId.equals("0")) {
            Toast.makeText(getContext(), "Invalid community ID", Toast.LENGTH_SHORT).show();
            return;
        }
        
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int communityIdInt = Integer.parseInt(communityId);
        
        apiService.getCommunityEvents(communityIdInt).enqueue(new Callback<List<EventDto>>() {
            @Override
            public void onResponse(Call<List<EventDto>> call, Response<List<EventDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EventDto> events = response.body();
                    
                    eventList.clear();
                    for (EventDto dto : events) {
                        Event event = new Event(dto);
                        eventList.add(event);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to load events", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<List<EventDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
    
    private void toggleEventParticipation(Event event, int position) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int eventId = Integer.parseInt(event.getId());
        
        apiService.participateInEvent(eventId).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    boolean wasParticipating = event.isRsvped();
                    event.setRsvped(!wasParticipating);
                    event.setAttendeeCount(wasParticipating ? event.getAttendeeCount() - 1 : event.getAttendeeCount() + 1);
                    adapter.updateItem(position);
                    
                    String message = wasParticipating ? "Cancelled RSVP for " + event.getTitle() : "You're going to " + event.getTitle();
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                } else {
                    if (response.code() == 401) {
                        Toast.makeText(getContext(), "Please login to RSVP", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to toggle RSVP", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void deleteEvent(Event event, int position) {
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    ApiService apiService = ApiClient.getClient().create(ApiService.class);
                    int eventId = Integer.parseInt(event.getId());
                    
                    apiService.deleteEvent(eventId).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful()) {
                                eventList.remove(position);
                                adapter.notifyItemRemoved(position);
                                Toast.makeText(getContext(), "Event deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Failed to delete event", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    public void refreshEvents() {
        loadEventsFromApi();
    }
    


}