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
    private String[] menuItemLabels;
    private Integer[] menuItemIcons = {
    		R.drawable.ic_menu_allfriends,
    		R.drawable.ic_menu_today,
    		R.drawable.ic_menu_preferences,
    		R.drawable.ic_menu_close_clear_cancel,
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

        menuItemLabels = new String[] {
                getString(R.string.title_activity_find_patient),
                getString(R.string.title_activity_find_visit),
                getString(R.string.title_activity_settings),
                getString(R.string.title_activity_exit),
        };
        MainMenuAdapter mainMenuAdapter = new MainMenuAdapter(this, R.layout.main_menu_item , menuItemLabels);
        
        menu = (GridView) findViewById(R.id.main_menu);
        menu.setOnItemClickListener(this);
        menu.setAdapter(mainMenuAdapter);
    }
    
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TextView textView = (TextView) view;
		String label = (String) textView.getText();
		
		Intent intent;
		
		if (menuItemLabels[0].equals(label)) {
			intent = new Intent(this, FindPatientActivity.class);
			startActivity(intent);
		} else if (menuItemLabels[1].equals(label)) {
            intent = new Intent(this, FindVisitActivity.class);
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

