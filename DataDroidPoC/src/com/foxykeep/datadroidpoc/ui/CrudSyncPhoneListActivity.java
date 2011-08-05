/*
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.foxykeep.datadroidpoc.R;
import com.foxykeep.datadroidpoc.config.DialogConfig;
import com.foxykeep.datadroidpoc.data.memprovider.MemoryProvider;
import com.foxykeep.datadroidpoc.data.model.Phone;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestManager;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestManager.OnRequestFinishedListener;
import com.foxykeep.datadroidpoc.data.service.PoCService;
import com.foxykeep.datadroidpoc.util.ArrayUtils;
import com.foxykeep.datadroidpoc.util.UserManager;

public class CrudSyncPhoneListActivity extends ListActivity implements OnRequestFinishedListener, OnItemClickListener {

    private static final String SAVED_STATE_REQUEST_ID = "savedStateRequestId";
    private static final String SAVED_STATE_REQUEST_TYPE = "savedStateRequestType";
    private static final String SAVED_STATE_ERROR_TITLE = "savedStateErrorTitle";
    private static final String SAVED_STATE_ERROR_MESSAGE = "savedStateErrorMessage";
    private static final String SAVED_STATE_POSITION_TO_DELETE = "savedStatePositionToDelete";
    private static final String SAVED_STATE_ARE_PHONES_LOADED = "savedStateIsResultLoaded";
    private static final String SAVED_STATE_PHONE_ARRAY_LIST = "savedStatePhoneArrayList";

    private static final int REQUEST_TYPE_LIST = 1;
    private static final int REQUEST_TYPE_DELETE_MONO = 2;
    private static final int REQUEST_TYPE_DELETE_ALL = 3;

    private static final int ACTIVITY_FOR_RESULT_ADD = 1;
    private static final int ACTIVITY_FOR_RESULT_EDIT = 2;
    private static final int ACTIVITY_FOR_RESULT_VIEW = 3;

    public static final String RESULT_EXTRA_ADDED_PHONE = "resultExtraAddedPhone";
    public static final String RESULT_EXTRA_EDITED_PHONE = "resultExtraEditedPhone";
    public static final String RESULT_EXTRA_DELETED_PHONE_ID = "resultExtraDeletedPhoneId";

    private TextView mTextViewEmpty;

    private PoCRequestManager mRequestManager;
    private int mRequestId = -1;
    private int mRequestType = -1;
    private String mUserId;

    private boolean mArePhonesLoaded = false;

    private MemoryProvider mMemoryProvider = MemoryProvider.getInstance();

    private LayoutInflater mInflater;

    private String mErrorDialogTitle;
    private String mErrorDialogMessage;
    private int mPositionToDelete;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.crud_phone_list);
        bindViews();

        if (savedInstanceState != null) {
            mRequestId = savedInstanceState.getInt(SAVED_STATE_REQUEST_ID, -1);
            mRequestType = savedInstanceState.getInt(SAVED_STATE_REQUEST_TYPE, -1);
            mErrorDialogTitle = savedInstanceState.getString(SAVED_STATE_ERROR_TITLE);
            mErrorDialogMessage = savedInstanceState.getString(SAVED_STATE_ERROR_MESSAGE);
            mPositionToDelete = savedInstanceState.getInt(SAVED_STATE_POSITION_TO_DELETE);
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
        mUserId = UserManager.getUserId(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestId != -1) {
            if (mRequestManager.isRequestInProgress(mRequestId)) {
                mRequestManager.addOnRequestFinishedListener(this);
                if (mRequestType == REQUEST_TYPE_LIST) {
                    setProgressBarIndeterminateVisibility(true);
                } else if (mRequestType == REQUEST_TYPE_DELETE_ALL || mRequestType == REQUEST_TYPE_DELETE_MONO) {
                    showDialog(DialogConfig.DIALOG_PROGRESS);
                }
            } else {
                if (mRequestType == REQUEST_TYPE_LIST) {
                    if (mMemoryProvider.syncPhoneList == null) {
                        showDialog(DialogConfig.DIALOG_CONNEXION_ERROR);
                    } else {
                        mRequestType = -1;

                        mArePhonesLoaded = true;

                        final ArrayList<Phone> syncPhoneList = mMemoryProvider.syncPhoneList;

                        if (syncPhoneList.size() == 0) {
                            mTextViewEmpty.setText(R.string.crud_phone_list_tv_empty_no_results);
                        }

                        final PhoneListAdapter adapter = (PhoneListAdapter) getListAdapter();
                        adapter.clear();
                        adapter.setNotifyOnChange(false);
                        for (Phone phone : syncPhoneList) {
                            adapter.add(phone);
                        }
                        adapter.notifyDataSetChanged();
                    }
                } else if (mRequestType == REQUEST_TYPE_DELETE_ALL || mRequestType == REQUEST_TYPE_DELETE_MONO) {
                    if (mMemoryProvider.syncPhoneDeleteData == null) {
                        showDialog(DialogConfig.DIALOG_CONNEXION_ERROR);
                    } else {
                        mRequestType = -1;

                        final long[] syncDeletedPhoneIdArray = mMemoryProvider.syncPhoneDeleteData;

                        final PhoneListAdapter adapter = (PhoneListAdapter) getListAdapter();
                        adapter.setNotifyOnChange(false);
                        for (int i = 0; i < adapter.getCount(); i++) {
                            final Phone phone = adapter.getItem(i);
                            if (ArrayUtils.inArray(syncDeletedPhoneIdArray, phone.serverId)) {
                                adapter.remove(phone);
                                i--;
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }

                mRequestId = -1;
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
        outState.putInt(SAVED_STATE_REQUEST_TYPE, mRequestType);
        outState.putString(SAVED_STATE_ERROR_TITLE, mErrorDialogTitle);
        outState.putString(SAVED_STATE_ERROR_MESSAGE, mErrorDialogMessage);
        outState.putInt(SAVED_STATE_POSITION_TO_DELETE, mPositionToDelete);
        outState.putBoolean(SAVED_STATE_ARE_PHONES_LOADED, mArePhonesLoaded);

        final ArrayList<Phone> phoneArrayList = new ArrayList<Phone>();
        final PhoneListAdapter adapter = (PhoneListAdapter) getListAdapter();

        final int adapterCount = adapter.getCount();
        for (int i = 0; i < adapterCount; i++) {
            phoneArrayList.add(adapter.getItem(i));
        }
        outState.putParcelableArrayList(SAVED_STATE_PHONE_ARRAY_LIST, phoneArrayList);
    }

    private void bindViews() {
        mTextViewEmpty = (TextView) findViewById(android.R.id.empty);

        setListAdapter(new PhoneListAdapter(this));

        final ListView listView = getListView();
        listView.setOnItemClickListener(this);
        registerForContextMenu(listView);
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
                        if (mRequestType == REQUEST_TYPE_LIST) {
                            callSyncPhoneListWS();
                        } else if (mRequestType == REQUEST_TYPE_DELETE_ALL) {
                            callSyncPhoneDeleteAllWS();
                        } else if (mRequestType == REQUEST_TYPE_DELETE_MONO) {
                            callSyncPhoneDeleteMonoWS();
                        }
                    }
                });
                b.setTitle(R.string.dialog_error_connexion_error_title);
                b.setMessage(R.string.dialog_error_connexion_error_message);
                return b.create();
            case DialogConfig.DIALOG_PROGRESS:
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setTitle(R.string.progress_dialog_title);
                dialog.setMessage(getString(R.string.progress_dialog_message));
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                return dialog;
            case DialogConfig.DIALOG_DELETE_ALL_CONFIRM:
                b = new Builder(this);
                b.setIcon(android.R.drawable.ic_dialog_alert);
                b.setTitle(R.string.crud_phone_list_dialog_delete_all_confirm_title);
                b.setMessage(R.string.crud_phone_list_dialog_delete_all_confirm_message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        callSyncPhoneDeleteAllWS();
                    }
                });
                b.setNegativeButton(android.R.string.cancel, null);
                b.setCancelable(true);
                return b.create();
            case DialogConfig.DIALOG_DELETE_CONFIRM:
                Phone phone = ((PhoneListAdapter) getListAdapter()).getItem(mPositionToDelete);

                b = new Builder(this);
                b.setIcon(android.R.drawable.ic_dialog_alert);
                b.setTitle(R.string.crud_phone_list_dialog_delete_confirm_title);
                b.setMessage(getString(R.string.crud_phone_list_dialog_delete_confirm_message, phone.name));
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        callSyncPhoneDeleteMonoWS();
                    }
                });
                b.setNegativeButton(android.R.string.cancel, null);
                b.setCancelable(true);
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
            case DialogConfig.DIALOG_DELETE_CONFIRM:
                ((AlertDialog) dialog).setMessage(getString(R.string.crud_phone_list_dialog_delete_confirm_message,
                        ((PhoneListAdapter) getListAdapter()).getItem(mPositionToDelete).name));
                break;
            default:
                super.onPrepareDialog(id, dialog);
                break;
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case ACTIVITY_FOR_RESULT_VIEW: {
                if (resultCode == RESULT_OK) {
                    final long deletedPhoneId = data.getLongExtra(RESULT_EXTRA_DELETED_PHONE_ID, -1);
                    final Phone editedPhone = data.getParcelableExtra(RESULT_EXTRA_EDITED_PHONE);

                    if (deletedPhoneId != -1) {
                        final PhoneListAdapter adapter = (PhoneListAdapter) getListAdapter();
                        adapter.setNotifyOnChange(false);
                        for (int i = 0; i < adapter.getCount(); i++) {
                            final Phone phone = adapter.getItem(i);
                            if (phone.serverId == deletedPhoneId) {
                                adapter.remove(phone);
                                break;
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else if (editedPhone != null) {
                        final PhoneListAdapter adapter = (PhoneListAdapter) getListAdapter();
                        final int adapterCount = adapter.getCount();
                        adapter.setNotifyOnChange(false);
                        for (int i = 0; i < adapterCount; i++) {
                            final Phone phone = adapter.getItem(i);
                            if (phone.serverId == editedPhone.serverId) {
                                phone.serverId = editedPhone.serverId;
                                phone.name = editedPhone.name;
                                phone.manufacturer = editedPhone.manufacturer;
                                phone.androidVersion = editedPhone.androidVersion;
                                phone.screenSize = editedPhone.screenSize;
                                phone.price = editedPhone.price;
                                break;
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
                break;
            }
            case ACTIVITY_FOR_RESULT_ADD: {
                if (resultCode == RESULT_OK) {
                    final Phone addedPhone = data.getParcelableExtra(RESULT_EXTRA_ADDED_PHONE);

                    final PhoneListAdapter adapter = (PhoneListAdapter) getListAdapter();
                    adapter.setNotifyOnChange(false);
                    adapter.add(addedPhone);
                    adapter.notifyDataSetChanged();
                }
                break;
            }
            case ACTIVITY_FOR_RESULT_EDIT: {
                if (resultCode == RESULT_OK) {
                    final Phone editedPhone = data.getParcelableExtra(RESULT_EXTRA_EDITED_PHONE);

                    final PhoneListAdapter adapter = (PhoneListAdapter) getListAdapter();
                    final int adapterCount = adapter.getCount();
                    adapter.setNotifyOnChange(false);
                    for (int i = 0; i < adapterCount; i++) {
                        final Phone phone = adapter.getItem(i);
                        if (phone.serverId == editedPhone.serverId) {
                            phone.serverId = editedPhone.serverId;
                            phone.name = editedPhone.name;
                            phone.manufacturer = editedPhone.manufacturer;
                            phone.androidVersion = editedPhone.androidVersion;
                            phone.screenSize = editedPhone.screenSize;
                            phone.price = editedPhone.price;
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                break;
            }
            default:
                super.onActivityResult(requestCode, resultCode, data);
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
                startActivityForResult(new Intent(this, CrudSyncPhoneAddEditActivity.class), ACTIVITY_FOR_RESULT_ADD);
                return true;
            case R.id.menu_delete_all:
                showDialog(DialogConfig.DIALOG_DELETE_ALL_CONFIRM);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void callSyncPhoneListWS() {
        setProgressBarIndeterminateVisibility(true);
        mRequestManager.addOnRequestFinishedListener(this);
        mRequestType = REQUEST_TYPE_LIST;
        mRequestId = mRequestManager.getSyncPhoneList(mUserId);
    }

    private void callSyncPhoneDeleteMonoWS() {
        mRequestType = REQUEST_TYPE_DELETE_MONO;
        callSyncPhoneDeleteWS(String.valueOf(((PhoneListAdapter) getListAdapter()).getItem(mPositionToDelete).serverId));
    }

    private void callSyncPhoneDeleteAllWS() {
        final StringBuilder sb = new StringBuilder();
        final PhoneListAdapter adapter = (PhoneListAdapter) getListAdapter();
        final int adapterCount = adapter.getCount();
        for (int i = 0; i < adapterCount; i++) {
            sb.append(adapter.getItem(i).serverId);
            if (i != adapterCount - 1) {
                sb.append(",");
            }
        }
        mRequestType = REQUEST_TYPE_DELETE_ALL;
        callSyncPhoneDeleteWS(sb.toString());
    }

    private void callSyncPhoneDeleteWS(final String phoneIdList) {
        showDialog(DialogConfig.DIALOG_PROGRESS);
        mRequestManager.addOnRequestFinishedListener(this);
        mRequestId = mRequestManager.deleteSyncPhones(mUserId, phoneIdList);
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        if (parent == getListView()) {
            Intent intent = new Intent(this, CrudSyncPhoneViewActivity.class);
            intent.putExtra(CrudSyncPhoneViewActivity.INTENT_EXTRA_PHONE,
                    ((PhoneListAdapter) getListAdapter()).getItem(position));
            startActivityForResult(intent, ACTIVITY_FOR_RESULT_VIEW);
        }
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.crud_phone_list_context, menu);

        menu.setHeaderTitle(((PhoneListAdapter) getListAdapter()).getItem(((AdapterContextMenuInfo) menuInfo).position).name);
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        final int itemId = item.getItemId();

        final int position = ((AdapterContextMenuInfo) item.getMenuInfo()).position;

        switch (itemId) {
            case R.id.menu_edit:
                Phone phone = ((PhoneListAdapter) getListAdapter()).getItem(position);
                Intent intent = new Intent(this, CrudSyncPhoneAddEditActivity.class);
                intent.putExtra(CrudSyncPhoneAddEditActivity.INTENT_EXTRA_PHONE, phone);
                startActivityForResult(intent, ACTIVITY_FOR_RESULT_EDIT);
                return true;
            case R.id.menu_delete:
                mPositionToDelete = position;
                showDialog(DialogConfig.DIALOG_DELETE_CONFIRM);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onRequestFinished(final int requestId, final int resultCode, final Bundle payload) {
        if (requestId == mRequestId) {
            if (mRequestType == REQUEST_TYPE_LIST) {
                setProgressBarIndeterminateVisibility(false);
            } else if (mRequestType == REQUEST_TYPE_DELETE_ALL || mRequestType == REQUEST_TYPE_DELETE_MONO) {
                dismissDialog(DialogConfig.DIALOG_PROGRESS);
            }
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
                if (mRequestType == REQUEST_TYPE_LIST) {
                    mArePhonesLoaded = true;

                    final ArrayList<Phone> syncPhoneList = payload
                            .getParcelableArrayList(PoCRequestManager.RECEIVER_EXTRA_PHONE_LIST);

                    if (syncPhoneList.size() == 0) {
                        mTextViewEmpty.setText(R.string.crud_phone_list_tv_empty_no_results);
                    }

                    final PhoneListAdapter adapter = (PhoneListAdapter) getListAdapter();
                    adapter.clear();
                    adapter.setNotifyOnChange(false);
                    for (Phone phone : syncPhoneList) {
                        adapter.add(phone);
                    }
                    adapter.notifyDataSetChanged();
                } else if (mRequestType == REQUEST_TYPE_DELETE_ALL || mRequestType == REQUEST_TYPE_DELETE_MONO) {
                    final long[] syncDeletedPhoneIdArray = payload
                            .getLongArray(PoCRequestManager.RECEIVER_EXTRA_PHONE_DELETE_DATA);

                    final PhoneListAdapter adapter = (PhoneListAdapter) getListAdapter();
                    adapter.setNotifyOnChange(false);
                    for (int i = 0; i < adapter.getCount(); i++) {
                        final Phone phone = adapter.getItem(i);
                        if (ArrayUtils.inArray(syncDeletedPhoneIdArray, phone.serverId)) {
                            adapter.remove(phone);
                            i--;
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                mRequestType = -1;
            }
        }
    }

    class ViewHolder {
        private TextView mTextViewName;
        private TextView mTextViewManufacturer;

        public ViewHolder(final View view) {
            mTextViewName = (TextView) view.findViewById(R.id.tv_name);
            mTextViewManufacturer = (TextView) view.findViewById(R.id.tv_manufacturer);
        }

        public void populateView(final Phone phone) {
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
            return convertView;
        }
    }
}
