package com.example.farejudge.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.farejudge.R;
import com.example.farejudge.models.DatabaseHelper;
import com.example.farejudge.models.Establishment;
import com.example.farejudge.utils.EstablishmentAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    LinearLayoutCompat lytParent;
    Toolbar toolbar;
    SwipeRefreshLayout refreshList;
    RecyclerView estList;
    FloatingActionButton fabAddEstablishment;

    DatabaseHelper databaseHelper;
    EstablishmentAdapter establishmentAdapter;
    List<Establishment> establishmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        setUpList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpList();
    }

    private void initViews() {
        lytParent = findViewById(R.id.lytParent);
        toolbar = findViewById(R.id.toolbar);
        refreshList = findViewById(R.id.refreshList);
        estList = findViewById(R.id.estList);
        fabAddEstablishment = findViewById(R.id.fabAddEstablishment);

        toolbar.inflateMenu(R.menu.menu_main);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) toolbar.getMenu().findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("Search");
        searchView.setSearchableInfo(searchManager != null ? searchManager.getSearchableInfo(getComponentName()) : null);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                establishmentAdapter.getFilter().filter(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                establishmentAdapter.getFilter().filter(newText);

                return true;
            }
        });

        refreshList.setColorSchemeResources(R.color.colorPrimary);
        refreshList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setUpList();
            }
        });

        databaseHelper = new DatabaseHelper(MainActivity.this);
        establishmentList = new ArrayList<>();
        establishmentAdapter = new EstablishmentAdapter(MainActivity.this, establishmentList);
        estList.setAdapter(establishmentAdapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
        estList.setLayoutManager(mLayoutManager);
        estList.setItemAnimator(new DefaultItemAnimator());

        fabAddEstablishment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, com.example.farejudge.activities.NewEstActivity.class));
            }
        });
    }

    private void setUpList() {
        refreshList.setRefreshing(true);
        establishmentList.clear();
        establishmentList.addAll((Collection<? extends Establishment>) databaseHelper.getAllEstablishments());
        establishmentAdapter.notifyDataSetChanged();
        refreshList.setRefreshing(false);
    }
}
