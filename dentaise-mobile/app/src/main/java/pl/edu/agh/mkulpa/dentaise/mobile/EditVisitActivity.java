package pl.edu.agh.mkulpa.dentaise.mobile;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import pl.edu.agh.mkulpa.dentaise.mobile.rest.Area;
import pl.edu.agh.mkulpa.dentaise.mobile.rest.AuthenticationFailedException;
import pl.edu.agh.mkulpa.dentaise.mobile.rest.Diagnosis;
import pl.edu.agh.mkulpa.dentaise.mobile.rest.Identifiable;
import pl.edu.agh.mkulpa.dentaise.mobile.rest.Repositories;
import pl.edu.agh.mkulpa.dentaise.mobile.rest.RestCallAsyncTask;
import pl.edu.agh.mkulpa.dentaise.mobile.rest.Treatment;
import pl.edu.agh.mkulpa.dentaise.mobile.rest.Visit;
import pl.edu.agh.mkulpa.dentaise.mobile.rest.VisitRepository;
import pl.edu.agh.mkulpa.dentaise.mobile.rest.Work;


public class EditVisitActivity extends Activity {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private static final String TAG = EditVisitActivity.class.getSimpleName();
    private Visit visit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_visit);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        final long visitId = getIntent().getExtras().getLong(FindVisitActivity.EXTRA_VISIT_ID);

        new RestCallAsyncTask<VisitData>(this) {

            @Override
            protected VisitData makeRestCall() throws IOException, JSONException, AuthenticationFailedException {
                VisitRepository vr = Repositories.visit;
                return new VisitData(vr.getVisit(visitId), vr.listAreas(), vr.listDiagnoses(), vr.listTreatments());
            }

            @Override
            protected void handleResult(VisitData result) {
                init(result);
            }
        }.execute();
    }

    private void init(final VisitData visitData) {
        visit = visitData.getVisit();
        EditText notes = (EditText) findViewById(R.id.visit_notes);
        notes.setText(visit.getNotes());
        EditText date = (EditText) findViewById(R.id.visit_date);
        date.setText(dateFormat.format(visit.getDate()));

        for (Work work : visit.getWorkList()) {
            addWorkLayout(visitData, work);
        }

        Button addWorkButton = (Button) findViewById(R.id.add_work_button);
        addWorkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWorkLayout(visitData, null);
            }
        });
    }


    public void addWorkLayout(VisitData visitData, Work work) {
        final LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final LinearLayout parent = (LinearLayout) findViewById(R.id.work_list);

        Button removeWorkButton = new Button(this);
        removeWorkButton.setText(R.string.remove_work_button);

        Spinner areaSpinner = new Spinner(this);
        final WorkTypeSpinnerAdapter<Area> areaAdapter = new WorkTypeSpinnerAdapter<Area>(visitData.getAreas());
        areaSpinner.setAdapter(areaAdapter);

        Spinner diagnosisSpinner = new Spinner(this);
        final WorkTypeSpinnerAdapter<Diagnosis> diagnosisAdapter = new WorkTypeSpinnerAdapter<Diagnosis>(visitData.getDiagnoses());
        diagnosisSpinner.setAdapter(diagnosisAdapter);


        Spinner treatmentSpinner = new Spinner(this);
        final WorkTypeSpinnerAdapter<Treatment> treatmentAdapter = new WorkTypeSpinnerAdapter<Treatment>(visitData.getTreatments());
        treatmentSpinner.setAdapter(treatmentAdapter);

        if (work != null) {
            areaSpinner.setSelection(areaAdapter.getPosition(work.getArea()));
            diagnosisSpinner.setSelection(diagnosisAdapter.getPosition(work.getDiagnosis()));
            treatmentSpinner.setSelection(treatmentAdapter.getPosition(work.getTreatment()));
        } else {
            work = new Work();
            visit.getWorkList().add(work);
        }


        final Work finalWork = work;
        removeWorkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.removeView(layout);
                visit.getWorkList().remove(finalWork);
            }
        });
        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Area area = areaAdapter.getItem(position);
                finalWork.setArea(area);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                finalWork.setArea(null);
            }
        });
        diagnosisSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Diagnosis diagnosis = diagnosisAdapter.getItem(position);
                finalWork.setDiagnosis(diagnosis);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                finalWork.setArea(null);
            }
        });
        treatmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Treatment treatment = treatmentAdapter.getItem(position);
                finalWork.setTreatment(treatment);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                finalWork.setArea(null);
            }
        });

        layout.addView(areaSpinner);
        layout.addView(diagnosisSpinner);
        layout.addView(treatmentSpinner);
        layout.addView(removeWorkButton);

        parent.addView(layout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_visit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.save_action:
                saveVisit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveVisit() {
        new RestCallAsyncTask<Void>(this) {
            @Override
            protected Void makeRestCall() throws IOException, JSONException, AuthenticationFailedException {
                Repositories.visit.saveVisit(visit);
                return null;
            }
            @Override
            protected String onSuccessMessage() {
                return getApplicationContext().getResources().getString(R.string.toast_saved);
            }
            @Override
            protected void handleResult(Void result) {

            }
        }.execute();
    }

    private class WorkTypeSpinnerAdapter<T extends Identifiable> extends ArrayAdapter<T> {

        private final List<T> items;

        public WorkTypeSpinnerAdapter(List<T> items) {
            super(EditVisitActivity.this, R.layout.work_item, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            T item = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.work_item, parent, false);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.work_item_text_view);
            textView.setText(item.toString());
            return convertView;
        }

        @Override
        public int getPosition(T checkedItem) {
            for (int i = 0; i < items.size(); i++) {
                T item = items.get(i);
                if (item.getId() == checkedItem.getId()) {
                    return i;
                }
            }
            return -1;
        }
    }

    private static class VisitData {
        private final Visit visit;

        private final List<Area> areas;
        private final List<Diagnosis> diagnoses;
        private final List<Treatment> treatments;

        private VisitData(Visit visit, List<Area> areas, List<Diagnosis> diagnoses, List<Treatment> treatments) {
            this.visit = visit;
            this.areas = areas;
            this.diagnoses = diagnoses;
            this.treatments = treatments;
        }

        public List<Area> getAreas() {
            return areas;
        }

        public List<Diagnosis> getDiagnoses() {
            return diagnoses;
        }

        public List<Treatment> getTreatments() {
            return treatments;
        }

        public Visit getVisit() {
            return visit;
        }
    }
}
