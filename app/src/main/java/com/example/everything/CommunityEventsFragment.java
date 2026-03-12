package com.example.everything;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CommunityEventsFragment extends Fragment {
    private EventAdapter adapter;
    private List<Event> eventList=new ArrayList<>();
    private String currentUserRole="admin";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view =inflater.inflate(R.layout.fragment_community_events,container,false);
        RecyclerView rvEvents=view.findViewById(R.id.rvEvents);
        FloatingActionButton fabCreateEvent=view.findViewById(R.id.fabCreatePost);
        rvEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        loadFakeEvents();
        adapter = new EventAdapter(getContext(), eventList,
                new EventAdapter.OnEventClickListener() {

                    @Override
                    public void onRsvpClick(Event event, int position) {
                        if (event.isRsvped()) {
                            event.setRsvped(false);
                            event.setAttendeeCount(event.getAttendeeCount() - 1);
                        } else {
                            event.setRsvped(true);
                            event.setAttendeeCount(event.getAttendeeCount() + 1);
                        }
                        adapter.updateItem(position);

                        String msg = event.isRsvped()
                                ? "You're going to " + event.getTitle()
                                : "Cancelled RSVP for " + event.getTitle();
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onEventClick(Event event) {
                        Toast.makeText(getContext(),
                                event.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                });
        rvEvents.setAdapter(adapter);
        if(currentUserRole.equals("admin")){
            fabCreateEvent.setVisibility(View.VISIBLE);
        }
        fabCreateEvent.setOnClickListener(v->{
            // TODO: open CreateEventActivity
            Toast.makeText(getContext(),
                    "Create event coming soon", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
    private void loadFakeEvents() {
        eventList.add(new Event("1", "Saturday Morning Run",
                "March 15, 2026", "7:00 AM",
                "Parc du Belvédère, Tunis",
                "Our weekly group run. All paces welcome, we never leave anyone behind. Bring water and good energy!",
                23, false, "1"));

        eventList.add(new Event("2", "Trail Running Workshop",
                "March 22, 2026", "9:00 AM",
                "Djebel Boukornine National Park",
                "Learn proper trail technique from experienced runners. Covers pacing, downhill form, and nutrition for long runs.",
                15, true, "1"));

        eventList.add(new Event("3", "Spring 10K Race",
                "April 5, 2026", "8:00 AM",
                "La Marsa Beachfront",
                "Our first community race of the year. Register separately on the race website. We'll meet at the start line as a group.",
                41, false, "1"));
    }

}