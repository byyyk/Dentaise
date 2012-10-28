package pl.edu.agh.mkulpa.dentaise.mobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnItemClickListener {

    private static String TAG = "MainActivity";

    private GridView menu;
    
    private static final String NEW_PATIENT_MENU_ITEM_LABEL = "Dodaj pacjenta";
    private static final String FIND_PATIENT_MENU_ITEM_LABEL = "Znajdź pacjenta";
	private static final String SETTINGS_MENU_ITEM_LABEL = "Ustawienia";
	private static final String EXIT_MENU_ITEM_LABEL = "Wyjdź";
    
	//TODO: move to nested class
    private String[] menuItemLabels = {
    		NEW_PATIENT_MENU_ITEM_LABEL,
    		FIND_PATIENT_MENU_ITEM_LABEL,
    		SETTINGS_MENU_ITEM_LABEL,
    		EXIT_MENU_ITEM_LABEL,
    };
    
    private Integer[] menuItemIcons = {
    		android.R.drawable.ic_menu_add,
    		android.R.drawable.ic_menu_search,
    		android.R.drawable.ic_menu_manage,
    		android.R.drawable.ic_menu_close_clear_cancel,
    };
    
    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after 
     * previously being shut down then this Bundle contains the data it most 
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        
        MainMenuAdapter mainMenuAdapter = new MainMenuAdapter(this, R.layout.main_menu_item , menuItemLabels);
        
        menu = (GridView) findViewById(R.id.main_menu);
        menu.setOnItemClickListener(this);
        menu.setAdapter(mainMenuAdapter);
    }
    
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TextView textView = (TextView) view;
		String label = (String) textView.getText();
		Log.d(TAG, "Pressed " + label);
		
		Intent intent;
		
		if (NEW_PATIENT_MENU_ITEM_LABEL.equals(label)) {
			intent = new Intent(this, AddPatientActivity.class);
			startActivity(intent);
		} else if (FIND_PATIENT_MENU_ITEM_LABEL.equals(label)) {
			intent = new Intent(this, FindPatientActivity.class);
			startActivity(intent);
		}
		
	}

    private class MainMenuAdapter extends ArrayAdapter<String> {

		public MainMenuAdapter(Context context, int textViewResourceId, String[] objects) {
			super(context, textViewResourceId, objects);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View view = inflater.inflate(R.layout.main_menu_item, parent, false);
			TextView textView = (TextView) view.findViewById(R.id.main_menu_item);
			textView.setText(menuItemLabels[position]);
			textView.setCompoundDrawablesWithIntrinsicBounds(0, menuItemIcons[position], 0, 0);
			return view;
		}
    	
    }
}

