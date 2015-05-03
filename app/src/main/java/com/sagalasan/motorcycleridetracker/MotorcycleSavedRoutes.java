package com.sagalasan.motorcycleridetracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.sax.StartElementListener;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.w3c.dom.ls.LSException;

import java.util.ArrayList;


public class MotorcycleSavedRoutes extends ActionBarActivity implements MotorcycleData
{
    private ListView lv;
    private ArrayAdapter<String> arrayAdapter;
    ArrayList<String> routeNames, routes;
    MyDBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motorcycle_saved_routes);

        lv = (ListView) findViewById(R.id.saved_routes);
        routes = new ArrayList<String>();
        routeNames = new ArrayList<String>();

        dbHandler = new MyDBHandler(this, null, null, 1);
        dbHandler.deleteMotorcycleRoute(name);

        popArrayList();

        arrayAdapter = new ArrayAdapter<String>(
                this,
                R.layout.list_item_custom_m,
                R.id.list_name,
                routeNames
        );
        lv.setAdapter(arrayAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                jump(position);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                AlertDialog diaBox = AskOption(position, id);
                diaBox.show();
                return true;
            }
        });
    }

    private void jump(int p)
    {
        Intent intent = new Intent(this, ShowSavedRoute.class);
        String a = routeNames.get(p);
        intent.putExtra(ROUTE_NAME, a);
        startActivity(intent);
    }

    private void popArrayList()
    {
        routes.clear();
        routes = dbHandler.returnRoutes();

        for(int i = 0; i < routes.size(); i++)
        {
            routeNames.add(routes.get(i).toString());
        }
    }

    private void deleteRoute(int position)
    {
        dbHandler.deleteMotorcycleRoute(routeNames.get(position));
        runOnUiThread(run);
    }

    Runnable run = new Runnable() {
        @Override
        public void run()
        {
            routeNames.clear();
            popArrayList();
            arrayAdapter.notifyDataSetChanged();
            lv.invalidateViews();
            lv.refreshDrawableState();
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_motorcycle_saved_routes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private AlertDialog AskOption(final int position, final long id)
    {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to Delete")
                .setIcon(R.drawable.abc_ic_voice_search_api_mtrl_alpha)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteRoute(position);
                        dialog.dismiss();
                    }

                })

                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;
    }
}
