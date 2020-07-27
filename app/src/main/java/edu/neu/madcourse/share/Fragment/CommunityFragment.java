package edu.neu.madcourse.share.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.share.Adapter.CommunityAdapter;
import edu.neu.madcourse.share.CommunityActivity;
import edu.neu.madcourse.share.Model.Community;
import edu.neu.madcourse.share.R;

public class CommunityFragment extends Fragment {
    private RecyclerView recyclerView;
    private CommunityAdapter communityAdapter;
    private List<Community> communities;
    private ImageView create;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_community, container, false);

        // Community setting.
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        RecyclerView.ItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

        // Inflate the adapter to the RecycleView.
        communities = new ArrayList<>();

        // Add values to the communities ArrayList.
        getCommunities();

        communityAdapter = new CommunityAdapter(getContext(), communities);
        recyclerView.setAdapter(communityAdapter);

//        Log.d("Test", "onCreateView: attached the textView already." + communities.size());

        // Button which create the community
        create = view.findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CommunityActivity.class));
            }
        });

        return view;
    }

    private void getCommunities() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Community");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                communities.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Community community = dataSnapshot.getValue(Community.class);
//                    Log.d("Test", "onDataChange: " + community.getName());
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