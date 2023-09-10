package com.example.blogapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DetailsActivity extends AppCompatActivity {

    private TextView postTitle, postAuthor, postDate, postContent;
    private Button updateBtn, deleteBtn, dialogBtn;
    private EditText dialogTitle, dialogContent;
    private TextView dialogLabel;
    private String postId;
    private FirebaseFirestore firestoreDb;
    private FirebaseAuth mAuth;
    private Dialog editBlogDialog;
    private String blogsCollection = "blogs";
    private AlertDialog deleteAlertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_details);

        postTitle = findViewById(R.id.detail_post_title);
        postAuthor = findViewById(R.id.detail_author);
        postContent = findViewById(R.id.detail_post_description);
        postDate = findViewById(R.id.detail_post_date);

        updateBtn = findViewById(R.id.detail_edit_post);
        deleteBtn = findViewById(R.id.detail_delete_post);

        mAuth = FirebaseAuth.getInstance();
        firestoreDb = FirebaseFirestore.getInstance();

        postId = getIntent().getStringExtra("id");

        initDialog();
        addListners();
    }

    @Override
    protected void onStart() {
        super.onStart();

        postTitle.setText(getIntent().getStringExtra("title"));
        postDate.setText(getIntent().getStringExtra("updatedAt"));
        postAuthor.setText(getIntent().getStringExtra("author"));
        postContent.setText(getIntent().getStringExtra("content"));

        String currentUserId = mAuth.getCurrentUser().getUid().toString();
        String blogAuthorId = getIntent().getStringExtra("authorId");

        if(!currentUserId.equalsIgnoreCase(blogAuthorId)) {
            updateBtn.setVisibility(View.INVISIBLE);
            deleteBtn.setVisibility(View.INVISIBLE);
        }

        deleteAlertDialog = getAlertDialog();
    }

    private void addListners() {

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogLabel.setText("Edit Blog");
                dialogTitle.setText(getIntent().getStringExtra("title"));
                dialogContent.setText(getIntent().getStringExtra("content"));
                editBlogDialog.show();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteAlertDialog.show();
            }
        });
    }

    private AlertDialog getAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setMessage("Do you want to delete this blog?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteBlog();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }
    private void initDialog() {

        editBlogDialog = new Dialog(this);
        editBlogDialog.setContentView(R.layout.add_edit_blog);
        editBlogDialog.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editBlogDialog.getWindow().getAttributes().gravity = Gravity.CENTER;

        dialogBtn = editBlogDialog.findViewById(R.id.submit_blog);
        dialogTitle = editBlogDialog.findViewById(R.id.blog_title);
        dialogContent = editBlogDialog.findViewById(R.id.blog_content);
        dialogLabel = editBlogDialog.findViewById(R.id.label_text);

        dialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> updatedPost = new HashMap<>();

                updatedPost.put("title", dialogTitle.getText().toString());
                updatedPost.put("content", dialogContent.getText().toString());
                updatedPost.put("updatedAt", new Timestamp(new Date()));

                firestoreDb.collection(blogsCollection).document(getIntent().getStringExtra("id"))
                        .update(updatedPost)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                editBlogDialog.dismiss();
                                showMessage("Blog Updated successfully!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showMessage("Something went wrong");
                            }
                        });

            }
        });

    }

    private void showMessage(String text) {

        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    private void deleteBlog() {

        Log.d("Blog Id", getIntent().getStringExtra("id"));

        firestoreDb.collection(blogsCollection).document(getIntent().getStringExtra("id")).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        showMessage("Blog Deleted Successfully!");
                        Intent intent = new Intent(getApplicationContext(), Home.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Something went wrong!");
                    }
                });
    }
}
