/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.ui.crud;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
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

import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager.RequestListener;
import com.foxykeep.datadroidpoc.R;
import com.foxykeep.datadroidpoc.config.DialogConfig;
import com.foxykeep.datadroidpoc.data.model.Phone;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestFactory;
import com.foxykeep.datadroidpoc.dialogs.ConnexionErrorDialogFragment;
import com.foxykeep.datadroidpoc.dialogs.ProgressDialogFragment;
import com.foxykeep.datadroidpoc.dialogs.ProgressDialogFragment.ProgressDialogFragmentBuilder;
import com.foxykeep.datadroidpoc.ui.DataDroidActivity;
import com.foxykeep.datadroidpoc.util.ArrayUtils;
import com.foxykeep.datadroidpoc.util.UserManager;

import java.util.ArrayList;

public final class CrudSyncPhoneListActivity extends DataDroidActivity implements RequestListener,
        OnItemClickListener {

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
    private ListView mListView;
    private PhoneListAdapter mListAdapter;

    private String mUserId;

    private boolean mArePhonesLoaded = false;

    private LayoutInflater mInflater;

    private int mPositionToDelete;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.crud_phone_list);
        bindViews();

        if (savedInstanceState != null) {
            mPositionToDelete = savedInstanceState.getInt(SAVED_STATE_POSITION_TO_DELETE);
            mArePhonesLoaded = savedInstanceState.getBoolean(SAVED_STATE_ARE_PHONES_LOADED, false);

            final ArrayList<Phone> phoneArrayList = savedInstanceState
                    .getParcelableArrayList(SAVED_STATE_PHONE_ARRAY_LIST);
            if (phoneArrayList != null && phoneArrayList.size() > 0) {
                mListAdapter.setNotifyOnChange(false);
                for (Phone phone : phoneArrayList) {
                    mListAdapter.add(phone);
                }
                mListAdapter.notifyDataSetChanged();
            }
        }

        mInflater = getLayoutInflater();
        mUserId = UserManager.getUserId(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (int i = 0, length = mRequestList.size(); i < length; i++) {
            Request request = mRequestList.get(i);
            int requestType = request.getRequestType();

            if (mRequestManager.isRequestInProgress(request)) {
                mRequestManager.addRequestListener(this, request);
                if (requestType == PoCRequestFactory.REQUEST_TYPE_CITY_LIST) {
                    setProgressBarIndeterminateVisibility(true);
                }
            } else {
                if (requestType != PoCRequestFactory.REQUEST_TYPE_CITY_LIST) {
                    ProgressDialogFragment.dismiss(this);
                }
                mRequestManager.callListenerWithCachedData(this, request);
            }
        }

        if (!mArePhonesLoaded) {
            callSyncPhoneListWS();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!mRequestList.isEmpty()) {
            mRequestManager.removeRequestListener(this);
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(SAVED_STATE_POSITION_TO_DELETE, mPositionToDelete);
        outState.putBoolean(SAVED_STATE_ARE_PHONES_LOADED, mArePhonesLoaded);

        final ArrayList<Phone> phoneArrayList = new ArrayList<Phone>();

        final int adapterCount = mListAdapter.getCount();
        for (int i = 0; i < adapterCount; i++) {
            phoneArrayList.add(mListAdapter.getItem(i));
        }
        outState.putParcelableArrayList(SAVED_STATE_PHONE_ARRAY_LIST, phoneArrayList);
    }

    private void bindViews() {
        mListView = (ListView) findViewById(android.R.id.list);
        mListView.setAdapter(new PhoneListAdapter(this));

        mTextViewEmpty = (TextView) findViewById(android.R.id.empty);

        mListView.setOnItemClickListener(this);
        registerForContextMenu(mListView);
    }

    @Override
    protected Dialog onCreateDialog(final int id) {
        Builder b;
        switch (id) {
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
                Phone phone = (mListAdapter).getItem(mPositionToDelete);

                b = new Builder(this);
                b.setIcon(android.R.drawable.ic_dialog_alert);
                b.setTitle(R.string.crud_phone_list_dialog_delete_confirm_title);
                b.setMessage(getString(R.string.crud_phone_list_dialog_delete_confirm_message,
                        phone.name));
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
            case DialogConfig.DIALOG_DELETE_CONFIRM:
                ((AlertDialog) dialog).setMessage(getString(
                        R.string.crud_phone_list_dialog_delete_confirm_message,
                        (mListAdapter).getItem(mPositionToDelete).name));
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
                    final long deletedPhoneId = data
                            .getLongExtra(RESULT_EXTRA_DELETED_PHONE_ID, -1);
                    final Phone editedPhone = data.getParcelableExtra(RESULT_EXTRA_EDITED_PHONE);

                    if (deletedPhoneId != -1) {
                        mListAdapter.setNotifyOnChange(false);
                        for (int i = 0; i < mListAdapter.getCount(); i++) {
                            final Phone phone = mListAdapter.getItem(i);
                            if (phone.serverId == deletedPhoneId) {
                                mListAdapter.remove(phone);
                                break;
                            }
                        }
                        mListAdapter.notifyDataSetChanged();
                    } else if (editedPhone != null) {
                        final int adapterCount = mListAdapter.getCount();
                        mListAdapter.setNotifyOnChange(false);
                        for (int i = 0; i < adapterCount; i++) {
                            final Phone phone = mListAdapter.getItem(i);
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
                        mListAdapter.notifyDataSetChanged();
                    }
                }
                break;
            }
            case ACTIVITY_FOR_RESULT_ADD: {
                if (resultCode == RESULT_OK) {
                    final Phone addedPhone = data.getParcelableExtra(RESULT_EXTRA_ADDED_PHONE);

                    mListAdapter.setNotifyOnChange(false);
                    mListAdapter.add(addedPhone);
                    mListAdapter.notifyDataSetChanged();
                }
                break;
            }
            case ACTIVITY_FOR_RESULT_EDIT: {
                if (resultCode == RESULT_OK) {
                    final Phone editedPhone = data.getParcelableExtra(RESULT_EXTRA_EDITED_PHONE);

                    final int adapterCount = mListAdapter.getCount();
                    mListAdapter.setNotifyOnChange(false);
                    for (int i = 0; i < adapterCount; i++) {
                        final Phone phone = mListAdapter.getItem(i);
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
                    mListAdapter.notifyDataSetChanged();
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
                startActivityForResult(new Intent(this, CrudSyncPhoneAddEditActivity.class),
                        ACTIVITY_FOR_RESULT_ADD);
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
        Request request = PoCRequestFactory.createGetSyncPhoneListRequest(mUserId);
        mRequestManager.execute(request, this);
        mRequestList.add(request);
    }

    private void callSyncPhoneDeleteMonoWS() {
        callSyncPhoneDeleteWS(String.valueOf((mListAdapter)
                .getItem(mPositionToDelete).serverId));
    }

    private void callSyncPhoneDeleteAllWS() {
        final StringBuilder sb = new StringBuilder();
        final int adapterCount = mListAdapter.getCount();
        for (int i = 0; i < adapterCount; i++) {
            sb.append(mListAdapter.getItem(i).serverId);
            if (i != adapterCount - 1) {
                sb.append(",");
            }
        }
        callSyncPhoneDeleteWS(sb.toString());
    }

    private void callSyncPhoneDeleteWS(final String phoneIdList) {
        new ProgressDialogFragmentBuilder(this)
                .setMessage(R.string.progress_dialog_message)
                .setCancelable(true)
                .show();
        Request request = PoCRequestFactory.createDeleteSyncPhonesRequest(mUserId, phoneIdList);
        mRequestManager.execute(request, this);
        mRequestList.add(request);
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position,
            final long id) {
        if (parent == mListView) {
            Intent intent = new Intent(this, CrudSyncPhoneViewActivity.class);
            intent.putExtra(CrudSyncPhoneViewActivity.INTENT_EXTRA_PHONE,
                    (mListAdapter).getItem(position));
            startActivityForResult(intent, ACTIVITY_FOR_RESULT_VIEW);
        }
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v,
            final ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.crud_phone_list_context, menu);

        menu.setHeaderTitle((mListAdapter)
                .getItem(((AdapterContextMenuInfo) menuInfo).position).name);
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        final int itemId = item.getItemId();

        final int position = ((AdapterContextMenuInfo) item.getMenuInfo()).position;

        switch (itemId) {
            case R.id.menu_edit:
                Phone phone = (mListAdapter).getItem(position);
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
    public void onRequestFinished(Request request, Bundle resultData) {
        if (mRequestList.contains(request)) {
            final int requestType = request.getRequestType();
            if (requestType == REQUEST_TYPE_LIST) {
                setProgressBarIndeterminateVisibility(false);
            } else {
                ProgressDialogFragment.dismiss(this);
            }
            mRequestList.remove(request);

            switch (request.getRequestType()) {
                case PoCRequestFactory.REQUEST_TYPE_CRUD_SYNC_PHONE_LIST: {
                    mArePhonesLoaded = true;

                    final ArrayList<Phone> syncPhoneList = resultData
                            .getParcelableArrayList(PoCRequestFactory.BUNDLE_EXTRA_PHONE_LIST);

                    if (syncPhoneList.size() == 0) {
                        mTextViewEmpty.setText(R.string.crud_phone_list_tv_empty_no_results);
                    }

                    mListAdapter.clear();
                    mListAdapter.setNotifyOnChange(false);
                    for (Phone phone : syncPhoneList) {
                        mListAdapter.add(phone);
                    }
                    mListAdapter.notifyDataSetChanged();
                    break;
                }
                case PoCRequestFactory.REQUEST_TYPE_CRUD_SYNC_PHONE_DELETE: {
                    final long[] syncDeletedPhoneIdArray = resultData
                            .getLongArray(PoCRequestFactory.BUNDLE_EXTRA_PHONE_DELETE_DATA);

                    mListAdapter.setNotifyOnChange(false);
                    for (int i = 0; i < mListAdapter.getCount(); i++) {
                        final Phone phone = mListAdapter.getItem(i);
                        if (ArrayUtils.inArray(syncDeletedPhoneIdArray, phone.serverId)) {
                            mListAdapter.remove(phone);
                            i--;
                        }
                    }
                    mListAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Override
    public void onRequestConnectionError(Request request) {
        if (mRequestList.contains(request)) {
            final int requestType = request.getRequestType();
            if (requestType == REQUEST_TYPE_LIST) {
                setProgressBarIndeterminateVisibility(false);
            } else {
                ProgressDialogFragment.dismiss(this);
            }
            mRequestList.remove(request);

            ConnexionErrorDialogFragment.show(this, request, this);
        }
    }

    @Override
    public void onRequestDataError(Request request) {
        if (mRequestList.contains(request)) {
            final int requestType = request.getRequestType();
            if (requestType == REQUEST_TYPE_LIST) {
                setProgressBarIndeterminateVisibility(false);
            } else {
                ProgressDialogFragment.dismiss(this);
            }
            mRequestList.remove(request);

            showBadDataErrorDialog();
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
