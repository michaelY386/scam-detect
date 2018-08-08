package edu.cmu.eps.scams;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TwoLineListItem;
import android.view.LayoutInflater;


import java.util.List;

import edu.cmu.eps.scams.logic.ApplicationLogicFactory;
import edu.cmu.eps.scams.logic.ApplicationLogicResult;
import edu.cmu.eps.scams.logic.ApplicationLogicTask;
import edu.cmu.eps.scams.logic.IApplicationLogicCommand;
import edu.cmu.eps.scams.logic.model.Association;
import edu.cmu.eps.scams.logic.model.History;
import edu.cmu.eps.scams.logic.IApplicationLogic;
import edu.cmu.eps.scams.notifications.NotificationFacade;
import edu.cmu.eps.scams.utilities.TimestampUtility;


public class HistoryActivity extends AppCompatActivity {

    private static final String TAG = "HistoryActivity";
    private IApplicationLogic logic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ListView listView = (ListView) findViewById(R.id.history_list_view);
        this.logic = ApplicationLogicFactory.build(this);
        ApplicationLogicTask task = new ApplicationLogicTask(
                this.logic,
                progress -> {
                },
                result -> {
                    List<History> items = result.getHistories();
                    Log.d(TAG, String.format("Retrieved list of %d total histories", items.size()));
                    MyArrayAdapter adapter = new MyArrayAdapter(
                            this,
                            android.R.layout.simple_list_item_1,
                            items,
                            this.logic);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            History item = (History) parent.getItemAtPosition(position);
                            adapter.remove(adapter.getItem(position));
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
        );
        // runs the task's code on a background thread
        task.execute((IApplicationLogicCommand) logic -> new ApplicationLogicResult(logic.getHistory()));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    private class MyArrayAdapter extends ArrayAdapter<History> {

        private final IApplicationLogic logic;
        private List<History> objects;
        private Context context;

        public MyArrayAdapter(Context context, int textViewResourceId, List<History> objects, IApplicationLogic logic) {
            super(context, textViewResourceId, objects);
            this.objects = objects;
            this.context = context;
            this.logic = logic;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.friend_row_layout, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.friend_name);
            textView.setText(
                    String.format("%s @ %s",
                            objects.get(position).getDescription(),
                            TimestampUtility.format(objects.get(position).getTimeOfCall())));
            return rowView;
        }

        @Override
        public void remove(@Nullable History object) {
            super.remove(object);
            ApplicationLogicTask task = new ApplicationLogicTask(
                    this.logic,
                    progress -> {
                    },
                    result -> {
                        List<History> items = result.getHistories();
                        Log.d(TAG, String.format("Retrieved list of %d total histories", items.size()));
                        this.objects = items;
                    }
            );
            // runs the task's code on a background thread
            task.execute((IApplicationLogicCommand) logic -> {
                logic.removeHistory(object);
                return new ApplicationLogicResult(logic.getHistory());
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
