package com.example.antena.myapplication;

import android.content.Intent;
import android.os.Handler;
import android.provider.ContactsContract;
import android.renderscript.Sampler;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class FragmentTech extends Fragment {

    private static final int TOTAL_ITEM_EACH_LOAD = 10;
    private long currentPage = 0 ;
    private int initCheck;

    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;


    private View v;
    private RecyclerView mRecyclerView;
    private List<Item> myDataset;
    private Myadapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    public FragmentTech() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.tech_fragment,container,false);
        return v;
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){

        myDataset = new ArrayList<Item>();
        mRecyclerView = view.findViewById(R.id.tech_recyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            Log.w("test","end");
                            loading = false;
                            Log.v("...", "Last Item Wow !");
                            loadMoreData();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        initCheck = 1;
        currentPage = 0;

        loadData();
        // 파이어베이스는 기본적으로 오름차순으로 정렬 따라서 최신 데이터를 가져오기 위해 뒤에서 부터 n개를 가져온다.
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("news");

    }

    @Override
    public void onPause() {
        super.onPause();
        // 중복해서 이벤트 리스너를 설정하게 되면 한번의 이벤트에 여러 번의 리스너가 트리거 되기 때문에
        // 프레그먼트가 포커스를 잃었을 때 마다 리스너를 제거한다. --> singleEventlisten로 바꿈
        //ref.removeEventListener(valueEventListener);
    }

    public void loadData() {
        Query query;
        // 초기에 프레그먼트가 resume 되었을 때 실행되는 query 정의(최근 데이터를 기준으로 정의된 개수만큼 가져온다)
        if (initCheck == 1) {
            query = ref.orderByChild("pubdate_ms").limitToLast(TOTAL_ITEM_EACH_LOAD);
        }
        // 스크롤이 맨 밑에 도달하였을 때 데이터를 이어서 가져오게 된다. inclusive한 범위로 데이터를 가져와서, currentpage - 1을 해주었다.
        else {
            Log.w("test","initcheck variable changed ");
            query = ref.orderByChild("pubdate_ms").limitToLast(TOTAL_ITEM_EACH_LOAD).endAt(currentPage - 1);
        }
       query.addListenerForSingleValueEvent(new ValueEventListener() {
            // 데이터베이스에 변경이 생겼을 때, 혹은 초기에 한번 불린다
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Item item;
                Stack stackItem = new Stack();

                for (DataSnapshot article : dataSnapshot.getChildren()) {
                    item = article.getValue(Item.class);

                    if (item.getThumbnail().equals("None")) {
                        item.setViewType(2);
                    } else {
                        item.setViewType(1);
                    }

                    stackItem.push(item);
                    //myDataset.add(item);
                }

                // Query가 마지막을 기준으로 특정 개수만큼 데이터를 가져올 때 오름차순으로 가져와서 stack을 이용해서 내림차순으로 데이터를 넣을 수 있도록 설정
                while(!stackItem.empty()){
                    Item itemInStack = (Item) stackItem.peek();
                    currentPage = itemInStack.getPubdate_ms();
                    Log.w("test",Long.toString(currentPage));
                    myDataset.add(itemInStack);
                    stackItem.pop();
                }

                if (initCheck == 1) {

                    mAdapter = new Myadapter(myDataset, new Myadapter.OnRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClick(int position, View view,String url) {

                            Intent intent = new Intent(getActivity(),Webviewactivity.class);
                            intent.putExtra("newsUrl",url);
                            startActivity(intent);
                        }
                    });
                    //Async하게 데이터를 가져오기 때문에 callback 안에서 어탭터, 리사이클러뷰를 설정해준다.
                    mRecyclerView.setAdapter(mAdapter);
                }

                else {

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });

                }

                initCheck = 0;
                loading = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("test","loadArticle : onCancelled",databaseError.toException());
            }
        });
    }

    private void loadMoreData(){
        loadData();
    }
}
