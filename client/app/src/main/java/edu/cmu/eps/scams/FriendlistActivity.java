package edu.cmu.eps.scams;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
import edu.cmu.eps.scams.logic.IApplicationLogic;


public class FriendlistActivity extends AppCompatActivity {

    private static final String TAG = "FriendlistActivity";
    private IApplicationLogic logic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);
        ListView listView = (ListView) findViewById(R.id.friend_list_view);
        this.logic = ApplicationLogicFactory.build(this);
        ApplicationLogicTask task = new ApplicationLogicTask(
                this.logic,
                progress -> {
                },
                result -> {
                    List<Association> associations = result.getAssociations();
                    Log.d(TAG, String.format("Retrieved list of %d total associations", associations.size()));
                    MyArrayAdapter adapter = new MyArrayAdapter(
                            this,
                            android.R.layout.simple_list_item_1,
                            associations,
                            this.logic);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Association item = (Association) parent.getItemAtPosition(position);
                            adapter.remove(adapter.getItem(position));
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
        );
       // runs the task's code on a background thread
        task.execute((IApplicationLogicCommand) logic -> new ApplicationLogicResult(logic.getAssociations()));
    }


    // Customize the list display
    private class MyArrayAdapter extends ArrayAdapter<Association> {

        private final IApplicationLogic logic;
        private List<Association> objects;
        private Context context;

        public MyArrayAdapter(Context context, int textViewResourceId, List<Association> objects, IApplicationLogic logic) {
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
            textView.setText(objects.get(position).getName());
            return rowView;
        }

        @Override
        public void remove(@Nullable Association object) {
            super.remove(object);
            ApplicationLogicTask task = new ApplicationLogicTask(
                    this.logic,
                    progress -> {
                    },
                    result -> {
                        List<Association> associations = result.getAssociations();
                        Log.d(TAG, String.format("Retrieved list of %d total associations", associations.size()));
                        this.objects = associations;
                    }
            );
            // runs the task's code on a background thread
            task.execute((IApplicationLogicCommand) logic -> {
                logic.removeAssociation(object);
                return new ApplicationLogicResult(logic.getAssociations());
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
        Log.d(TAG, "Item selected");
        return super.onOptionsItemSelected(item);
    }
}

