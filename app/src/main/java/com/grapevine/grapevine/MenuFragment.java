package com.grapevine.grapevine;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grapevine.grapevine.Adapters.MenuPageAdapter;
import com.grapevine.grapevine.Models.Branch;
import com.grapevine.grapevine.Models.Review;
import com.grapevine.grapevine.Models.Tree;
import com.grapevine.grapevine.Utils.ConnectionClass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private SwipeRefreshLayout swipeLayout;
    private RecyclerView recyclerView;
    private MenuPageAdapter menuPageAdapter;
    private ArrayList<Tree> treeList = new ArrayList<>();
    private int userID;
    GetTreeInfo getTreeInfo = new GetTreeInfo();

    public MenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);

        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);
        swipeLayout.setOnRefreshListener(this);

        if(getArguments()!=null){
            userID = getArguments().getInt("userid");
        }

        recyclerView = rootView.findViewById(R.id.menubar);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        menuPageAdapter = new MenuPageAdapter(getContext(),treeList,userID);
        recyclerView.setAdapter(menuPageAdapter);

        syncTasks();

        return rootView;
    }

    @Override
    public void onRefresh() {
        syncTasks();
        swipeLayout.setRefreshing(false);
    }




    public class GetTreeInfo extends AsyncTask<Void, Void, ArrayList<Tree>> {
        ConnectionClass connectionClass = new ConnectionClass();
        private ArrayList<Tree> dbtreeList = new ArrayList<>();

        @Override
        protected ArrayList<Tree> doInBackground(Void... strings) {
            try {
                Connection con = connectionClass.CONN();
                if(con==null){
                }else {

                    String query="SELECT * FROM trees";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    int treeid,userid;
                    String image,canvas;

                    while(rs.next()){
                        ArrayList<Review> reviews= new ArrayList<>();
                        ArrayList<Branch> branches=new ArrayList<>();

                        treeid = rs.getInt(1);
                        userid = rs.getInt(2);
                        image = rs.getString(3);
                        canvas = rs.getString(4);

                        String query2="SELECT * FROM reviews where treeID ='"+treeid+"'";
                        Statement stmt2 = con.createStatement();
                        ResultSet rs2 = stmt2.executeQuery(query2);
                        int ruserid,rtreeid,importance;
                        String comment;

                        while(rs2.next()){
                            comment = rs2.getString(1);
                            ruserid = rs2.getInt(2);
                            importance = rs2.getInt(3);
                            rtreeid = rs2.getInt(4);

                            reviews.add(new Review(ruserid,comment,importance,rtreeid));
                        }
                        Tree tree = new Tree(treeid,image,canvas,userid,branches,reviews);
                        dbtreeList.add(tree);

                    }
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
            return dbtreeList;
        }

        @Override
        protected void onPostExecute(ArrayList<Tree> result) {

            if(treeList.isEmpty()){
                treeList.addAll(result);
            }else {
                treeList.clear();
                treeList.addAll(result);
            }
            menuPageAdapter.notifyDataSetChanged();

        }
    }


    public void syncTasks() {
        try {
            if (getTreeInfo.getStatus() != AsyncTask.Status.RUNNING){   // check if asyncTasks is running
                getTreeInfo.cancel(true); // asyncTasks not running => cancel it
                getTreeInfo = new GetTreeInfo(); // reset task
                getTreeInfo.execute(); // execute new task (the same task)
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MainActivity_TSK", "Error: "+e.toString());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        syncTasks();
    }
}
