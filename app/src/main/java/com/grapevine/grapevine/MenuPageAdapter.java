package com.grapevine.grapevine;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.grapevine.grapevine.Models.Review;
import com.grapevine.grapevine.Models.Tree;

import java.util.List;

public class MenuPageAdapter extends RecyclerView.Adapter<MenuPageAdapter.ViewHolder>{

    public Context mContext;
    public List<Tree> mTreeList;

    public MenuPageAdapter(Context mContext, List<Tree> mTreeList) {
        this.mContext = mContext;
        this.mTreeList = mTreeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_tree,viewGroup,false);
        return new MenuPageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Tree tree = mTreeList.get(i);

        Glide.with(mContext).load(tree.getTimage()).into(viewHolder.recimage);
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
}
