package com.grapevine.grapevine;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.grapevine.grapevine.Models.Branch;
import com.grapevine.grapevine.Models.Coordinates;
import com.grapevine.grapevine.Models.Review;
import com.grapevine.grapevine.Models.Tree;
import com.android.volley.Request;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class AnnotationFragment extends Fragment implements AdapterView.OnItemSelectedListener,View.OnClickListener,View.OnTouchListener {

    Button load,sbranch,fbranch,amarker,rmarker,sub;
    Spinner parentspin;
    ListView listView;
    TextView tinfo,text;
    ImageView isource;
    LinearLayout layoutopvis;
    Canvas newCanvas;
    Paint paint;
    Path path;
    ArrayAdapter<Integer> spadapter;
    ArrayAdapter lvadapter;
    final int RQS_IMAGE = 1;
    Uri source1;
    int userID=1;
    byte[] blob;

    ArrayList<String> lvcontent = new ArrayList<>();
    ArrayList<Integer> numberss = new ArrayList<>();
    ArrayList<Coordinates> coord = new ArrayList<>();
    int branchid = 0;
    ArrayList<Branch> branches = new ArrayList<>();
    int treeid= 0;
    ArrayList<Tree> trees = new ArrayList<>();

    Branch branch;
    int selected=-1;

    public AnnotationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_annotation, container, false);

        load = rootView.findViewById(R.id.load);
        sbranch = rootView.findViewById(R.id.branch);
        fbranch = rootView.findViewById(R.id.finbranch);
        amarker = rootView.findViewById(R.id.marker);
        rmarker = rootView.findViewById(R.id.revmarker);
        parentspin = rootView.findViewById(R.id.spinparent);
        sub = rootView.findViewById(R.id.submit);
        listView = rootView.findViewById(R.id.ulistview);
        text = rootView.findViewById(R.id.utext);
        tinfo = rootView.findViewById(R.id.loadinfo);
        isource = rootView.findViewById(R.id.isource);
        layoutopvis = rootView.findViewById(R.id.lloption);

        load.setOnClickListener(this);

        spadapter = new ArrayAdapter<Integer>(getActivity(),android.R.layout.simple_spinner_item,numberss);
        spadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        parentspin.setAdapter(spadapter);

        parentspin.setOnItemSelectedListener(this);

        lvadapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,lvcontent);
        listView.setAdapter(lvadapter);


        return rootView;

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.load:
                if(amarker.isEnabled()){
                    parentspin.setEnabled(true);
                    lvcontent.clear();
                    coord.clear();
                    numberss.clear();
                    branches.clear();
                    branchid=0;

                    lvadapter.notifyDataSetChanged();
                    spadapter.notifyDataSetChanged();

                    amarker.setEnabled(true);
                }
                Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,RQS_IMAGE);
                break;
            case R.id.branch:
                createBranch();
                isource.setOnTouchListener(this);
                break;
            case R.id.finbranch:
                addBranch();
                break;
            case R.id.marker:
                addCoordinates();
                break;
            case R.id.revmarker:
                removeCoordinates();
                break;
            case R.id.submit:
                try {
                    createTree();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case RQS_IMAGE:
                    source1 = data.getData();
                    tinfo.setText(uritofilename(source1));
                    isource.setImageBitmap(ProcessingBitmap());
                    layoutopvis.setVisibility(View.VISIBLE);

                    rmarker.setEnabled(false);
                    fbranch.setEnabled(false);
                    sbranch.setEnabled(true);
                    sub.setEnabled(false);

                    amarker.setOnClickListener(this);
                    rmarker.setOnClickListener(this);
                    fbranch.setOnClickListener(this);
                    sbranch.setOnClickListener(this);
                    sub.setOnClickListener(this);

                    break;
            }
        }
    }

    private void createBranch(){
        //create new branch with branchid and push it to branches list
        branch = new Branch(branchid);
        branches.add(branch);

        // add it in lvcontent + and on text set the name
        lvcontent.add("Branch - " + branchid);
        lvadapter.notifyDataSetChanged();
        text.setText("Coordinates");

        //set enable imageview for rouching and finish branch button
        isource.setEnabled(true);
        sbranch.setEnabled(false);
        sub.setEnabled(false);
    }

    private void addCoordinates(){
        // show tha quick tutorial about how to use

        new ShowcaseView.Builder(getActivity())
                .setStyle(R.style.SCVStyle)
                .setContentTitle(" Quick Tutorial ")
                .setContentText("1 - Click on Start Branch and select the parent" + "\n" +
                "2 - Annotate the branch in question." + "\n" +
                "3 - Click on Finish Branch" + "\n" +
                "4 - Click on Submit after finish all branches")
                .hideOnTouchOutside()
                .build();
    }

    private void removeCoordinates(){
        // clear the last marker on canvas for using coordinates
        Paint clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        newCanvas.drawCircle(coord.get(coord.size()-1).getX(),coord.get(coord.size()-1).getY(),10, clearPaint);

        // clear last coordinates from coord and lvcontent
        coord.remove(coord.size() - 1);
        lvcontent.remove(lvcontent.size()-1);
        lvadapter.notifyDataSetChanged();

        if(coord.size()<1){
            rmarker.setEnabled(false);
        }
        if(coord.size()<2){
            fbranch.setEnabled(false);
        }

    }

    private void addBranch(){

        // draw line between coordinates

        paint.setStrokeWidth(10);
        for(int i=0;i<coord.size()-1;i++){
            newCanvas.drawLine(coord.get(i).getX(),coord.get(i).getY(),
                    coord.get(i+1).getX(),coord.get(i+1).getY(),paint);
        }

        // set paint color for next branch lines.
        int xy = getMidpointofLines();
        paint.setTextSize(50);
        paint.setFakeBoldText(true);
        paint.setColor(Color.WHITE);
        newCanvas.drawCircle(coord.get(xy).getX()+10,coord.get(xy).getY()+10,30,paint);
        paint.setColor(Color.BLACK);
        newCanvas.drawText(String.valueOf(branchid),coord.get(xy).getX()-5,coord.get(xy).getY()+30,paint);
        paint.setColor(getRandomColor());

        // set coordinates to branch

        branch.setCoordinates(coord);
        //branch.setChildren();

        // set parentid for branch and childrenid for its parents
        if(parentspin.getSelectedView()!=null){
            int parentid = selected;
            branch.setParentid(parentid);

            for(int i=0;i<branches.size();i++){
                if(branches.get(i).getId()== parentid){
                    branches.get(i).setChildren(branchid);
                }
            }
        }else{
            int parentid = -1;
            branch.setParentid(parentid);
        }

        // add bancid to spinner to choose parent for next branch
        numberss.add(branchid);
        spadapter.notifyDataSetChanged();

        //clear coordinates and listview
        coord.clear();
        lvcontent.clear();
        lvadapter.notifyDataSetChanged();

        // inrease the branchid for new one
        branchid++;

        //set buttons to click or not
        isource.setEnabled(false);
        fbranch.setEnabled(false);
        sbranch.setEnabled(true);
        rmarker.setEnabled(false);
        sub.setEnabled(true);
    }

    private void createTree() throws SQLException {

        Tree tree = new Tree();
        tree.setTreeid(treeid);
        tree.setBranches(branches);

        trees.add(tree);
        int startparentid=-1;
        ArrayList<Integer> parents=new ArrayList<>();
        recursive(startparentid,parents);
        //for user
        text.setText("Tree");

        branchid=0;

        rmarker.setEnabled(false);
        amarker.setEnabled(false);
        sbranch.setEnabled(false);
        fbranch.setEnabled(false);
        sub.setEnabled(false);
        parentspin.setEnabled(false);

        ConnectionClass connectionClass = new ConnectionClass();
        Connection con = connectionClass.CONN();
        String query = "INSERT INTO trees(personID,Image,Canvas) VALUES ('"+userID+"','"+blob+"','"+blob+"')";
        Statement stmt = con.createStatement();
        stmt.executeUpdate(query);
        con.close();

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selected = Integer.parseInt(adapterView.getItemAtPosition(i).toString());;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @SuppressLint("DefaultLocale")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        newCanvas.drawCircle(x,y,10,paint);
        Coordinates coordinates = new Coordinates(x,y,branchid);
        coord.add(coordinates);
        lvcontent.add("X = "+ String.format("%.2f", x) + "Y = " + String.format("%.2f",y));
        lvadapter.notifyDataSetChanged();

        isource.invalidate();

        if(coord.size()>0){
            rmarker.setEnabled(true);
        }
        if(coord.size()>1){
            fbranch.setEnabled(true);
        }
        return false;
    }

    // to find and return file name of image on uri
    private String uritofilename(Uri uri) {

        String name = "";
        String scheme = uri.getScheme();

        if (scheme.equals("file")) {
            name = uri.getLastPathSegment();
        }
        else if (scheme.equals("content")) {
            Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
        }
        return name;
    }

    // convert bitmap to make available for canvas
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private Bitmap ProcessingBitmap(){
        Bitmap bm1 = null;
        Bitmap newBitmap = null;

        try {
            bm1 =BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(source1));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap.Config config = bm1.getConfig();
        if(config ==null){
            config = Bitmap.Config.ARGB_8888;
        }

        newBitmap = Bitmap.createBitmap(bm1.getWidth(),bm1.getHeight(),config);

        Drawable d = new BitmapDrawable(getResources(), bm1);
        isource.setBackground(d);

        blob = getBytesFromBitmap(newBitmap);
        newCanvas = new Canvas(newBitmap);
        newCanvas.drawBitmap(bm1,0,0,null);

        paint = new Paint();
        paint.setColor(Color.RED);

        return newBitmap;
    }

    public int getRandomColor(){
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    private int getMidpointofLines() {
        ArrayList<Double> distances = new ArrayList<>();
        double total=0,xdiff,ydiff,dis,midpoint = 0;

        for (int i =0;i<coord.size()-1;i++){
            xdiff = coord.get(i).getX()-coord.get(i+1).getX();
            ydiff = coord.get(i).getY()-coord.get(i+1).getY();
            dis = Math.abs(xdiff) + Math.abs(ydiff);
            distances.add(Math.sqrt(dis));
            total += Math.sqrt(dis);
        }

        int findcoor=0;
        for(int i =0;i<distances.size();i++)
        {
            midpoint += distances.get(i);
            if(midpoint>total/2){
                findcoor=i;
                break;
            }
        }

        return findcoor;
    }

    private void recursive(int parentid, ArrayList<Integer> backparent){
        for(int i=0; i<branches.size(); i++){
            if(branches.get(i).getParentid()== parentid){
                showonlist(backparent,i);
                backparent.add(parentid);
                parentid = branches.get(i).getId();
                branches.remove(i);
                recursive(parentid,backparent);
            }
        }
        if(branches.size()>0){
            parentid = backparent.get(backparent.size()-1);
            backparent.remove(backparent.size()-1);
            recursive(parentid,backparent);
        }
    }

    private void showonlist(ArrayList<Integer> backparent, int i) {
        if(backparent.size()==0){
            lvcontent.add("Branch - " + branches.get(i).getId());
        }else if(backparent.size()==1){
            lvcontent.add("- > Branch - " + branches.get(i).getId());
        }else if(backparent.size()==2){
            lvcontent.add("- - > Branch - " + branches.get(i).getId());
        }else if(backparent.size()==3){
            lvcontent.add("- - - > Branch - " + branches.get(i).getId());
        }else if(backparent.size()==4){
            lvcontent.add("- - - - > Branch - " + branches.get(i).getId());
        }else if(backparent.size()==5){
            lvcontent.add("- - - - - > Branch - " + branches.get(i).getId());
        }else if(backparent.size()==6){
            lvcontent.add("- - - - - - > Branch - " + branches.get(i).getId());
        }else if(backparent.size()==7){
            lvcontent.add("- - - - - - - > Branch - " + branches.get(i).getId());
        }else if(backparent.size()==8){
            lvcontent.add("- - - - - - - - > Branch - " + branches.get(i).getId());
        }else if(backparent.size()==9){
            lvcontent.add("- - - - - - - - - > Branch - " + branches.get(i).getId());
        }else if(backparent.size()==10){
            lvcontent.add("- - - - - - - - - - > Branch - " + branches.get(i).getId());
        }else {
            lvcontent.add("- - - - - - - - - - + > branch - " + branches.get(i).getId());
        }
        lvadapter.notifyDataSetChanged();
    }

    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        if (bitmap!=null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            return stream.toByteArray();
        }
        return null;
    }
}
