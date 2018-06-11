package com.example.antena.myapplication.mainview;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.antena.myapplication.loginview.LoginActivity;
import com.example.antena.myapplication.R;
import com.example.antena.myapplication.wordview.WordActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    //firebase

    private FirebaseDatabase database;
    private DatabaseReference ref;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference userRef;
    private NavigationView navigationView;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    private List<Item> myDataset;
    private ActionBarDrawerToggle mdrawerToggle;
    private static final String TAG = "Item";

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;

    private TabLayout mTabLayout;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;

    private TextView userNameView;
    private TextView userEmailView;
    private CircleImageView userImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("news");

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        userRef = ref.child("users").child(mFirebaseUser.getUid());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        setupFirebaseListener();

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mTabLayout = (TabLayout) findViewById(R.id.tablayout);

        // add fragment
        viewPagerAdapter.AddFragment(new FragmentTech(),"Tech");
        viewPagerAdapter.AddFragment(new FragmentHard(),"Hard");
        viewPagerAdapter.AddFragment(new FragmentApp(),"Apps");

        viewPager.setAdapter(viewPagerAdapter);
        mTabLayout.setupWithViewPager(viewPager);

        // 네비게이션 리스너 설정
        navigationView = (NavigationView) findViewById(R.id.navigationview);
        navigationView.setNavigationItemSelectedListener(this);

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

//https://stackoverflow.com/questions/33560219/in-android-how-to-set-navigation-drawer-header-image-and-name-programmatically-i
        View v = navigationView.getHeaderView(0);
        ((TextView)v.findViewById(R.id.name)).setText(mFirebaseUser.getDisplayName());
        ((TextView)v.findViewById(R.id.email)).setText(mFirebaseUser.getEmail());
        CircleImageView profileView = ((CircleImageView)v.findViewById(R.id.drawer_profile_image));

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.mipmap.ic_launcher);

        Glide.with(profileView).load(mFirebaseUser.getPhotoUrl()).apply(options).into(profileView);

        //userNameView.setText(mFirebaseUser.getDisplayName());
        //userEmailView.setText(mFirebaseUser.getEmail());

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
        setSupportActionBar(mToolbar);

        mdrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        mdrawerToggle.setDrawerIndicatorEnabled(true);

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //item.setChecked(true);

        switch (item.getItemId()){

            case R.id.my_word_list: {
                Intent intent = new Intent(MainActivity.this,WordActivity.class);
                startActivity(intent);
                break;
            }

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









// pager설정 전 oncreate 안에 내용

/*
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
                //mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

                viewPager = (ViewPager) findViewById(R.id.viewpager);
                viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                mTabLayout = (TabLayout) findViewById(R.id.tablayout);

                // add fragment
                viewPagerAdapter.AddFragment(new FragmentTech(),"Tech");
                viewPagerAdapter.AddFragment(new FragmentHard(),"Hard");
                viewPagerAdapter.AddFragment(new FragmentApp(),"Apps");

                viewPager.setAdapter(viewPagerAdapter);
                mTabLayout.setupWithViewPager(viewPager);



                // 네비게이션 리스너 설정
                NavigationView navigationView = (NavigationView) findViewById(R.id.navigationview);
                navigationView.setNavigationItemSelectedListener(this);
/*
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
//                        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
//                        startActivity(viewIntent);
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
*/
