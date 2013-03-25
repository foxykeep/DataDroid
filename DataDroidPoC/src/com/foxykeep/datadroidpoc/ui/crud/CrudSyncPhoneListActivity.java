/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.ui.crud;

import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager.RequestListener;
import com.foxykeep.datadroidpoc.R;
import com.foxykeep.datadroidpoc.data.model.Phone;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestFactory;
import com.foxykeep.datadroidpoc.dialogs.ConnectionErrorDialogFragment;
import com.foxykeep.datadroidpoc.dialogs.ConnectionErrorDialogFragment.ConnectionErrorDialogListener;
import com.foxykeep.datadroidpoc.dialogs.ProgressDialogFragment;
import com.foxykeep.datadroidpoc.dialogs.ProgressDialogFragment.ProgressDialogFragmentBuilder;
import com.foxykeep.datadroidpoc.dialogs.QuestionDialogFragment.QuestionDialogFragmentBuilder;
import com.foxykeep.datadroidpoc.ui.DataDroidActivity;
import com.foxykeep.datadroidpoc.util.ArrayUtils;
import com.foxykeep.datadroidpoc.util.UserManager;

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

import java.util.ArrayList;

public final class CrudSyncPhoneListActivity extends DataDroidActivity implements RequestListener,
        OnItemClickListener, ConnectionErrorDialogListener {

    private static final String SAVED_STATE_POSITION_TO_DELETE = "savedStatePositionToDelete";
    private static final String SAVED_STATE_ARE_PHONES_LOADED = "savedStateIsResultLoaded";
    private static final String SAVED_STATE_PHONE_ARRAY_LIST = "savedStatePhoneArrayList";

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.crud_phone_list);
        bindViews();

        if (savedInstanceState != null) {
            mPositionToDelete = savedInstanceState.getInt(SAVED_STATE_POSITION_TO_DELETE);
            mArePhonesLoaded = savedInstanceState.getBoolean(SAVED_STATE_ARE_PHONES_LOADED, false);

            ArrayList<Phone> phoneArrayList = savedInstanceState
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
        for (int i = 0; i < mRequestList.size(); i++) {
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
                i--;
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(SAVED_STATE_POSITION_TO_DELETE, mPositionToDelete);
        outState.putBoolean(SAVED_STATE_ARE_PHONES_LOADED, mArePhonesLoaded);

        ArrayList<Phone> phoneArrayList = new ArrayList<Phone>();

        int adapterCount = mListAdapter.getCount();
        for (int i = 0; i < adapterCount; i++) {
            phoneArrayList.add(mListAdapter.getItem(i));
        }
        outState.putParcelableArrayList(SAVED_STATE_PHONE_ARRAY_LIST, phoneArrayList);
    }

    private void bindViews() {
        mListView = (ListView) findViewById(android.R.id.list);
        mListAdapter = new PhoneListAdapter(this);
        mListView.setAdapter(mListAdapter);
        mListView.setEmptyView(findViewById(android.R.id.empty));

        mTextViewEmpty = (TextView) findViewById(android.R.id.empty);

        mListView.setOnItemClickListener(this);
        registerForContextMenu(mListView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVITY_FOR_RESULT_VIEW: {
                if (resultCode == RESULT_OK) {
                    long deletedPhoneId = data
                            .getLongExtra(RESULT_EXTRA_DELETED_PHONE_ID, -1);
                    Phone editedPhone = data.getParcelableExtra(RESULT_EXTRA_EDITED_PHONE);

                    if (deletedPhoneId != -1) {
                        mListAdapter.setNotifyOnChange(false);
                        for (int i = 0; i < mListAdapter.getCount(); i++) {
                            Phone phone = mListAdapter.getItem(i);
                            if (phone.serverId == deletedPhoneId) {
                                mListAdapter.remove(phone);
                                break;
                            }
                        }
                        mListAdapter.notifyDataSetChanged();
                    } else if (editedPhone != null) {
                        int adapterCount = mListAdapter.getCount();
                        mListAdapter.setNotifyOnChange(false);
                        for (int i = 0; i < adapterCount; i++) {
                            Phone phone = mListAdapter.getItem(i);
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
                    Phone addedPhone = data.getParcelableExtra(RESULT_EXTRA_ADDED_PHONE);

                    mListAdapter.setNotifyOnChange(false);
                    mListAdapter.add(addedPhone);
                    mListAdapter.notifyDataSetChanged();
                }
                break;
            }
            case ACTIVITY_FOR_RESULT_EDIT: {
                if (resultCode == RESULT_OK) {
                    Phone editedPhone = data.getParcelableExtra(RESULT_EXTRA_EDITED_PHONE);

                    int adapterCount = mListAdapter.getCount();
                    mListAdapter.setNotifyOnChange(false);
                    for (int i = 0; i < adapterCount; i++) {
                        Phone phone = mListAdapter.getItem(i);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.crud_phone_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_add:
                startActivityForResult(new Intent(this, CrudSyncPhoneAddEditActivity.class),
                        ACTIVITY_FOR_RESULT_ADD);
                return true;
            case R.id.menu_delete_all:
                QuestionDialogFragmentBuilder b = new QuestionDialogFragmentBuilder(this);
                b.setTitle(R.string.crud_phone_list_dialog_delete_all_confirm_title);
                b.setMessage(R.string.crud_phone_list_dialog_delete_all_confirm_message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callSyncPhoneDeleteAllWS();
                    }
                });
                b.setNegativeButton(android.R.string.cancel, null);
                b.show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void callSyncPhoneListWS() {
        setProgressBarIndeterminateVisibility(true);
        Request request = PoCRequestFactory.getSyncPhoneListRequest(mUserId);
        mRequestManager.execute(request, this);
        mRequestList.add(request);
    }

    private void callSyncPhoneDeleteMonoWS() {
        callSyncPhoneDeleteWS(String.valueOf(mListAdapter
                .getItem(mPositionToDelete).serverId));
    }

    private void callSyncPhoneDeleteAllWS() {
        StringBuilder sb = new StringBuilder();
        int adapterCount = mListAdapter.getCount();
        for (int i = 0; i < adapterCount; i++) {
            sb.append(mListAdapter.getItem(i).serverId);
            if (i != adapterCount - 1) {
                sb.append(",");
            }
        }
        callSyncPhoneDeleteWS(sb.toString());
    }

    private void callSyncPhoneDeleteWS(String phoneIdList) {
        Request request = PoCRequestFactory.deleteSyncPhonesRequest(mUserId, phoneIdList);
        callSyncPhoneDeleteWS(request);
    }

    private void callSyncPhoneDeleteWS(Request request) {
        new ProgressDialogFragmentBuilder(this)
                .setMessage(R.string.progress_dialog_message)
                .setCancelable(true)
                .show();
        mRequestManager.execute(request, this);
        mRequestList.add(request);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        if (parent == mListView) {
            Intent intent = new Intent(this, CrudSyncPhoneViewActivity.class);
            intent.putExtra(CrudSyncPhoneViewActivity.INTENT_EXTRA_PHONE,
                    mListAdapter.getItem(position));
            startActivityForResult(intent, ACTIVITY_FOR_RESULT_VIEW);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.crud_phone_list_context, menu);

        menu.setHeaderTitle(mListAdapter
                .getItem(((AdapterContextMenuInfo) menuInfo).position).name);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        int position = ((AdapterContextMenuInfo) item.getMenuInfo()).position;

        switch (itemId) {
            case R.id.menu_edit: {
                Phone phone = mListAdapter.getItem(position);
                Intent intent = new Intent(this, CrudSyncPhoneAddEditActivity.class);
                intent.putExtra(CrudSyncPhoneAddEditActivity.INTENT_EXTRA_PHONE, phone);
                startActivityForResult(intent, ACTIVITY_FOR_RESULT_EDIT);
                return true;
            }
            case R.id.menu_delete: {
                mPositionToDelete = position;
                Phone phone = mListAdapter.getItem(mPositionToDelete);

                QuestionDialogFragmentBuilder b = new QuestionDialogFragmentBuilder(this);
                b.setTitle(R.string.crud_phone_list_dialog_delete_confirm_title);
                b.setMessage(getString(R.string.crud_phone_list_dialog_delete_confirm_message,
                        phone.name));
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callSyncPhoneDeleteMonoWS();
                    }
                });
                b.setNegativeButton(android.R.string.cancel, null);
                b.show();
                return true;
            }
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onRequestFinished(Request request, Bundle resultData) {
        for (int i = 0, length = mRequestList.size(); i < length; i++) {
        }
        if (mRequestList.contains(request)) {
            int requestType = request.getRequestType();
            if (requestType == PoCRequestFactory.REQUEST_TYPE_CRUD_SYNC_PHONE_LIST) {
                setProgressBarIndeterminateVisibility(false);
            } else {
                ProgressDialogFragment.dismiss(this);
            }
            mRequestList.remove(request);

            switch (requestType) {
                case PoCRequestFactory.REQUEST_TYPE_CRUD_SYNC_PHONE_LIST: {
                    mArePhonesLoaded = true;

                    ArrayList<Phone> syncPhoneList = resultData
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
                    long[] syncDeletedPhoneIdArray = resultData
                            .getLongArray(PoCRequestFactory.BUNDLE_EXTRA_PHONE_DELETE_DATA);

                    mListAdapter.setNotifyOnChange(false);
                    for (int i = 0; i < mListAdapter.getCount(); i++) {
                        Phone phone = mListAdapter.getItem(i);
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
    public void onRequestConnectionError(Request request, int statusCode) {
        if (mRequestList.contains(request)) {
            int requestType = request.getRequestType();
            if (requestType == PoCRequestFactory.REQUEST_TYPE_CRUD_SYNC_PHONE_LIST) {
                setProgressBarIndeterminateVisibility(false);
            } else {
                ProgressDialogFragment.dismiss(this);
            }
            mRequestList.remove(request);

            ConnectionErrorDialogFragment.show(this, request, this);
        }
    }

    @Override
    public void onRequestDataError(Request request) {
        if (mRequestList.contains(request)) {
            int requestType = request.getRequestType();
            if (requestType == PoCRequestFactory.REQUEST_TYPE_CRUD_SYNC_PHONE_LIST) {
                setProgressBarIndeterminateVisibility(false);
            } else {
                ProgressDialogFragment.dismiss(this);
            }
            mRequestList.remove(request);

            showBadDataErrorDialog();
        }
    }

    @Override
    public void onRequestCustomError(Request request, Bundle resultData) {
        // Never called.
    }

    @Override
    public void connectionErrorDialogCancel(Request request) {
        if (request.getRequestType() == PoCRequestFactory.REQUEST_TYPE_CRUD_SYNC_PHONE_LIST) {
            finish();
        }
    }

    @Override
    public void connectionErrorDialogRetry(Request request) {
        switch (request.getRequestType()) {
            case PoCRequestFactory.REQUEST_TYPE_CRUD_SYNC_PHONE_LIST:
                callSyncPhoneListWS();
                break;
            case PoCRequestFactory.REQUEST_TYPE_CRUD_SYNC_PHONE_DELETE:
                callSyncPhoneDeleteWS(request);
                break;
        }
    }

    class ViewHolder {
        private TextView mTextViewName;
        private TextView mTextViewManufacturer;

        public ViewHolder(View view) {
            mTextViewName = (TextView) view.findViewById(R.id.tv_name);
            mTextViewManufacturer = (TextView) view.findViewById(R.id.tv_manufacturer);
        }

        public void populateView(Phone phone) {
            mTextViewName.setText(phone.name);
            mTextViewManufacturer.setText(phone.manufacturer);
        }
    }

    class PhoneListAdapter extends ArrayAdapter<Phone> {

        public PhoneListAdapter(Context context) {
            super(context, -1);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
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