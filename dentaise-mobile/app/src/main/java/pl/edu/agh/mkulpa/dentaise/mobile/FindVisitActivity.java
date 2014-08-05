package pl.edu.agh.mkulpa.dentaise.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.edu.agh.mkulpa.dentaise.mobile.rest.Visit;


public class FindVisitActivity extends Activity {

    private static final String TAG = FindVisitActivity.class.getSimpleName();
    private static String EXTRA_VISIT_ID = "visit_id";
    private PatientListAdapter visitListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_visit);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        visitListAdapter = new PatientListAdapter(new ArrayList<Visit>());
        final ListView visitsList = (ListView) findViewById(R.id.visits_list);
        updateVisitList();
        visitsList.setAdapter(visitListAdapter);
        visitsList.setOnItemClickListener(visitListAdapter);
    }

    private void updateVisitList() {
        List<Visit> mock = new ArrayList<Visit>();
        Visit visit = new Visit();
        visit.setDate(new Date());
        visit.setId(1);
        visit.setNotes("jakieś notatki...");
        mock.add(visit);
        mock.add(visit);
        mock.add(visit);
        mock.add(visit);
        mock.add(visit);
        visitListAdapter.updateData(mock);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.find_visit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class PatientListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
        private List<Visit> visits;

        public PatientListAdapter(List<Visit> visits) {
            this.visits = visits;
        }

        public void updateData(List<Visit> visits) {
            Log.i(TAG, "visits updated! " + visits.size());
            this.visits = visits;
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return visits.size();
        }

        @Override
        public Object getItem(int position) {
            return visits.get(position);
        }

        @Override
        public long getItemId(int position) {
            return visits.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Visit visit = visits.get(position);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.patient_list_item, parent, false);
            TextView textView = (TextView) view.findViewById(R.id.patient_list_item);
            textView.setText(visit.getDate().toString());
            return textView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(FindVisitActivity.this, EditVisitActivity.class);
            intent.putExtra(FindVisitActivity.EXTRA_VISIT_ID, id);
            startActivity(intent);
        }
    }

}
