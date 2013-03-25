/**
 *
 */
package com.foxykeep.datadroidpoc.ui;

import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroidpoc.R;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestManager;
import com.foxykeep.datadroidpoc.dialogs.ErrorDialogFragment.ErrorDialogFragmentBuilder;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;

/**
 * @author Foxykeep
 *
 */
public abstract class DataDroidActivity extends FragmentActivity {

    private static final String SAVED_STATE_REQUEST_LIST = "savedStateRequestList";

    protected PoCRequestManager mRequestManager;
    protected ArrayList<Request> mRequestList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRequestManager = PoCRequestManager.from(this);

        if (savedInstanceState != null) {
            mRequestList = savedInstanceState.getParcelableArrayList(SAVED_STATE_REQUEST_LIST);
        } else {
            mRequestList = new ArrayList<Request>();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(SAVED_STATE_REQUEST_LIST, mRequestList);
    }

    protected void showBadDataErrorDialog() {
        new ErrorDialogFragmentBuilder(this).setTitle(R.string.dialog_error_data_error_title)
                .setMessage(R.string.dialog_error_data_error_message).show();
    }
}
