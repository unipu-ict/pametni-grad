package hr.unipu.inf.ma.pametnigrad.activities;

import android.content.ContentValues;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import java.util.ArrayList;
import java.util.List;

import hr.unipu.inf.ma.pametnigrad.database.FeedReaderDbHelper;
import hr.unipu.inf.ma.pametnigrad.drawer.CustomAdapter;
import hr.unipu.inf.ma.pametnigrad.R;
import hr.unipu.inf.ma.pametnigrad.drawer.RowItem;
import hr.unipu.inf.ma.pametnigrad.fragments.DashboardFragment;
import hr.unipu.inf.ma.pametnigrad.fragments.EditFragment;
import hr.unipu.inf.ma.pametnigrad.fragments.LoginFragment;
import hr.unipu.inf.ma.pametnigrad.fragments.SolvedFragment;
import hr.unipu.inf.ma.pametnigrad.fragments.ViewFragment;
import xdroid.core.Global;


public class MainActivity extends AppCompatActivity {

    String[] menuTitles;
    TypedArray menuIcons;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private DrawerLayout mDrawerlayout;
    private ListView mdrawerList;
    private ActionBarDrawerToggle mDrawerToogle;

    private List<RowItem> rowItems;
    private CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Fragment fragment = null;
        Class fragmentClass = null;
        fragmentClass = DashboardFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.mainContainer, fragment).commit();

        final FloatingActionButton fab= (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditFragment editFragment = new EditFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.mainContainer, editFragment).commit();
            }
        });

        mTitle = mDrawerTitle = getTitle();

        menuTitles = getResources().getStringArray(R.array.menu_main);
        menuIcons = getResources().obtainTypedArray(R.array.icons);

        mDrawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mdrawerList = (ListView) findViewById(R.id.slide_menu);

        rowItems = new ArrayList<RowItem>();

        for(int i = 0; i< menuTitles.length; i++){
            RowItem items = new RowItem(menuTitles[i], menuIcons.getResourceId(
                    i, -1));
            rowItems.add(items);
        }

        adapter = new CustomAdapter((getApplicationContext()), rowItems);

        mdrawerList.setAdapter(adapter);
        mdrawerList.setOnItemClickListener(new SlideitemListener());

        mDrawerToogle = new ActionBarDrawerToggle(this, mDrawerlayout,
                null, R.string.sliding_menu, R.string.sliding_menu){
            public void onDrawerClosed(View view){
                getSupportActionBar().setTitle(mTitle);

                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView){
                getSupportActionBar().setTitle(mDrawerTitle);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
               // super.onDrawerSlide(drawerView, slideOffset);

                fab.setAlpha(1-slideOffset);
            }
        };
        //mDrawerlayout.setDrawerListener(mDrawerToogle);
        mDrawerlayout.addDrawerListener(mDrawerToogle);

        if(savedInstanceState == null){
            updateDisplay(0);
        }
    }

    class SlideitemListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            updateDisplay(position);
            findViewById(R.id.floatingActionButton).setVisibility(View.VISIBLE);
        }
    }

    private void updateDisplay(int position){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = new DashboardFragment();
        switch (position){
            case 0:
                fragment = new DashboardFragment();
                break;
            case 1:
                fragment = new ViewFragment();
                break;
            case 2:
                fragment = new SolvedFragment();
                break;
            /*case 3:
                fragment = new EditFragment();
                break;*/
            case 3:
                fragment = new LoginFragment();
                break;
            default:
                break;
        }



       if(fragment != null){

           transaction.replace(R.id.mainContainer, fragment).commit();

           setTitle(menuTitles[position]);
           mDrawerlayout.closeDrawer(mdrawerList);

       }else{
           Log.e("MainActivity", "Error in creating fragment");
       }

    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
       if (mDrawerToogle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

        //return super.onOptionsItemSelected(item);
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerlayout.isDrawerOpen(mdrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToogle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToogle.onConfigurationChanged(newConfig);
    }

    public static void setSolved(Integer id, boolean isSolved){
        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(Global.getContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Label", !isSolved);

        Integer counter = db.update("Post", values, "ID=" + id, null);

        Log.d("baza", "setSolved: " + counter.toString());
        Log.d("baza", "setSolved id: " + id.toString());

        mDbHelper.close();
    }
}
