package com.example.antena.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.widget.DrawerLayout;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Item> myDataset;

    private FirebaseDatabase database;
    private DatabaseReference ref;
    private static final String TAG = "Item";

    private String[] mListItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("news");

        mListItems = getResources().getStringArray(R.array.drawerItemArray);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item,mListItems));

        myDataset = new ArrayList<Item>();

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
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
    }
}
