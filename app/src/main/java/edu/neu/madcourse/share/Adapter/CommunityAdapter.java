package edu.neu.madcourse.share.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import edu.neu.madcourse.share.CommunityDetailActivity;
import edu.neu.madcourse.share.Model.Community;
import edu.neu.madcourse.share.R;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {
    public Context context;
    public List<Community> communities;

    private FirebaseUser firebaseUser;

    public CommunityAdapter(Context context, List<Community> communities) {
        this.context = context;
        this.communities = communities;
    }

    @NonNull
    @Override
    public CommunityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.community_item, parent, false);
        return new CommunityAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Community community = communities.get(position);
        holder.name.setText(community.getName());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Community")
                .child(community.getCommunityId());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Community community = snapshot.getValue(Community.class);

                // Set the image of the community.
                Glide.with(context).load(community.getImage()).into(holder.image);
                holder.description.setText(community.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // When clicked go to the detail page of the community.
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommunityDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("communityId", community.getCommunityId());
                intent.putExtra("creatorId", community.getCreator());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return communities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, description;
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.community_title);
            image = itemView.findViewById(R.id.community_image);
            description = itemView.findViewById(R.id.description);

        }
    }
}
