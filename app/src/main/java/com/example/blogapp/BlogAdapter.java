package com.example.blogapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {

    Context mContext;
    List<Post> postData;

    public BlogAdapter(Context mContext, List<Post> postData) {
        this.mContext = mContext;
        this.postData = postData;
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.post_row, parent, false);
        return new BlogViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {

        holder.docId.setText(postData.get(position).getBlogId());
        holder.postTitle.setText(postData.get(position).getTitle());
        holder.postAuthor.setText(postData.get(position).getUserName());

        Timestamp updatedAt = (Timestamp) postData.get(position).getUpdatedAt();

        String formattedDate = new SimpleDateFormat("MM/dd/yy").format(updatedAt.toDate());

        holder.postDate.setText(formattedDate);

    }

    @Override
    public int getItemCount() {
        return postData.size();
    }

    public class BlogViewHolder extends RecyclerView.ViewHolder {

        TextView postTitle, postAuthor, postDate, docId;
        public BlogViewHolder(View itemView) {
            super(itemView);

            postTitle = itemView.findViewById(R.id.post_title);
            postAuthor = itemView.findViewById(R.id.author);
            postDate = itemView.findViewById(R.id.post_date);
            docId = itemView.findViewById(R.id.blog_doc_id);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent postDetails = new Intent(mContext, DetailsActivity.class);
                    postDetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    int pos = getAdapterPosition();

                    postDetails.putExtra("id", postData.get(pos).getBlogId());
                    postDetails.putExtra("title", postData.get(pos).getTitle());
                    postDetails.putExtra("author", postData.get(pos).getUserName());
                    postDetails.putExtra("authorId", postData.get(pos).getUserId());
                    postDetails.putExtra("content", postData.get(pos).getDescription());
                    postDetails.putExtra("updatedAt", postDate.getText().toString());

                    mContext.startActivity(postDetails);
                }
            });
        }

    }
}
