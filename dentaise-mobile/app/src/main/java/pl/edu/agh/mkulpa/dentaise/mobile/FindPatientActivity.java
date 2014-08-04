package pl.edu.agh.mkulpa.dentaise.mobile;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.edu.agh.mkulpa.dentaise.mobile.rest.Patient;
import pl.edu.agh.mkulpa.dentaise.mobile.rest.RestClient;

public class FindPatientActivity extends Activity {
    private static final String TAG = FindPatientActivity.class.getName();
    public static final String EXTRA_PATIENT_ID = "patient_id";
    private PatientListAdapter patientListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_patient);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        patientListAdapter = new PatientListAdapter(new ArrayList<Patient>());

        final ListView patientList = (ListView) findViewById(R.id.patients_list);

        new AsyncTask<String, Void, List<Patient>>() {
            @Override
            protected List<Patient> doInBackground(String... urls) {
                try {
                    Log.i(TAG, "retrieving patient list");
                    return RestClient.listPatients(null);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Patient> patients) {
                Log.i(TAG, "updating patients");
                patientListAdapter.updateData(patients);
            }
        }.execute();
        patientList.setAdapter(patientListAdapter);
        patientList.setOnItemClickListener(patientListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_find_patient, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            final String query = intent.getStringExtra(SearchManager.QUERY);

            new AsyncTask<String, Void, List<Patient>>() {
                @Override
                protected List<Patient> doInBackground(String... urls) {
                    try {
                        return RestClient.listPatients(query);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(List<Patient> patients) {
                    patientListAdapter.updateData(patients);
                }
            }.execute();
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_add:
                Intent intent = new Intent(this, EditPatientActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class PatientListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
        private List<Patient> patients;

        public PatientListAdapter(List<Patient> patients) {
            this.patients = patients;
        }

        public void updateData(List<Patient> patients) {
            System.out.print("patients updated! " + patients.size());
            this.patients = patients;
            this.notifyDataSetChanged();
        }

		@Override
		public int getCount() {
			return patients.size();
		}

		@Override
		public Object getItem(int position) {
			return patients.get(position);
		}

		@Override
		public long getItemId(int position) {
			return patients.get(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
            Patient patient = patients.get(position);
			LayoutInflater inflater = getLayoutInflater();
			View view = inflater.inflate(R.layout.patient_list_item, parent, false);
			TextView textView = (TextView) view.findViewById(R.id.patient_list_item);
			textView.setText(patient.getForename() + " " + patient.getSurname() + ", " + patient.getPesel());
			return textView;
		}

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(FindPatientActivity.this, EditPatientActivity.class);
            intent.putExtra(FindPatientActivity.EXTRA_PATIENT_ID, id);
            startActivity(intent);
        }
    }
    
}
