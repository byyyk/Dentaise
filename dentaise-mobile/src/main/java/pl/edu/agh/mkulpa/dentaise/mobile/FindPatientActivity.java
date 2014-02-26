package pl.edu.agh.mkulpa.dentaise.mobile;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
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
        /*final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnSearchClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String query = searchView.getQuery().toString();//TODO
			}
		});*/
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
    
    public void onSearchButtonClicked(View view) {
    	EditText filterText = (EditText) findViewById(R.id.searched_patient_editbox);
    	filterText.getText().toString();
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
