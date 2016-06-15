package apps.sayan.onlinepetition;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

public class ViewPetitionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_petition);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Petitions going on");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db=helper.getWritableDatabase();

        DataSet.addPetitionInArray(db,helper);
        RecyclerView recyclerView= (RecyclerView) findViewById(R.id.view_petition_recycler);
        PetitionRecyclerAdapter adapter = new PetitionRecyclerAdapter(this,DataSet.data,db,helper);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
    }
}
