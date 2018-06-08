package com.example.antena.myapplication;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.nfc.Tag;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    //firebase
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private GoogleSignInClient mGoogleSignInClient;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    private List<Item> myDataset;
    private ActionBarDrawerToggle mdrawerToggle;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private static final String TAG = "Item";

    private String[] mListItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("news");

        setupFirebaseListener();

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // 네비게이션 리스너 설정
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationview);
        navigationView.setNavigationItemSelectedListener(this);

        myDataset = new ArrayList<Item>();

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        ref.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // check practice https://firebase.googleblog.com/2014/04/best-practices-arrays-in-firebase.html?m=1
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    for (DataSnapshot article : child.getChildren()) {

                        Item item = article.getValue(Item.class);

                        if (item.getThumbnail().equals("None")) {
                            item.setViewType(2);
                        } else {
                            item.setViewType(1);
                        }

                        myDataset.add(item);

                    }
                }

                mAdapter = new Myadapter(myDataset, new Myadapter.OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(int position, View view,String url) {
// load url not using webview
/*                        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                        startActivity(viewIntent);*/
                        Intent intent = new Intent(MainActivity.this,Webviewactivity.class);
                        intent.putExtra("newsUrl",url);
                        startActivity(intent);

                    }
                });

                mRecyclerView.setAdapter(mAdapter);

            }



            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG,"loadArticle : onCancelled",databaseError.toException());
            }
        });

        initInstancesDrawer();

    }



    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null){
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
    }

    // https://stackoverflow.com/questions/26754940/appcompatv7-v21-navigation-drawer-not-showing-hamburger-icon

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mdrawerToggle.syncState();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mdrawerToggle.onConfigurationChanged(newConfig);

    }

    private void setupFirebaseListener(){

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                // user signed in
                if (user!= null) {
                    // don't do anything
                } // signed out
                else {
                    Toast.makeText(getApplicationContext(),"로그아웃",Toast.LENGTH_SHORT).show();

                    mGoogleSignInClient.signOut();
                    // 로그인 액티비티로 보낸다.
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    //액티비티 스택의 모든 것을 제거한다.
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };
    }


    private void initInstancesDrawer(){

        mToolbar = (Toolbar) findViewById(R.id.maintoolbar);
        mTabLayout = (TabLayout) findViewById(R.id.tablayout);
        setSupportActionBar(mToolbar);

        mdrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        mdrawerToggle.setDrawerIndicatorEnabled(true);

        mTabLayout.addTab(mTabLayout.newTab().setText("Tab One"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Tab Two"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Tab Three"));

    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);

        switch (item.getItemId()){

            case R.id.logout_button:{
                Log.d(TAG,"attempting to sign out the user.");
                //https://stackoverflow.com/questions/38707133/google-firebase-sign-out-and-forget-user-in-android-app
                //firebase logout
                FirebaseAuth.getInstance().signOut();
                // Google sign out
                //Google sign out -> 로그인 시 다시 아이디를 선택해야 함.

                break;
            }
            // navigation drawer를 닫는다
        }

        mDrawerLayout.closeDrawers();
        return true;
    }
}
