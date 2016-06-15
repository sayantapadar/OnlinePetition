package apps.sayan.onlinepetition;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by SAYAN on 04-04-2016.
 */

/**Format for file...
 * month
 * previousAmount
 * savedAmount
 * spent
 * spendAmount, spendAmount, spendAmount...
 * spendDetail, spendDetail, spendDetail...
 */
public class DataSet {
    static ArrayList<Information> data=new ArrayList<>();

    static String user_email="";
    static String user_name="";
    static int user_id;
    static boolean loggedIn=false;
    static ArrayList<Integer> petitionWritten;
    static ArrayList<Integer> petitionSigned;


    public static void addPetitionInArray(SQLiteDatabase db, DBHelper helper){
        Cursor cursor=db.query(helper.PETITION_TABLE,new String[]{helper.ID, helper.COLUMN_TITLE, helper.COLUMN_DECISION_MAKER, helper.COLUMN_DESCRIPTION, helper.COLUMN_SIGNATURES, helper.COLUMN_MAKER},null,null,null,null,null);
        if(cursor.getCount()>0) {
            cursor.moveToFirst();
            data.clear();
            while (!cursor.isAfterLast()) {
                Information information = new Information();
                information.title = cursor.getString(cursor.getColumnIndex(helper.COLUMN_TITLE));
                information.decision_maker = cursor.getString(cursor.getColumnIndex(helper.COLUMN_DECISION_MAKER));
                information.description = cursor.getString(cursor.getColumnIndex(helper.COLUMN_DESCRIPTION));
                information.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(helper.ID)));
                information.maker = cursor.getString(cursor.getColumnIndex(helper.COLUMN_MAKER));
                information.signatures=getIdArray(cursor.getString(cursor.getColumnIndex(helper.COLUMN_SIGNATURES)));
                data.add(0, information);
                cursor.moveToNext();
            }
        }
        cursor.close();
    }

    public static boolean addNewPetition(Context context, Information information, SQLiteDatabase db, DBHelper helper){
        Cursor cursor=db.query(helper.PETITION_TABLE,new String[]{helper.COLUMN_TITLE},helper.COLUMN_TITLE+" LIKE?",new String[]{information.title},null,null,null);
        if(cursor.getCount()==0) {
            ContentValues values = new ContentValues();
            values.put(helper.COLUMN_TITLE, information.title);
            values.put(helper.COLUMN_DECISION_MAKER, information.decision_maker);
            values.put(helper.COLUMN_DESCRIPTION, information.description);
            values.put(helper.COLUMN_SIGNATURES, getStringofId(information.signatures));
            values.put(helper.COLUMN_MAKER, information.maker);

            db.insert(helper.PETITION_TABLE, null, values);
            cursor.close();
            cursor = db.query(helper.PETITION_TABLE, new String[]{helper.ID, helper.COLUMN_TITLE}, helper.COLUMN_TITLE + " LIKE?", new String[]{information.title}, null, null, null);
            cursor.moveToFirst();
            int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(helper.ID)));
            cursor.close();
            petitionWritten.add(id);
            String petitionWrittenString = getStringofId(petitionWritten);
            values.clear();
            values.put(helper.COLUMN_PETITION_WRITTEN, petitionWrittenString);
            db.update(helper.USER_TABLE, values, helper.COLUMN_EMAIL + " LIKE?", new String[]{user_email});
            return true;
        }
        else {
            new AlertDialog.Builder(context).setTitle("Duplicate petition").setMessage("This petition already exists").create().show();
            return false;
        }
    }

    public static boolean login(String email, String password, SQLiteDatabase db, DBHelper helper){
        boolean success=false;
        Cursor cursor=db.query(helper.USER_TABLE,new String[]{helper.ID,helper.COLUMN_EMAIL,helper.COLUMN_PASSWORD,helper.COLUMN_NAME,helper.COLUMN_PETITION_WRITTEN, helper.COLUMN_PETITION_SIGNED}
                    ,helper.COLUMN_EMAIL+" LIKE?",new String[]{email},null,null,null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            if(cursor.getString(cursor.getColumnIndex(helper.COLUMN_PASSWORD)).equals(password))
                success=true;
        }
        if(success){
            user_name=cursor.getString(cursor.getColumnIndex(helper.COLUMN_NAME));
            user_email=cursor.getString(cursor.getColumnIndex(helper.COLUMN_EMAIL));
            user_id= Integer.parseInt(cursor.getString(cursor.getColumnIndex(helper.ID)));
            petitionSigned=getIdArray(cursor.getString(cursor.getColumnIndex(helper.COLUMN_PETITION_SIGNED)));
            petitionWritten=getIdArray(cursor.getString(cursor.getColumnIndex(helper.COLUMN_PETITION_WRITTEN)));
            loggedIn=true;
        }
        cursor.close();
        return success;
    }

    public static boolean register(final Context context, final String email, final String password, final String name, final SQLiteDatabase db, final DBHelper helper){
        Cursor cursor=db.query(helper.USER_TABLE, new String[]{helper.COLUMN_EMAIL},helper.COLUMN_EMAIL+" LIKE?",new String[]{email},null,null,null);
        if(cursor.getCount()!=0){
            final AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setMessage("Already registered")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create();
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            cursor.close();
            return false;
        }
        else{
            cursor.close();
            ContentValues values=new ContentValues();
            values.put(helper.COLUMN_EMAIL,email);
            values.put(helper.COLUMN_PASSWORD, password);
            values.put(helper.COLUMN_NAME, name);
            db.insert(helper.USER_TABLE,null,values);

            /*Snackbar.make(new View(context),"Registration successful",Snackbar.LENGTH_LONG)
                    .setAction("Log In", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            login(context,email,password,db,helper);
                        }
                    }).show();*/
            Log.d("Registered",email);
            return true;
        }
    }

    static boolean signPetition(Information information, SQLiteDatabase db, DBHelper helper){
        if(!petitionSigned.contains(information.id)) {
            petitionSigned.add(information.id);
            information.signatures.add(user_id);

            ContentValues values = new ContentValues();
            values.put(helper.COLUMN_SIGNATURES, getStringofId(information.signatures));
            db.update(helper.PETITION_TABLE, values, helper.ID + " LIKE?", new String[]{String.valueOf(information.id)});

            values.clear();
            values.put(helper.COLUMN_PETITION_SIGNED, getStringofId(petitionSigned));
            db.update(helper.USER_TABLE, values, helper.COLUMN_EMAIL + " LIKE?", new String[]{user_email});
            return true;
        }
        else{
            return false;
        }
    }


    static ArrayList<Integer> getIdArray(String info){
        ArrayList<Integer> ids=new ArrayList<>();
        if(info==null)
            return ids;
        StringTokenizer infoTokens= new StringTokenizer(info,";");
        while(infoTokens.hasMoreTokens())
        ids.add(Integer.valueOf(infoTokens.nextToken()));
        return ids;
    }
    static String getStringofId(ArrayList<Integer> ids){
        String info="";
        if(ids.size()==0)
            return info;
        else {
            info = String.valueOf(ids.get(0));
            for (int i = 1; i < ids.size(); i++)
                info = info + ";" + ids.get(i);
        }
        return info;
    }

    public static ArrayList<String> searchByTitle(String search) {
        ArrayList<String> searchResult=new ArrayList<>();
        Log.d("Search",search);
        for(int i=0; i<data.size(); i++){
            if(data.get(i).title.toLowerCase().contains(search.toLowerCase()))
                searchResult.add(data.get(i).title);
        }
        return searchResult;
    }
    public static Information searchByTitleGetObject(String title) {
        Information info = null;
        for(int i=0; i<data.size(); i++){
            if(data.get(i).title.equals(title))
                info=data.get(i);
        }
        return info;
    }
}
