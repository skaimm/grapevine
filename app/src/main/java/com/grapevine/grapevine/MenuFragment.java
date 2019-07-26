package com.grapevine.grapevine;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.grapevine.grapevine.Models.Branch;
import com.grapevine.grapevine.Models.Review;
import com.grapevine.grapevine.Models.Tree;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {


    private RecyclerView recyclerView;
    private MenuPageAdapter menuPageAdapter;
    private List<Tree> treeList;

    public MenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);

        recyclerView = rootView.findViewById(R.id.menubar);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        treeList = new ArrayList<>();
        menuPageAdapter = new MenuPageAdapter(getContext(),treeList);
        recyclerView.setAdapter(menuPageAdapter);

        ReadPost rp = new ReadPost();
        rp.execute();

        menuPageAdapter.notifyDataSetChanged();
        return rootView;
    }

    public class ReadPost extends AsyncTask<String, String, Void> {
        ConnectionClass connectionClass = new ConnectionClass();


        @Override
        protected Void doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if(con==null){
                    Toast.makeText(getContext(),"Please check your Internet",Toast.LENGTH_SHORT).show();
                }else {

                    String query="SELECT * FROM trees";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    int treeid,userid;
                    com.mysql.jdbc.Blob image,canvas;

                    while(rs.next()){

                        ArrayList<Review> reviews= new ArrayList<>();
                        int ruserid,rtreeid,importance;
                        String comment;

                        ArrayList<Branch> branches=new ArrayList<>();

                        treeid = rs.getInt(1);
                        image = (com.mysql.jdbc.Blob) rs.getBlob(3);

                        String query2="SELECT * FROM reviews where treeID ='"+treeid+"'";
                        Statement stmt2 = con.createStatement();
                        ResultSet rs2 = stmt2.executeQuery(query2);

                        while(rs2.next()){
                            comment = rs2.getString(1);
                            ruserid = rs2.getInt(2);
                            importance = rs2.getInt(3);
                            rtreeid = rs2.getInt(4);

                            Review review = new Review(ruserid,comment,importance,rtreeid);
                            reviews.add(review);
                        }


                        Tree tree = new Tree(treeid,image,branches,reviews);
                        treeList.add(tree);

                    }
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
