package apps.sayan.onlinepetition;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class SignActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        int position=getIntent().getExtras().getInt("position");

        Information temp;
        temp=DataSet.data.get(position);

        final Information information=temp;

        ((TextView)findViewById(R.id.title)).setText(information.title);
        ((TextView)findViewById(R.id.signatures)).setText(String.valueOf(information.signatures.size()));
        ((TextView)findViewById(R.id.description)).setText(information.description);
        ((TextView)findViewById(R.id.decisionMaker)).setText(information.decision_maker);
        ((TextView)findViewById(R.id.maker)).setText(information.maker);

        final DBHelper helper=new DBHelper(this);
        final SQLiteDatabase db=helper.getWritableDatabase();

        findViewById(R.id.signButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DataSet.loggedIn){
                    boolean success=DataSet.signPetition(information,db,helper);
                    if(success) {
                        DataSet.addPetitionInArray(db, helper);
                        ((TextView) findViewById(R.id.signatures)).setText(String.valueOf(information.signatures.size()));
                    }
                    else
                        Snackbar.make(v,"You have already signed the petition",Snackbar.LENGTH_SHORT).show();
                }
                else
                Snackbar.make(v,"Please Log in to continue",Snackbar.LENGTH_SHORT).show();
            }
        });
    }

}
