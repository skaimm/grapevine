package com.grapevine.grapevine;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.grapevine.grapevine.Adapters.CommentAdapter;
import com.grapevine.grapevine.Models.Review;
import com.grapevine.grapevine.Utils.ConnectionClass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class CommetsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private ArrayList<Review> reviewList;

    EditText addcomment;
    TextView sendcomment;
    Review review;


    int userID,treeid,importance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commets);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        reviewList = new ArrayList<>();
        assert bundle != null;
        reviewList = bundle.getParcelableArrayList("reviews");
        importance = bundle.getInt("importance");
        userID = bundle.getInt("userid");
        treeid = bundle.getInt("treeid");

        recyclerView = findViewById(R.id.commentrec);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        commentAdapter = new CommentAdapter(this,reviewList);
        recyclerView.setAdapter(commentAdapter);

        addcomment = findViewById(R.id.addcomment);
        sendcomment = findViewById(R.id.send);

        sendcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                review = new Review();
                if(addcomment.getText().toString().equals("")){
                    Toast.makeText(CommetsActivity.this,"You can't send empty comment",Toast.LENGTH_LONG).show();
                }else{
                    review.setComment(addcomment.getText().toString());
                    review.setImportance(importance);
                    review.setUserid(userID);
                    review.setTreeid(treeid);

                    SendComment sendComment = new SendComment(review);
                    sendComment.execute();

                    addcomment.getText().clear();
                    reviewList.add(review);
                    commentAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    @SuppressLint("StaticFieldLeak")
    public class SendComment extends AsyncTask<String, String, Void> {
        ConnectionClass connectionClass;
        Review review;

        public SendComment(Review review) {
            this.connectionClass = new ConnectionClass();
            this.review = review;
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if(con==null){
                }else {
                    String query = "INSERT INTO reviews(comment,personID,importance,treeID) VALUES ('"+review.getComment()+"'," +
                            "'"+review.getUserid()+"','"+review.getImportance()+"','"+review.getTreeid()+"')";

                    Statement stmt = con.createStatement();
                    stmt.executeUpdate(query);
                    con.close();
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // after add new commet set change recy items TODO:
            super.onPostExecute(aVoid);
        }
    }

}
