package pl.edu.agh.mkulpa.dentaise.mobile;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FindPatientActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_patient);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        ListView patientList = (ListView) findViewById(R.id.patients_list);
        ListAdapter patientListAdapter = new PatientListAdapter();
        patientList.setAdapter(patientListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_find_patient, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class PatientListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 32;
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
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View view = inflater.inflate(R.layout.patient_list_item, parent, false);
			TextView textView = (TextView) view.findViewById(R.id.patient_list_item);
			textView.setText("Dupa Blada, 88091405937");
			return textView;
		}
    	
    }
    
}