package com.grapevine.grapevine;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Base64;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.grapevine.grapevine.Models.Branch;
import com.grapevine.grapevine.Models.Coordinates;
import com.grapevine.grapevine.Models.Tree;
import com.grapevine.grapevine.Utils.ConnectionClass;
import com.grapevine.grapevine.Utils.PreferenceUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

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
    ArrayAdapter<Integer> spadapter;
    ArrayAdapter lvadapter;
    final int RQS_IMAGE = 1;
    Uri source1;
    int userID;
    Bitmap newBitmap;

    ArrayList<String> lvcontent;
    ArrayList<Integer> numberss;
    ArrayList<Coordinates> coord = new ArrayList<>();
    ArrayList<Bitmap> savedCanvas;
    ArrayList<Branch> branches;
    int branchid = 0;
    int treeid= 0;
    public Tree tree;

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

        if(getArguments()!=null){
            userID = getArguments().getInt("userid");
        }

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

        loadData();
        load.setOnClickListener(this);

        spadapter = new ArrayAdapter<Integer>(getActivity(),android.R.layout.simple_spinner_item,numberss);
        spadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        parentspin.setAdapter(spadapter);

        parentspin.setOnItemSelectedListener(this);

        lvadapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,lvcontent);
        lvadapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,lvcontent);
        listView.setAdapter(lvadapter);


        amarker.setOnClickListener(this);
        rmarker.setOnClickListener(this);
        fbranch.setOnClickListener(this);
        sbranch.setOnClickListener(this);
        sub.setOnClickListener(this);

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
                    isource.setImageBitmap(ProcessingBitmap(null));
                    layoutopvis.setVisibility(View.VISIBLE);

                    rmarker.setEnabled(false);
                    fbranch.setEnabled(false);
                    sbranch.setEnabled(true);
                    sub.setEnabled(false);

                    break;
            }
        }
    }

    private void createBranch(){

        lvcontent.clear();
        lvadapter.notifyDataSetChanged();

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
        for(int i = 0; i<coord.size();i++){
            branch.setCoordinates(coord.get(i));
        }
        //branch.setChildren();

        // set parentid for branch and childrenid for its parents
        if(parentspin.getSelectedView()!=null){
            int parentid = selected;
            branch.setParentid(parentid);

        }else{
            int parentid = -1;
            branch.setParentid(parentid);
        }

        // save the canvas of branch
        Bitmap willsave = newBitmap.copy(newBitmap.getConfig(),true);
        savedCanvas.add(willsave);

        // add bancid to spinner to choose parent for next branch
        numberss.add(branchid);
        spadapter.notifyDataSetChanged();

        branchid++;


        SaveData saveData = new SaveData(lvcontent,branches);
        saveData.execute();

        //clear coordinates and listview
        coord.clear();

        // inrease the branchid for new one

        //set buttons to click or not
        isource.setEnabled(false);
        fbranch.setEnabled(false);
        sbranch.setEnabled(true);
        rmarker.setEnabled(false);
        sub.setEnabled(true);
    }

    private void createTree() throws SQLException {

        tree = new Tree(treeid,getStringFromBitmap(savedCanvas.get(0)),
                getStringFromBitmap(savedCanvas.get(savedCanvas.size()-1)),userID);

        for(int i=0;i<branches.size();i++){
            int parentid = branches.get(i).getParentid();
            for(int j=0;j<branches.size();j++){
                if(branches.get(j).getId()== parentid){
                    branches.get(j).setChildren(branches.get(i).getId());
                }
            }
        }
        for(int i = 0; i<branches.size();i++){
            tree.setBranches(branches.get(i));
        }

        SaveTask saveTask = new SaveTask();
        saveTask.execute();

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
        load.setEnabled(true);

        restoreData();
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
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        newCanvas.drawCircle(x,y,10,paint);
        Coordinates coordinates = new Coordinates(x,y,branchid);
        coord.add(coordinates);
        lvcontent.add("X = "+ String.valueOf(x) +" "+ "Y = " + String.valueOf(y));
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
    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private Bitmap ProcessingBitmap(Bitmap bitmap){
        Bitmap bm1 = null;
        newBitmap = null;

        if(bitmap==null){
            try {
                bm1 =BitmapFactory.decodeStream(Objects.requireNonNull(getContext()).getContentResolver().openInputStream(source1));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if(bm1==null){
            bm1 = bitmap;
        }
        Bitmap.Config config = bm1.getConfig();
        if(config ==null){
            config = Bitmap.Config.ARGB_8888;
        }
        newBitmap = Bitmap.createBitmap(bm1.getWidth(),bm1.getHeight(),config);

        Drawable d = new BitmapDrawable(getResources(), bm1);
        isource.setBackground(d);

        savedCanvas.add(bm1);

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
            if(backparent.size()>0){
                parentid = backparent.get(backparent.size()-1);
                backparent.remove(backparent.size()-1);
                recursive(parentid,backparent);
            }
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

    public static String getStringFromBitmap(Bitmap bitmap) {
        if (bitmap!=null) {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
            byte[] imgByte = stream.toByteArray();
            return Base64.encodeToString(imgByte,Base64.DEFAULT);
        }
        return null;
    }

    private Bitmap getBitmapFromString(String stringPicture) {
        byte[] decodedString = Base64.decode(stringPicture, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    @SuppressLint("StaticFieldLeak")
    public class SaveTask extends AsyncTask<String, String, Void> {
        ConnectionClass connectionClass = new ConnectionClass();

        @Override
        protected Void doInBackground(String... strings) {
            try {
                Connection con = connectionClass.CONN();
                if(con==null){
                }else {

                    String query = "INSERT INTO trees(personID,Image,Canvas) VALUES ('"+userID+"','"+tree.getTimage()+"','"+tree.getCimage()+"')";

                    Statement stmt = con.createStatement();
                        stmt.executeUpdate(query);

                    String query2="SELECT * FROM trees WHERE personID = '"+userID+"' and Canvas = '"+tree.getCimage()+"'";
                    Statement stmt2 = con.createStatement();
                    ResultSet rs = stmt2.executeQuery(query2);
                    int treeid = 0;
                    while (rs.next()){
                        treeid = rs.getInt(1);
                    }
                    tree.setTreeid(treeid);


                    for(int i=0;i<tree.getBranches().size();i++){
                        query = "INSERT INTO branches(parentID,treeID) VALUES ('"+tree.getBranches().get(i).getParentid()+"','"+tree.getTreeid()+"')";
                        stmt.executeUpdate(query);

                        String query3="SELECT * FROM branches WHERE treeID = '"+tree.getTreeid()+"' and parentID = '"+tree.getBranches().get(i).getParentid()+"'";
                        Statement stmt3 = con.createStatement();
                        ResultSet rs2 = stmt3.executeQuery(query3);

                        int branchid = 0;
                        while (rs2.next()){
                            branchid = rs2.getInt(1);
                        }
                        tree.getBranches().get(i).setId(branchid);

                        for(int j=0;j < tree.getBranches().get(i).getCoordinates().size();j++){
                            query = "INSERT INTO coordinates(x,y,branchID) VALUES ('"+tree.getBranches().get(i).getCoordinates().get(j).getX()+"'," +
                                    "'"+tree.getBranches().get(i).getCoordinates().get(j).getY()+"','"+tree.getBranches().get(i).getId()+"')";
                            stmt.executeUpdate(query);
                        }
                    }
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

    @SuppressLint("StaticFieldLeak")
    public class SaveData extends AsyncTask<String, String, Void> {
        ArrayList<String> newContent;
        ArrayList<Branch> newBranches;
        public SaveData(ArrayList<String> lvcontent, ArrayList<Branch> branches) {
            this.newContent = new ArrayList<>();
            this.newContent.addAll(lvcontent);
            this.newBranches = new ArrayList<>();
            this.newBranches.addAll(branches);

        }

        @Override
        protected Void doInBackground(String... strings) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared preferences",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();

            ArrayList<String> stringBitmaps= new ArrayList<>(); //stringBranch = new ArrayList<>();

            for(int i=0;i<savedCanvas.size();i++){
                //if(i<savedCanvas.size()){
                    String bitmap = getStringFromBitmap(savedCanvas.get(i));
                    stringBitmaps.add(bitmap);
               // }
              /*  if(i<branches.size()){
                    String branch = branches.get(i).toString();
                    stringBranch.add(branch);
                }*/
            }


            String jsonbrances = gson.toJson(newBranches);
            String jsoncanvas = gson.toJson(stringBitmaps);
            String jsonnumber = gson.toJson(numberss);
            String jsoncontent = gson.toJson(newContent);

            editor.putInt("branchid",branchid);
            editor.putString("branch",jsonbrances);
            editor.putString("numberss",jsonnumber);
            editor.putString("lvcontent",jsoncontent);
            editor.putString("savedCanvas",jsoncanvas);
            editor.apply();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


    private void restoreData(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared preferences",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("branchid",0);
        editor.putString("branch",null);
        editor.putString("savedCanvas",null);
        editor.putString("numberss",null);
        editor.putString("lvcontent",null);
        editor.apply();
    }


    @SuppressLint("NewApi")
    private void loadData(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared preferences",MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonBranch = sharedPreferences.getString("branch",null);
        String jsonCanvas = sharedPreferences.getString("savedCanvas",null);
        String jsonNumbers = sharedPreferences.getString("numberss",null);
        String jsonContent = sharedPreferences.getString("lvcontent",null);

        branchid = sharedPreferences.getInt("branchid",0);
        if(branchid!=0){

            ArrayList<String> stringBitmap;//,stringBranch = new ArrayList<>();
            savedCanvas = new ArrayList<>();

            Type type = new TypeToken<ArrayList<Branch>>() {}.getType();
            branches = gson.fromJson(jsonBranch,type);

            Type type2 = new TypeToken<ArrayList<String>>() {}.getType();
            stringBitmap = gson.fromJson(jsonCanvas,type2);
            for(int i=0;i<stringBitmap.size();i++) { //|| i<stringBranch.size();i++){
                //   if(i<stringBitmap.size())
                savedCanvas.add(getBitmapFromString(stringBitmap.get(i)));
                // if(i<stringBitmap.size())
                //   branches.add(stringBranch.get(i));

            }
            Type type3 = new TypeToken<ArrayList<Integer>>() {}.getType();
            numberss = gson.fromJson(jsonNumbers,type3);
            Type type4 = new TypeToken<ArrayList<String>>() {}.getType();
            lvcontent = gson.fromJson(jsonContent,type4);
        }

        if(branches == null){
            branches = new ArrayList<>();
        }else{
        }
        if(savedCanvas == null){
            savedCanvas = new ArrayList<>();
        }else{
            layoutopvis.setVisibility(View.VISIBLE);
            load.setEnabled(false);
            sub.setEnabled(true);
            rmarker.setEnabled(false);
            amarker.setEnabled(true);
            sbranch.setEnabled(true);
            fbranch.setEnabled(false);
            isource.setImageBitmap(ProcessingBitmap(savedCanvas.get(savedCanvas.size()-1)));
        }
        if(numberss == null){
            numberss = new ArrayList<>();
        }
        if(lvcontent == null){
            lvcontent = new ArrayList<>();
        }
    }

}
