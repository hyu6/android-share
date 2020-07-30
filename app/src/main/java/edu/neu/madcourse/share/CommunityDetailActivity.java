package edu.neu.madcourse.share;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.share.Adapter.PostAdapter;
import edu.neu.madcourse.share.Model.Community;
import edu.neu.madcourse.share.Model.Post;
import edu.neu.madcourse.share.Model.User;

public class CommunityDetailActivity extends AppCompatActivity {
    String communityId;
    String creatorId;
    String communityName;
    TextView description, creator_name;
    Button subscribe;
    ImageView community_image, creator_profile;
    List<Post> posts;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_detail);

        // ToolBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setBackgroundColor(getResources().getColor(R.color.lightBlue));

        // Get the Id.
        Intent intent = getIntent();
        communityId = intent.getStringExtra("communityId");
        creatorId = intent.getStringExtra("creatorId");

        // Find the component.
        community_image = findViewById(R.id.community_image);
        description = findViewById(R.id.description);
        creator_name = findViewById(R.id.creator_name);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Users").child(creatorId);

        // Set the name and the image of the creator.
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                creator_name.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Set the information about the community.
        getCommunity();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        RecyclerView.ItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

        // Display the posts inside this community.
        posts = new ArrayList<>();

        getPosts();

        postAdapter = new PostAdapter(this, posts);
        recyclerView.setAdapter(postAdapter);


        //set subscribe button
        subscribe = findViewById(R.id.subscribe);
        final String mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        isSubscribed(communityId, mUid);
        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subscribe.getText().toString().equals("Subscribe")) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Subscribe")
                            .child(mUid)
                            .child(communityId)
                            .setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Subscribe")
                            .child(mUid)
                            .child(communityId)
                            .removeValue();
                }
            }
        });
    }

    // Check whether the current user subscribes this community.
    private void isSubscribed(final String communityId, String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Subscribe")
                .child(uid)
                .child(communityId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    subscribe.setText("Unsubscribe");
                } else {
                    subscribe.setText("Subscribe");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPosts() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);

                    if (post != null && post.getCommunity() != null && post.getCommunity().equals(communityName)) {
                        posts.add(post);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Set the value of the community details
    private void getCommunity() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Community").child(communityId);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Community community = snapshot.getValue(Community.class);
                communityName = community.getName();
                getSupportActionBar().setTitle(communityName);
                Glide.with(getBaseContext()).load(community.getImage()).into(community_image);
                description.setText(community.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}