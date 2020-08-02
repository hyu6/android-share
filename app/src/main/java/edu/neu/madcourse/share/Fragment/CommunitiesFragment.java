package edu.neu.madcourse.share.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.share.Adapter.CommunityAdapter;
import edu.neu.madcourse.share.Adapter.SingleCommunityAdapter;
import edu.neu.madcourse.share.Model.Community;
import edu.neu.madcourse.share.Model.Post;
import edu.neu.madcourse.share.MyPostsActivity;
import edu.neu.madcourse.share.R;
import edu.neu.madcourse.share.SearchCommunityActivity;

public class CommunitiesFragment extends Fragment {

    private RecyclerView recyclerView;
    private SingleCommunityAdapter communityAdapter;
    private List<Community> communities;

    private ImageView search;

    RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_communities, container, false);
        // Inflate the layout for this fragment

        recyclerView = view.findViewById(R.id.recycler_view);
        layoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);


        communities = new ArrayList<>();
        // Add values to the communities ArrayList.
        getCommunities();
        communityAdapter = new SingleCommunityAdapter(getContext(), communities);
        recyclerView.setAdapter(communityAdapter);

        //set search on click
        search = view.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchCommunityActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void getCommunities() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Communities");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                communities.clear();
                int count = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Community community = dataSnapshot.getValue(Community.class);
                    communities.add(community);
                    count++;
                }
                Log.d("count", "onDataChange: " + count);
                communityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}