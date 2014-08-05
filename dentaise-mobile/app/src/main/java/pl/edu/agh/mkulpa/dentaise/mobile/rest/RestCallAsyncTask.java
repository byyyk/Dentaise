package pl.edu.agh.mkulpa.dentaise.mobile.rest;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import pl.edu.agh.mkulpa.dentaise.mobile.R;

/**
 * Created by byyyk on 05.08.14.
 */
public abstract class RestCallAsyncTask<T> extends AsyncTask<Void, Void, T> {

    private static final String TAG = RestCallAsyncTask.class.getSimpleName();
    private boolean connectionFault;
    private boolean authenticationFault;
    private final Context context;

    public RestCallAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected T doInBackground(Void... params) {
        try {
            connectionFault = false;
            authenticationFault = false;
            return makeRestCall();
        } catch (IOException e) {
            connectionFault = true;
            Log.i(TAG, "Could not connect to server: " + e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON: " + e.getMessage());
        } catch (AuthenticationFailedException e) {
            Log.e(TAG, "Could not log in: " + e.getMessage());
            authenticationFault = true;
        }
        return null;
    }

    protected String onSuccessMessage() {
        return null;
    }

    protected abstract T makeRestCall() throws IOException, JSONException, AuthenticationFailedException;

    @Override
    protected void onPostExecute(T result) {
        if (result != null) {
            handleResult(result);
            toast(onSuccessMessage());
        } else if(connectionFault) {
            toast(getResourceString(R.string.toast_connection_fault));
        } else if (authenticationFault) {
            toast(getResourceString(R.string.toast_authentication_fault));
        }
    }

    private String getResourceString(int resId) {
        return context.getResources().getString(resId);
    }

    private void toast(String message) {
        if (message != null) {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    protected abstract void handleResult(T result);

}
