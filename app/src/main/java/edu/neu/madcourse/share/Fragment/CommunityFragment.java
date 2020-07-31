package edu.neu.madcourse.share.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.share.Adapter.CommunityAdapter;
import edu.neu.madcourse.share.Model.Community;
import edu.neu.madcourse.share.R;

public class CommunityFragment extends Fragment {
    private RecyclerView recyclerView;
    private CommunityAdapter communityAdapter;
    private List<Community> communities;
    EditText searchBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_community, container, false);

        // Community setting.
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);


//        RecyclerView.ItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
//        recyclerView.addItemDecoration(divider);

        // Inflate the adapter to the RecycleView.
        communities = new ArrayList<>();

        // Add values to the communities ArrayList.
        getCommunities();

        communityAdapter = new CommunityAdapter(getContext(), communities);
        recyclerView.setAdapter(communityAdapter);

//        Log.d("Test", "onCreateView: attached the textView already." + communities.size());


        //set search application
        searchBar = view.findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchCommunities(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void searchCommunities(String s) {
        Query query = FirebaseDatabase.getInstance().getReference("Communities")
                .orderByChild("name")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                communities.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Community community = snapshot.getValue(Community.class);
                    communities.add(community);
                }

                communityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getCommunities() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Communities");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                communities.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Community community = dataSnapshot.getValue(Community.class);
                    communities.add(community);
                }
                communityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}