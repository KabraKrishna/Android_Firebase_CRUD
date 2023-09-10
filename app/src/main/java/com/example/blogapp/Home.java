package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore firestoreDb;
    private EditText dialogTitle, dialogContent;
    private TextView dialogLabel;
    private Button logoutBtn, dialogBtn;
    private FloatingActionButton addNewBlogBtn;
    private Dialog addBlogDialog;
    private String blogsCollection = "blogs";
    private Boolean isEditBlog = false;
    private RecyclerView blogListView;
    private List<Post> blogList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firestoreDb = FirebaseFirestore.getInstance();

        logoutBtn = findViewById(R.id.logout);
        addNewBlogBtn = findViewById(R.id.new_blog);

        blogListView = findViewById(R.id.container_list);
        blogListView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        initDialog();
        addListners();
    }


    @Override
    protected void onStart() {
        super.onStart();
        fetchBlogs();
    }

    private void initDialog() {

        addBlogDialog = new Dialog(this);
        addBlogDialog.setContentView(R.layout.add_edit_blog);
        addBlogDialog.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addBlogDialog.getWindow().getAttributes().gravity = Gravity.CENTER;

        dialogBtn = addBlogDialog.findViewById(R.id.submit_blog);
        dialogTitle = addBlogDialog.findViewById(R.id.blog_title);
        dialogContent = addBlogDialog.findViewById(R.id.blog_content);
        dialogLabel = addBlogDialog.findViewById(R.id.label_text);

        dialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newTitle = dialogTitle.getText().toString();
                String newContent = dialogContent.getText().toString();
                String newUserName = mAuth.getCurrentUser().getDisplayName();
                String newUserId = mAuth.getCurrentUser().getUid().toString();

                Map<String, Object> newPost = new HashMap<>();

                newPost.put("title", newTitle);
                newPost.put("content", newContent);
                newPost.put("userName", newUserName);
                newPost.put("userId", newUserId);
                newPost.put("isActive", true);
                newPost.put("createdAt", new Timestamp(new Date()));
                newPost.put("updatedAt", new Timestamp(new Date()));

                firestoreDb.collection(blogsCollection)
                        .add(newPost)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                addBlogDialog.dismiss();
                                showMessage("Blog added successfulyy!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showMessage(e.getMessage());
                            }
                        });

            }
        });

    }

    private void showMessage(String text) {

        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    private Context getActivityContext() {
        return this;
    }

    private void addListners() {

        addNewBlogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogLabel.setText("Add New Blog");
                addBlogDialog.show();
            }
        });
    }

    private void fetchBlogs() {

        firestoreDb.collection(blogsCollection)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        blogList = new ArrayList<>();

                        for(DocumentSnapshot doc: value.getDocuments()){

                            String bId = doc.getId();
                            String title = doc.get("title").toString();
                            String userId = doc.get("userId").toString();
                            String content = doc.get("content").toString();
                            String userName = doc.get("userName").toString();
                            Object createdAt = doc.get("createdAt");
                            Object updatedAt = doc.get("updatedAt");
                            Boolean isActive = (Boolean) doc.get("isActive");

                            Post post = new Post(bId, title, content, userId, userName, createdAt, updatedAt, isActive);

                            blogList.add(post);

                        }

                        BlogAdapter blogAdapter = new BlogAdapter(getActivityContext(), blogList);
                        blogListView.setAdapter(blogAdapter);
                    }
                });
    }
}