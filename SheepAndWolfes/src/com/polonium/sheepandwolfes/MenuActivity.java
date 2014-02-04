package com.polonium.sheepandwolfes;

import java.util.ArrayList;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.SparseIntArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.polonium.sheepandwolfes.GameFragment.OnFragmentInteractionListener;

public class MenuActivity extends FragmentActivity implements OnFragmentInteractionListener {
    private Fragment currentFragment = null;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;

    private GameFragment fragment;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
        mDrawerLayout, /* DrawerLayout object */
        R.drawable.ic_navigation_drawer, /* nav drawer icon to replace 'Up' caret */
        R.string.app_name, /* "open drawer" description */
        R.string.app_name /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(R.string.app_name);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(R.string.app_name);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        fragment = GameFragment.newInstance();
        setFragment(fragment);

        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        DrawerAdapter adapter = new DrawerAdapter();
        mDrawerList.setAdapter(adapter);
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(adapter);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    private void setFragment(Fragment fragment) {
        setFragment(fragment, null);
    }

    private void setFragment(Fragment fragment, String name) {
        if (fragment != currentFragment) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (name != null) ft.addToBackStack(name);
            // TODO: add animations
            ft.replace(R.id.content_frame, fragment).commit();
            currentFragment = fragment;
        }
    }

    @Override
    public void openDrawer() {
        mDrawerLayout.openDrawer(mDrawerList);
    }

    @Override
    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mDrawerList);
    }
    
    class DrawerAdapter extends BaseAdapter implements OnItemClickListener{

        ArrayList<String> title = new ArrayList<String>();
        ArrayList<Integer> group = new ArrayList<Integer>();
        ArrayList<Boolean> isTitle = new ArrayList<Boolean>();
        ArrayList<Integer> level = new ArrayList<Integer>();
        SparseIntArray selected = new SparseIntArray();

        public DrawerAdapter() {
            addItem("Игрок за овечку", 0xffffbb33, 0, true);
            addItem("Человек", 0xffffbb33, 0, false);
            addItem("Компьютер минимакс 1", 0xffffbb33, 1, false);
            addItem("Компьютер минимакс 2", 0xffffbb33, 2, false);
            addItem("Компьютер минимакс 3", 0xffffbb33, 3, false);
            addItem("Компьютер альфа-бета 2", 0xffffbb33, 4, false);
            addItem("Компьютер альфа-бета 3", 0xffffbb33, 5, false);
            addItem("Компьютер альфа-бета 4", 0xffffbb33, 6, false);
            
            addItem("Игрок за волков", 0xff33b5e5, 0, true);
            addItem("Человек", 0xff33b5e5, 0, false);
            addItem("Компьютер минимакс 1", 0xff33b5e5, 1, false);
            addItem("Компьютер минимакс 2", 0xff33b5e5, 2, false);
            addItem("Компьютер минимакс 3", 0xff33b5e5, 3, false);
            addItem("Компьютер альфа-бета 2", 0xff33b5e5, 4, false);
            addItem("Компьютер альфа-бета 3", 0xff33b5e5, 5, false);
            addItem("Компьютер альфа-бета 4", 0xff33b5e5, 6, false);
            setSelected(1);
            setSelected(10);
        }

        public void setSelected(int position) {
            selected.put(group.get(position), position);
        }

        public void addItem(String title, int group, int level, boolean isTitle) {
            this.title.add(title);
            this.group.add(group);
            this.level.add(level);
            this.isTitle.add(isTitle);
        }

        @Override
        public int getCount() {
            return title.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            return isTitle.get(position) ? 0 : 1;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }
        @Override
        public boolean isEnabled(int position) {
            return !isTitle.get(position);
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                if (getItemViewType(position) == 0) {
                    convertView = getLayoutInflater().inflate(R.layout.item_title, null);
                }
                if (getItemViewType(position) == 1) {
                    convertView = getLayoutInflater().inflate(R.layout.item_item, null);
                }
            }
            TextView title = (TextView)convertView.findViewById(R.id.title);
            if (selected.get(group.get(position), -1) == position)title.setBackgroundColor(group.get(position));
            else title.setBackgroundColor(0x00000000);
            title.setText(this.title.get(position));
            return convertView;
        }

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
           setSelected(position);
           notifyDataSetChanged();
           fragment.changePlayers(level.get(selected.get(0xffffbb33)), level.get(selected.get(0xff33b5e5)));
           mDrawerLayout.closeDrawer(mDrawerList);
        }
    }
}
