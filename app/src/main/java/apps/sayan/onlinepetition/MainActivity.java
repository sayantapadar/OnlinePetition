package apps.sayan.onlinepetition;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextInputEditText searchInput;
    CardView searchContainer;
    CardView startContainer;
    Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Context context;
    CoordinatorLayout primaryContainer;

    DBHelper helper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchInput= (TextInputEditText) findViewById(R.id.search_text);
        primaryContainer=(CoordinatorLayout)findViewById(R.id.primary_container);
        searchContainer= (CardView) findViewById(R.id.search_container);
        startContainer = (CardView) findViewById(R.id.start_petition_container) ;
        searchContainer.setCardElevation(0);
        searchInput.setBackground(getResources().getDrawable(R.drawable.search_field_background));
        searchInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableSearch();
            }
        });

        context=this;
        helper=new DBHelper(context);
        db=helper.getWritableDatabase();

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                //Checking if the item is in checked state or not, if not make it in checked state
                if (item.isChecked())
                    item.setChecked(false);
                else
                    item.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (item.getItemId()) {
                    case R.id.view_petition:
                        startActivity(new Intent(context,ViewPetitionActivity.class));
                        return true;
                    default:
                        return true;
                }
            }
        });
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle= new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        startContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!DataSet.loggedIn) {

                    final AlertDialog alertDialog= new AlertDialog.Builder(context)
                            .setTitle("Login or Register")
                            .setMessage("Please login or register to continue")
                            .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    login();
                                }})
                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }})
                            .setNegativeButton("Register", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    register();
                                }
                            }).create();
                    alertDialog.show();
                    alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                }
                else
                    startActivity(new Intent(context,WritePetitionActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.email)).setText(DataSet.user_email);
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.username)).setText(DataSet.user_name);


        navigationView.getHeaderView(0).findViewById(R.id.header_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_login) {
            login();
            return true;
        }
        if(id==R.id.action_register){
            register();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(!startContainer.isShown())
            searchDisable();
        else
            super.onBackPressed();
    }

    private void searchDisable() {
        AppCompatImageView logoImage= (AppCompatImageView) findViewById(R.id.logo_image);
        logoImage.setVisibility(View.VISIBLE);
        searchContainer.setCardElevation(0);
        searchInput.setBackground(getResources().getDrawable(R.drawable.search_field_background));
        toolbar.setVisibility(View.VISIBLE);
        startContainer.setVisibility(View.VISIBLE);
        searchInput.clearFocus();
        searchContainer.clearFocus();
    }
    private void enableSearch() {
        AppCompatImageView logoImage= (AppCompatImageView) findViewById(R.id.logo_image);
        logoImage.setVisibility(View.GONE);
        searchContainer.setCardElevation(10);
        searchInput.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        toolbar.setVisibility(View.GONE);
        startContainer.setVisibility(View.GONE);
        DataSet.addPetitionInArray(db,helper);

        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String search=searchInput.getText().toString();
                    final ArrayList<String> searchResult = DataSet.searchByTitle(search);
                    Log.d("Size of searchList", String.valueOf(searchResult.size()));
                    ArrayAdapter<String> searchAdapter= new ArrayAdapter<>(context, android.R.layout.simple_selectable_list_item, searchResult);
                    new AlertDialog.Builder(context).setAdapter(searchAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Information information=DataSet.searchByTitleGetObject(searchResult.get(which));
                            int position=DataSet.data.indexOf(information);
                            startActivity(new Intent(context,SignActivity.class).putExtra("position",position));
                        }
                    }).setTitle("Search Results").create().show();
                    searchInput.setText("");
                    return true;
                }
                return false;
            }
        });
    }

    public void register(){
        final AlertDialog alertDialog= new AlertDialog.Builder(context)
                .setView(R.layout.register)
                .setTitle("Registration")
                .setPositiveButton("Register", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username, email, password;
                email=((EditText)alertDialog.findViewById(R.id.register_email_text)).getText().toString();
                username=((EditText)alertDialog.findViewById(R.id.register_username)).getText().toString();
                password=((EditText)alertDialog.findViewById(R.id.register_password)).getText().toString();
                Log.d("Register",email);
                boolean success=DataSet.register(context,email,password,username,db,helper);
                if(success)
                    alertDialog.dismiss();
                else{
                    alertDialog.dismiss();
                    Snackbar.make(primaryContainer,"Registered as "+email,Snackbar.LENGTH_LONG)
                            .setAction("Log in", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    boolean loggedSuccess=DataSet.login(email,password,db,helper);
                                    if(loggedSuccess)
                                        Snackbar.make(v,"Logged in as "+email,Snackbar.LENGTH_LONG).show();
                                    else
                                        new AlertDialog.Builder(context)
                                                .setTitle("Error logging in")
                                                .setMessage("Either the username or the password was incorrect")
                                                .create().show();
                                }
                            }).show();
                }
            }
        });
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.email)).setText(DataSet.user_email);
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.username)).setText(DataSet.user_name);
    }
    public void login(){
        final AlertDialog alertDialog= new AlertDialog.Builder(context)
                .setView(R.layout.login)
                .setTitle("Login")
                .setPositiveButton("Log In", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email=((EditText)alertDialog.findViewById(R.id.login_email_text)).getText().toString();
                password=((EditText)alertDialog.findViewById(R.id.login_password)).getText().toString();
                boolean success= DataSet.login(email,password,db,helper);
                ((TextView) navigationView.getHeaderView(0).findViewById(R.id.email)).setText(DataSet.user_email);
                ((TextView) navigationView.getHeaderView(0).findViewById(R.id.username)).setText(DataSet.user_name);
                alertDialog.dismiss();
                if(!success)
                    new AlertDialog.Builder(context)
                            .setTitle("Error logging in")
                            .setMessage("Either the username or the password was incorrect")
                            .create().show();
                else
                    Snackbar.make(primaryContainer,"Logged in as "+DataSet.user_email,Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
