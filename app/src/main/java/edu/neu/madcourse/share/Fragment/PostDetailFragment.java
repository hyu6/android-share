package edu.neu.madcourse.share.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.neu.madcourse.share.CommentsActivity;
import edu.neu.madcourse.share.Model.Post;
import edu.neu.madcourse.share.Model.User;
import edu.neu.madcourse.share.R;

public class PostDetailFragment extends Fragment {
    String postID;
    private Post mpost;

    private TextView post_title;
    private TextView post_content;
    private TextView author_name;
    private ImageView author_profile;
    private ImageView post_img;

    private Button back_button;
    private ImageView like;
    private ImageView comment;
    private ImageView favorite;
    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        postID = sharedPreferences.getString("postID", "none");

        post_title = view.findViewById(R.id.post_title);
        post_content = view.findViewById(R.id.post_content);
        author_profile = view.findViewById(R.id.image_profile);
        author_name = view.findViewById(R.id.username);
        post_img = view.findViewById(R.id.post_img);

        comment = view.findViewById(R.id.comment);
        like = view.findViewById(R.id.like);

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CommentsActivity.class);
                intent.putExtra("postID", postID);
                intent.putExtra("authorID", mpost.getAuthorID());
                startActivity(intent);
            }
        });

        getLikes(postID, like);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(postID)
                            .child(firebaseUser.getUid()).setValue(true);
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(postID)
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });


        getPost();
        return view;
    }

    private void getLikes(String postID, final ImageView imageView){


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("Likes").child(postID);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                }else{
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getLikes(final TextView likes, String postID){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Likes").child(postID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildren() + "likes");
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
                if(getActivity().getApplicationContext() != null){
                    Glide.with(getActivity().getApplicationContext()).load(mpost.getPostIMG()).into(post_img);
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
                if(getActivity().getApplicationContext() != null && author.getImageurl() != null) {
                    Glide.with(getActivity().getApplicationContext()).load(author.getImageurl()).into(author_profile);
                }
                author_name.setText(author.getUsername());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}