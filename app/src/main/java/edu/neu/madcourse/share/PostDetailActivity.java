package edu.neu.madcourse.share;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.HashMap;
import java.util.List;

import edu.neu.madcourse.share.Adapter.CommentAdapter;
import edu.neu.madcourse.share.Model.Comment;
import edu.neu.madcourse.share.Model.Post;
import edu.neu.madcourse.share.Model.User;

public class PostDetailActivity extends AppCompatActivity {
    String postID;
    private Post mpost;

    private TextView post_title;
    private TextView post_content;
    private TextView author_name;
    private ImageView author_profile;
    private ImageView post_img;

    private ImageView like;
    private ImageView save;
    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private EditText addComment;
    private TextView post;

    private TextView like_num;
    private TextView comment_num;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Post Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        postID = intent.getStringExtra("postID");

        post_title = findViewById(R.id.post_title);
        post_content = findViewById(R.id.post_content);
        author_profile = findViewById(R.id.image_profile);
        author_name = findViewById(R.id.username);
        post_img = findViewById(R.id.post_img);

        like = findViewById(R.id.like);
        save = findViewById(R.id.favorite);

        like_num = findViewById(R.id.like_num);
        comment_num = findViewById(R.id.comment_num);

        getLikes(postID, like);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (like.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(postID)
                            .child(firebaseUser.getUid()).setValue(true);
                    addNotifications(mpost.getAuthorID(), mpost.getPostID());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(postID)
                            .child(firebaseUser.getUid()).removeValue();
                }
                countLikes(postID, like_num);
            }
        });
        getPost();

        //get comment
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList);
        recyclerView.setAdapter(commentAdapter);

        RecyclerView.ItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);
        addComment = findViewById(R.id.add_comment);
        readComments();

        //add comments
        post = findViewById(R.id.post);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addComment.getText().toString().equals("")) {
                    Toast.makeText(PostDetailActivity.this, "Empty Comment.", Toast.LENGTH_SHORT).show();
                } else {
                    addComment();
                }
            }
        });

        //save posts
        isSaved(postID, save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (save.getTag().equals("save")) {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(postID).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(postID).removeValue();
                }
            }
        });

        //count likes
        countLikes(postID, like_num);


        //count comments
        countComments(postID, comment_num);
    }

    private void isSaved(final String postID, final ImageView imageView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(firebaseUser.getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postID).exists()) {
                    imageView.setImageResource(R.drawable.ic_saved);
                    imageView.setTag("saved");
                } else {
                    imageView.setImageResource(R.drawable.ic_favorite);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getLikes(String postID, final ImageView imageView) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("Likes").child(postID);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addComment() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments")
                .child(postID);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", addComment.getText().toString());
        hashMap.put("publisher", firebaseUser.getUid());

        reference.push().setValue(hashMap);
        addNotifications();
        addComment.setText("");
        closeKeyboard();
        readComments();
        countComments(postID, comment_num);
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void countLikes(String postID, final TextView like_num) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Likes").child(postID);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    count++;
                }
                like_num.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void countComments(String postID, final TextView comment_num) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Comments").child(postID);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    count++;
                }
                comment_num.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getPost() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postID);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mpost = snapshot.getValue(Post.class);
                post_title.setText(mpost.getTitle());
                post_content.setText(mpost.getPostContent());
                if (getBaseContext() != null) {
                    Glide.with(getBaseContext()).load(mpost.getPostIMG()).into(post_img);
                }
                getAuthorInfo(mpost.getAuthorID());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAuthorInfo(String authorID) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(authorID);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User author = snapshot.getValue(User.class);
                if (getBaseContext() != null && author.getImageurl() != null) {
                    Glide.with(getBaseContext()).load(author.getImageurl()).into(author_profile);
                }
                author_name.setText(author.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readComments() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments")
                .child(postID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }

                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // like
    private void addNotifications(String userId, String postId) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Notifications").child(userId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userId", userId);
        hashMap.put("text", "liked your post");
        hashMap.put("postId", postId);
        hashMap.put("isPost", true);

        reference.push().setValue(hashMap);
    }

    // comment
    private void addNotifications() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Notifications").child(mpost.getAuthorID());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userId", firebaseUser.getUid());
        hashMap.put("text", "commented: " + addComment.getText().toString());
        hashMap.put("postId", mpost.getPostID());
        hashMap.put("isPost", true);

        reference.push().setValue(hashMap);
    }
}
