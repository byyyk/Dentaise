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

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.edu.agh.mkulpa.dentaise.mobile.rest.AuthenticationFailedException;
import pl.edu.agh.mkulpa.dentaise.mobile.rest.Patient;
import pl.edu.agh.mkulpa.dentaise.mobile.rest.Repositories;
import pl.edu.agh.mkulpa.dentaise.mobile.rest.RestCallAsyncTask;
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
        new RestCallAsyncTask<List<Visit>>(FindVisitActivity.this) {
            @Override
            protected String onSuccessMessage() {
                return null;
            }
            @Override
            protected List<Visit> makeRestCall() throws IOException, JSONException, AuthenticationFailedException {
                return Repositories.visit.listVisits();
            }
            @Override
            protected void handleResult(List<Visit> result) {
                visitListAdapter.updateData(result);
            }
        }.execute();
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
            Patient patient = visit.getPatient();
            String patientFullName = patient == null ? "" : ", " + patient.getForename() + " " + patient.getSurname();
            textView.setText(visit.getDate().toString() + patientFullName);
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
