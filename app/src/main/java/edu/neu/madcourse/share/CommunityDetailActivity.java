package edu.neu.madcourse.share;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
    TextView community_title, description, creator_name;
    ImageView community_image, creator_profile;
    List<Post> posts;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_detail_acitvity);

        // ToolBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Community Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Get the Id.
        Intent intent = getIntent();
        communityId = intent.getStringExtra("communityId");
        creatorId = intent.getStringExtra("creatorId");

        // Find the component.
        community_title = findViewById(R.id.community_title);
        community_image = findViewById(R.id.community_image);
        description = findViewById(R.id.description);
        creator_name = findViewById(R.id.creator_name);
        creator_profile = findViewById(R.id.creator_profile);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Users").child(creatorId);

        // Set the name and the image of the creator.
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                creator_name.setText("Created by: " + user.getUsername());
                Glide.with(getBaseContext()).load(user.getImageurl()).into(creator_profile);
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
    }

    private void getPosts() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);

                    if (post != null && post.getCommunity() != null && post.getCommunity().equals(community_title.getText().toString())) {
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
                community_title.setText(community.getName().toString());
                Glide.with(getBaseContext()).load(community.getImage()).into(community_image);
                description.setText(community.getDescription().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}