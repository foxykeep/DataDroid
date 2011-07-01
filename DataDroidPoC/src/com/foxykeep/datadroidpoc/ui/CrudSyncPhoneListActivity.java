/*
 * 2011 Foxykeep (http://www.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.datadroidpoc.ui;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.foxykeep.datadroidpoc.R;
import com.foxykeep.datadroidpoc.config.DialogConfig;
import com.foxykeep.datadroidpoc.data.memprovider.MemoryProvider;
import com.foxykeep.datadroidpoc.data.model.Phone;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestManager;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestManager.OnRequestFinishedListener;
import com.foxykeep.datadroidpoc.data.service.PoCService;
import com.foxykeep.datadroidpoc.util.UserManager;

public class CrudSyncPhoneListActivity extends ListActivity implements OnRequestFinishedListener {

    private static final String SAVED_STATE_REQUEST_ID = "savedStateRequestId";
    private static final String SAVED_STATE_ERROR_TITLE = "savedStateErrorTitle";
    private static final String SAVED_STATE_ERROR_MESSAGE = "savedStateErrorMessage";
    private static final String SAVED_STATE_ARE_PHONES_LOADED = "savedStateIsResultLoaded";
    private static final String SAVED_STATE_PHONE_ARRAY_LIST = "savedStatePhoneArrayList";

    private TextView mTextViewEmpty;

    private PoCRequestManager mRequestManager;
    private int mRequestId = -1;

    private boolean mArePhonesLoaded = false;

    private MemoryProvider mMemoryProvider = MemoryProvider.getInstance();

    private LayoutInflater mInflater;

    private String mErrorDialogTitle;
    private String mErrorDialogMessage;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.crud_phone_list);
        bindViews();

        if (savedInstanceState != null) {
            mRequestId = savedInstanceState.getInt(SAVED_STATE_REQUEST_ID, -1);
            mErrorDialogTitle = savedInstanceState.getString(SAVED_STATE_ERROR_TITLE);
            mErrorDialogMessage = savedInstanceState.getString(SAVED_STATE_ERROR_MESSAGE);
            mArePhonesLoaded = savedInstanceState.getBoolean(SAVED_STATE_ARE_PHONES_LOADED, false);

            final ArrayList<Phone> phoneArrayList = savedInstanceState
                    .getParcelableArrayList(SAVED_STATE_PHONE_ARRAY_LIST);
            if (phoneArrayList != null && phoneArrayList.size() > 0) {
                final PhoneListAdapter adapter = (PhoneListAdapter) getListAdapter();
                adapter.setNotifyOnChange(false);
                for (Phone phone : phoneArrayList) {
                    adapter.add(phone);
                }
                adapter.notifyDataSetChanged();
            }
        }

        mRequestManager = PoCRequestManager.from(this);
        mInflater = getLayoutInflater();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestId != -1) {
            if (mRequestManager.isRequestInProgress(mRequestId)) {
                mRequestManager.addOnRequestFinishedListener(this);
                setProgressBarIndeterminateVisibility(true);
            } else {
                mRequestId = -1;

                if (mMemoryProvider.syncPhoneList == null) {
                    showDialog(DialogConfig.DIALOG_CONNEXION_ERROR);
                } else {
                    mArePhonesLoaded = true;

                    final ArrayList<Phone> syncPhoneList = mMemoryProvider.syncPhoneList;

                    if (syncPhoneList.size() == 0) {
                        mTextViewEmpty.setText(R.string.crud_phone_list_tv_empty_no_results);
                        return;
                    }

                    final PhoneListAdapter adapter = (PhoneListAdapter) getListAdapter();
                    adapter.setNotifyOnChange(false);
                    for (Phone phone : syncPhoneList) {
                        adapter.add(phone);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        } else if (!mArePhonesLoaded) {
            callSyncPhoneListWS();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mRequestId != -1) {
            mRequestManager.removeOnRequestFinishedListener(this);
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(SAVED_STATE_REQUEST_ID, mRequestId);
        outState.putString(SAVED_STATE_ERROR_TITLE, mErrorDialogTitle);
        outState.putString(SAVED_STATE_ERROR_MESSAGE, mErrorDialogMessage);
        outState.putBoolean(SAVED_STATE_ARE_PHONES_LOADED, mArePhonesLoaded);
    }

    private void bindViews() {
        mTextViewEmpty = (TextView) findViewById(android.R.id.empty);

        setListAdapter(new PhoneListAdapter(this));
    }

    @Override
    protected Dialog onCreateDialog(final int id) {
        Builder b;
        switch (id) {
            case DialogConfig.DIALOG_ERROR:
                b = new Builder(this);
                b.setTitle(mErrorDialogTitle);
                b.setMessage(mErrorDialogMessage);
                b.setCancelable(true);
                b.setNeutralButton(android.R.string.ok, null);
                return b.create();
            case DialogConfig.DIALOG_CONNEXION_ERROR:
                b = new Builder(this);
                b.setCancelable(true);
                b.setNeutralButton(getString(android.R.string.ok), null);
                b.setPositiveButton(getString(R.string.dialog_button_retry), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int which) {
                        callSyncPhoneListWS();
                    }
                });
                b.setTitle(R.string.dialog_error_connexion_error_title);
                b.setMessage(R.string.dialog_error_connexion_error_message);
                return b.create();
            case DialogConfig.DELETE_ALL_CONFIRM:
                b = new Builder(this);
                b.setIcon(android.R.drawable.ic_dialog_alert);
                b.setTitle(R.string.crud_phone_list_dialog_delete_all_confirm_title);
                b.setMessage(R.string.crud_phone_list_dialog_delete_all_confirm_message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        // TODO
                    }
                });
                b.setNegativeButton(android.R.string.cancel, null);
                return b.create();
            default:
                return super.onCreateDialog(id);
        }
    }

    @Override
    protected void onPrepareDialog(final int id, final Dialog dialog) {
        switch (id) {
            case DialogConfig.DIALOG_ERROR:
                dialog.setTitle(mErrorDialogTitle);
                ((AlertDialog) dialog).setMessage(mErrorDialogMessage);
                break;
            default:
                super.onPrepareDialog(id, dialog);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.crud_phone_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_add:
                // TODO
                // Intent intent = new Intent(this, Activity.class);
                // startActivity(intent);
                return true;
            case R.id.menu_delete_all:
                showDialog(DialogConfig.DELETE_ALL_CONFIRM);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void callSyncPhoneListWS() {
        setProgressBarIndeterminateVisibility(true);
        mRequestManager.addOnRequestFinishedListener(this);
        mRequestId = mRequestManager.getSyncPhoneList(UserManager.getUserId(this));
    }

    @Override
    public void onRequestFinished(final int requestId, final int resultCode, final Bundle payload) {
        if (requestId == mRequestId) {
            setProgressBarIndeterminateVisibility(false);
            mRequestId = -1;
            mRequestManager.removeOnRequestFinishedListener(this);
            if (resultCode == PoCService.ERROR_CODE) {
                if (payload != null) {
                    final int errorType = payload.getInt(PoCRequestManager.RECEIVER_EXTRA_ERROR_TYPE, -1);
                    if (errorType == PoCRequestManager.RECEIVER_EXTRA_VALUE_ERROR_TYPE_DATA) {
                        mErrorDialogTitle = getString(R.string.dialog_error_data_error_title);
                        mErrorDialogMessage = getString(R.string.dialog_error_data_error_message);
                        showDialog(DialogConfig.DIALOG_ERROR);
                    } else {
                        showDialog(DialogConfig.DIALOG_CONNEXION_ERROR);
                    }
                } else {
                    showDialog(DialogConfig.DIALOG_CONNEXION_ERROR);
                }
            } else {
                mArePhonesLoaded = true;

                final ArrayList<Phone> syncPhoneList = payload
                        .getParcelableArrayList(PoCRequestManager.RECEIVER_EXTRA_SYNC_PHONE_LIST);

                final PhoneListAdapter adapter = (PhoneListAdapter) getListAdapter();
                adapter.setNotifyOnChange(false);
                for (Phone phone : syncPhoneList) {
                    adapter.add(phone);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    class ViewHolder {
        private Phone mPhone;
        private TextView mTextViewName;
        private TextView mTextViewManufacturer;

        public ViewHolder(final View view) {
            mTextViewName = (TextView) view.findViewById(R.id.tv_name);
            mTextViewManufacturer = (TextView) view.findViewById(R.id.tv_manufacturer);
        }

        public void populateView(final Phone phone) {
            mPhone = phone;
            mTextViewName.setText(phone.name);
            mTextViewManufacturer.setText(phone.manufacturer);
        }
    }

    class PhoneListAdapter extends ArrayAdapter<Phone> {

        public PhoneListAdapter(final Context context) {
            super(context, -1);
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.crud_phone_list_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.populateView(getItem(position));
            return super.getView(position, convertView, parent);
        }
    }
}
