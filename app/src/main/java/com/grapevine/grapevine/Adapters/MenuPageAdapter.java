package com.grapevine.grapevine.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.grapevine.grapevine.CommetsActivity;
import com.grapevine.grapevine.Utils.ConnectionClass;
import com.grapevine.grapevine.Models.Pruning;
import com.grapevine.grapevine.Models.Tree;
import com.grapevine.grapevine.R;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MenuPageAdapter extends RecyclerView.Adapter<MenuPageAdapter.ViewHolder>{

    public Context mContext;
    public List<Tree> mTreeList;
    public List<Pruning> mPrunings;
    Canvas newCanvas;
    Paint paint;
    ArrayList<Integer> lastPositions;
    ArrayList<ViewHolder> viewHolders;
    int userID;

    @SuppressLint("UseSparseArrays")
    public MenuPageAdapter(Context mContext, List<Tree> mTreeList,int userID) {
        this.mContext = mContext;
        this.mTreeList = mTreeList;
        this.newCanvas = null;
        this.paint = new Paint();
        this.userID =userID;
        this.lastPositions = new ArrayList<>(2);
        this.viewHolders = new ArrayList<>(2);
        this.mPrunings = new ArrayList<>();
        paint.setColor(Color.RED);
        paint.setFakeBoldText(true);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_tree,viewGroup,false);
        return new MenuPageAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        final Tree tree = mTreeList.get(i);

        Bitmap btmp = byteArraytoBitmap(tree.getCimage());
        viewHolder.recimage.setImageBitmap(btmp);
        viewHolder.treeid.setText(String.valueOf(tree.getTreeid()));
        viewHolder.userid.setText(String.valueOf(tree.getPersonid()));
        viewHolder.comment.setText("View All "+tree.getReviewList().size()+" Comments");


        viewHolder.prune.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View view) {

                viewHolder.postanno.setEnabled(false);
                viewHolder.preanno.setEnabled(false);

                if(lastPositions.size()==2){
                    lastPositions.remove(0);
                    viewHolders.remove(0);
                }
                lastPositions.add(i);
                viewHolders.add(viewHolder);

                ViewHolder v = viewHolders.get(0);
                if(!v.prune.isEnabled()){
                    Tree tree = mTreeList.get(lastPositions.get(0));
                    v.prune.setEnabled(true);
                    v.postanno.setEnabled(true);
                    v.preanno.setEnabled(true);
                    Bitmap btmp = byteArraytoBitmap(tree.getCimage());
                    v.recimage.setEnabled(false);
                    v.recimage.setImageBitmap(btmp);
                    mPrunings.clear();
                }

                Bitmap btmp = byteArraytoBitmap(tree.getTimage());
                Bitmap newBitmap = null;
                Drawable d = new BitmapDrawable(mContext.getResources(), btmp);
                viewHolder.recimage.setBackground(d);
                viewHolder.recimage.setImageBitmap(btmp);
                viewHolder.recimage.setEnabled(true);
                Bitmap.Config config = btmp.getConfig();
                if(config ==null){
                    config = Bitmap.Config.ARGB_8888;
                }
                newBitmap = Bitmap.createBitmap(viewHolder.recimage.getWidth(),viewHolder.recimage.getHeight(),config);

                newCanvas = new Canvas(newBitmap);
                newCanvas.drawBitmap(btmp,0,0,null);

                viewHolder.prune.setEnabled(false);
                final Bitmap finalNewBitmap = newBitmap;
                viewHolder.recimage.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        int x = (int) motionEvent.getX();
                        int y = (int) motionEvent.getY();
                        newCanvas.drawCircle(x,y,20,paint);
                        Pruning prune = new Pruning(x,y,tree.getTreeid());
                        mPrunings.add(prune);
                        viewHolder.recimage.setImageBitmap(finalNewBitmap);

                        Boolean bool = false;
                        for(int i=0;i<mPrunings.size();i++){
                            if(tree.getTreeid() == mPrunings.get(i).getTreeid()){
                                bool =true;
                                break;
                            }
                        }
                        if(bool){
                            viewHolder.rprune.setEnabled(true);
                            viewHolder.sprune.setEnabled(true);
                        }
                        return false;
                    }
                });
            }
        });

        viewHolder.rprune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = mPrunings.size()-1; i>=0;i--){
                    if(mPrunings.get(i).getTreeid()==tree.getTreeid()){
                        Paint clearPaint = new Paint();
                        clearPaint.setFakeBoldText(true);
                        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                        newCanvas.drawCircle(mPrunings.get(i).getX(),mPrunings.get(i).getY(),20, clearPaint);

                        mPrunings.remove(i);
                        break;
                    }
                }

                Boolean bool = false;
                for(int i=0;i<mPrunings.size();i++){
                    if(tree.getTreeid() == mPrunings.get(i).getTreeid()){
                        bool =true;
                        break;
                    }
                }
                if(!bool){
                    viewHolder.rprune.setEnabled(false);
                    viewHolder.sprune.setEnabled(false);
                }
            }
        });

        viewHolder.sprune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // save the prune coordinates
                SavePrune savePrune = new SavePrune();
                savePrune.execute();

                viewHolder.prune.setEnabled(true);
                viewHolder.rprune.setEnabled(false);
                viewHolder.sprune.setEnabled(false);
                viewHolder.preanno.setEnabled(true);
                viewHolder.postanno.setEnabled(true);

                Bitmap btmp = byteArraytoBitmap(tree.getCimage());
                viewHolder.recimage.setEnabled(false);
                viewHolder.recimage.setImageBitmap(btmp);

                // prune it on picture ??
            }
        });

        viewHolder.preanno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap btmp = byteArraytoBitmap(tree.getTimage());
                viewHolder.recimage.setImageBitmap(btmp);
            }
        });

        viewHolder.postanno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap btmp = byteArraytoBitmap(tree.getCimage());
                viewHolder.recimage.setImageBitmap(btmp);
            }
        });

        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,CommetsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("reviews", tree.getReviewList());
                bundle.putInt("userid",userID);
                bundle.putInt("importance",0);
                bundle.putInt("treeid",tree.getTreeid());
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTreeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView recimage;
        public TextView userid, treeid,comment;
        public Button prune,rprune,sprune,preanno,postanno;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recimage = itemView.findViewById(R.id.recimage);
            userid = itemView.findViewById(R.id.tuserid);
            treeid = itemView.findViewById(R.id.ttreeid);
            comment = itemView.findViewById(R.id.reccomments);
            prune = itemView.findViewById(R.id.prune);
            rprune = itemView.findViewById(R.id.rprune);
            sprune = itemView.findViewById(R.id.sprune);
            preanno = itemView.findViewById(R.id.preanno);
            postanno = itemView.findViewById(R.id.postanno);

        }
    }

    @SuppressLint("SetTextI18n")
    private void getComments(Tree tree, TextView comment){
        comment.setText("View All "+tree.getReviewList().size()+" Comments");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public Bitmap byteArraytoBitmap(String data) {

        byte[] encodeByte  = Base64.decode(data, Base64.DEFAULT);
        Bitmap imgBitMap = BitmapFactory.decodeByteArray(encodeByte , 0, encodeByte.length);
        return imgBitMap;
    }

    @SuppressLint("StaticFieldLeak")
    public class SavePrune extends AsyncTask<String, String, Void> {
        ConnectionClass connectionClass = new ConnectionClass();

        @Override
        protected Void doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if(con==null){
                }else {
                    for(int i=0;i<mPrunings.size();i++){
                        String query = "INSERT INTO prunings(x,y,treeID,PersonID) VALUES ('"+mPrunings.get(i).getX()+"'," +
                                "'"+mPrunings.get(i).getY()+"','"+mPrunings.get(i).getTreeid()+"','"+userID+"')";

                        Statement stmt = con.createStatement();
                        stmt.executeUpdate(query);
                    }

                    mPrunings.clear();
                    con.close();
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


}
