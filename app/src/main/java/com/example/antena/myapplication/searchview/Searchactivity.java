package com.example.antena.myapplication.searchview;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.example.antena.myapplication.R;
import com.example.antena.myapplication.mainview.Filter;
import com.example.antena.myapplication.mainview.Item;
import com.example.antena.myapplication.mainview.Myadapter;
import com.example.antena.myapplication.webview.Webviewactivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Searchactivity extends AppCompatActivity {


    private Client client;
    private Index index;
    private Query algoliaQuery;
    private SearchResultsJsonParser searchResultsJsonParser;
    private List <Item> articlesList;
    private RecyclerView mRecyclerView;
    private Myadapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_searchactivity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        client =  new Client("0IGE08OLAI","667594917ed30d2c01795c2639299ff4");;
        index = client.getIndex("TechNews");
        algoliaQuery = new Query();
        algoliaQuery.setHitsPerPage(10);

        searchResultsJsonParser = new SearchResultsJsonParser();

        mRecyclerView = findViewById(R.id.searchActivityRecyclerview);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.searchicon,menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.expandActionView();
        SearchView searchView = (SearchView) searchItem.getActionView();

        // Assumes current activity is the searchable activity
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setIconifiedByDefault(false);

        searchView.setQueryHint("제목 검색");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.w("test","search button clicked");
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            String query = intent.getStringExtra(SearchManager.QUERY);
            search(query);
        }
    }

    private void search(String query) {

        algoliaQuery.setQuery(query);

        index.searchAsync(algoliaQuery, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject jsonObject, AlgoliaException e) {
                articlesList = searchResultsJsonParser.parseResults(jsonObject);
                //Log.w("test",articlesList.toString());

                mAdapter = new Myadapter(articlesList, new Myadapter.OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(int position, View view,String url) {

                        Intent intent = new Intent(Searchactivity.this,Webviewactivity.class);
                        intent.putExtra("newsUrl",url);
                        startActivity(intent);
                    }
                });

                //Async하게 데이터를 가져오기 때문에 callback 안에서 어탭터, 리사이클러뷰를 설정해준다.
                mRecyclerView.setAdapter(mAdapter);
            }

        });

    }

}
