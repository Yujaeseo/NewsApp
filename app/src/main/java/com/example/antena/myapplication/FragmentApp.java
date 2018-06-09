package com.example.antena.myapplication;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentApp extends Fragment {

    private View v;

    private RecyclerView mRecyclerView;
    private List<Item> myDataset;
    private Myadapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private ValueEventListener valueEventListener;

    public FragmentApp() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.app_fragment,container,false);
        return v;
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        mRecyclerView = view.findViewById(R.id.app_recyclerView);

    }

    @Override
    public void onResume() {
        super.onResume();
        ref.orderByChild("topic").equalTo("App").addListenerForSingleValueEvent(valueEventListener);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("news");
        setFirebaseEventlistener();
    }

/*
    @Override
    public void onPause() {
        super.onPause();
        // 중복해서 이벤트 리스너를 설정하게 되면 한번의 이벤트에 여러 번의 리스너가 트리거 되기 때문에
        // 프레그먼트가 포커스를 잃었을 때 마다 리스너를 제거한다.
        ref.removeEventListener(valueEventListener);
    }
*/
    public void setFirebaseEventlistener () {

        valueEventListener = new ValueEventListener() {
            // 데이터베이스에 변경이 생겼을 때, 혹은 초기에 한번 불린다
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                myDataset = new ArrayList<Item>();

                for (DataSnapshot article : dataSnapshot.getChildren()) {

                    Item item = article.getValue(Item.class);

                        if (item.getThumbnail().equals("None")) {
                            item.setViewType(2);
                        } else {
                            item.setViewType(1);
                        }

                        myDataset.add(item);
                }


                mAdapter = new Myadapter(myDataset, new Myadapter.OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(int position, View view,String url) {
// load url not using webview
//                        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
//                        startActivity(viewIntent);

                        Intent intent = new Intent(getActivity(),Webviewactivity.class);
                        intent.putExtra("newsUrl",url);
                        startActivity(intent);

                    }
                });

                //Async하게 데이터를 가져오기 때문에 callback 안에서 어탭터, 리사이클러뷰를 설정해준다.

                mRecyclerView.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("test","loadArticle : onCancelled",databaseError.toException());
            }
        };
    }
}
