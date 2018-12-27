package com.example.imtiaz.lab_tasks.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.imtiaz.lab_tasks.R;
import com.example.imtiaz.lab_tasks.fragments.UserHome;
import com.example.imtiaz.lab_tasks.fragments.Camera_Frag;
import com.example.imtiaz.lab_tasks.fragments.Timeline_Frag;
import com.google.firebase.auth.FirebaseAuth;

import static maes.tech.intentanim.CustomIntent.customType;


public class Home extends AppCompatActivity implements Timeline_Frag.TabListener {

    // views
    private Toolbar mToolbar;
    private TabLayout mTablayout;

    // fragment manager
    private FragmentManager fm;
    private FragmentTransaction ft;

    // sharedPreferences
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mToolbar = (Toolbar) findViewById(R.id.toolBar_home);
        setSupportActionBar(mToolbar);

        mTablayout = (TabLayout) findViewById(R.id.tablayout_home);

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();

        mTablayout.getTabAt(1).select();
        ft.add(R.id.framelayout_home, new Timeline_Frag());
        ft.commit();

        mTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int id = tab.getPosition();
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                switch (id){
                    case 0:
                        ft.replace(R.id.framelayout_home, new Camera_Frag());
                        ft.commit();
                        break;
                    case 1:
                        ft.replace(R.id.framelayout_home, new Timeline_Frag());
                        ft.commit();
                        break;
                    case 2:
                        ft.replace(R.id.framelayout_home, new UserHome());
                        ft.commit();
                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //overriding toolbar with menufile
        getMenuInflater().inflate(R.menu.menu_main,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.setting_item:
                startActivity(new Intent(this, Settings.class));
                customType(this,"left-to-right");
                break;
            case R.id.logout_item:
                // logout user from firebase
                FirebaseAuth.getInstance().signOut();
                clearCurrentUser();
                sendToUserAuthentication();
                customType(this,"right-to-left");
                break;
            case R.id.search_item:
                // implementing searchview
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearCurrentUser() {
        mPreferences = this.getPreferences(MODE_PRIVATE);
        mEditor = mPreferences.edit();
        mEditor.putString("username", null);
        mEditor.putString("email", null);
        mEditor.putString("password", null);
        mEditor.commit();
    }

    private void sendToUserAuthentication() {
        startActivity(new Intent(Home.this, UserAuthentication.class));
        finish();
    }


    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.exit_message);
        builder.setTitle(R.string.exit);
        builder.setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Home.this, UserAuthentication.class));
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        });
        builder.show();

    }


    @Override
    public void selectTab(int position) {
        mTablayout.getTabAt(position).select();
    }


}
