package apps.sayan.onlinepetition;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by SAYAN on 13-05-2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    protected final String ID="ID";
    protected final String USER_TABLE="USERS";
    protected final String COLUMN_EMAIL="EMAIL";
    protected final String COLUMN_NAME="NAME";
    protected final String COLUMN_PASSWORD="PASSWORD";
    protected final String COLUMN_PETITION_WRITTEN="PETITION_WRITTEN";
    protected final String COLUMN_PETITION_SIGNED="PETITION_SIGNED";

    protected final String PETITION_TABLE="PETITIONS";
    protected final String COLUMN_MAKER="MAKER";
    protected final String COLUMN_TITLE="TITLE";
    protected final String COLUMN_DECISION_MAKER="DECISION";
    protected final String COLUMN_DESCRIPTION="DESCRIPTION";
    protected final String COLUMN_SIGNATURES="SIGNATURES";

    public DBHelper(Context context) {
        super(context, "OnlinePetitionDB", null, 1);
    }
    public DBHelper(Context context, int version) {
        super(context, "OnlinePetitionDB", null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE USERS (ID INTEGER PRIMARY KEY, EMAIL TEXT, NAME TEXT, PASSWORD TEXT, PETITION_WRITTEN TEXT, PETITION_SIGNED TEXT)");
            db.execSQL("CREATE TABLE PETITIONS (ID INTEGER PRIMARY KEY, TITLE TEXT, DECISION TEXT, DESCRIPTION TEXT, SIGNATURES TEXT, MAKER TEXT)");
            Log.d("Database","MASTER added" );
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            Log.d("Database","Trying to upgrade");
            db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + PETITION_TABLE);
            onCreate(db);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db,oldVersion,newVersion);
    }
}
