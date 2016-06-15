package apps.sayan.onlinepetition;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

public class WritePetitionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_petition);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Write a petition");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Context context=this;

        final TextInputEditText titleText=(TextInputEditText)findViewById(R.id.write_title);
        final TextInputEditText decisionText=(TextInputEditText)findViewById(R.id.write_decision);
        final TextInputEditText descriptionText=(TextInputEditText)findViewById(R.id.write_description);

        final DBHelper helper=new DBHelper(this);
        final SQLiteDatabase db=helper.getWritableDatabase();

        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.submit_fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title, decisionMaker, description;
                title=titleText.getText().toString();
                decisionMaker=decisionText.getText().toString();
                description=descriptionText.getText().toString();

                if(title.equals(""))
                    Snackbar.make(v,"Please enter the title",Snackbar.LENGTH_LONG).show();
                else if(description.equals(""))
                    Snackbar.make(v,"Please enter a brief description",Snackbar.LENGTH_LONG).show();
                else {
                    Information information = new Information();
                    information.title = title;
                    information.decision_maker = decisionMaker;
                    information.description = description;
                    information.maker = DataSet.user_name;
                    information.signatures=new ArrayList<>();
                    boolean success=DataSet.addNewPetition(context, information,db,helper);

                    titleText.setText("");
                    decisionText.setText("");
                    descriptionText.setText("");
                    if(success)
                        Snackbar.make(v,"Petition successfully added",Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

}
