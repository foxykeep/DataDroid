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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.foxykeep.datadroid.model.RssFeed;
import com.foxykeep.datadroid.model.RssItem;
import com.foxykeep.datadroidpoc.R;
import com.foxykeep.datadroidpoc.config.DialogConfig;
import com.foxykeep.datadroidpoc.data.memprovider.MemoryProvider;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestManager;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestManager.OnRequestFinishedListener;
import com.foxykeep.datadroidpoc.data.service.PoCService;

public class RssFeedListActivity extends ListActivity implements OnRequestFinishedListener, OnClickListener, OnItemClickListener {

    private static final String SAVED_STATE_REQUEST_ID = "savedStateRequestId";
    private static final String SAVED_STATE_ERROR_TITLE = "savedStateErrorTitle";
    private static final String SAVED_STATE_ERROR_MESSAGE = "savedStateErrorMessage";

    private Spinner mSpinnerFeedUrl;
    private Button mButtonLoad;
    private Button mButtonClearMemory;

    private String[] mFeedUrlArray;

    private PoCRequestManager mRequestManager;
    private int mRequestId = -1;

    private MemoryProvider mMemoryProvider = MemoryProvider.getInstance();

    private LayoutInflater mInflater;

    private String mErrorDialogTitle;
    private String mErrorDialogMessage;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.rss_feed_list);
        bindViews();
        setListAdapter(new RssItemListAdapter(this));

        mFeedUrlArray = getResources().getStringArray(R.array.rss_feed_url);

        if (savedInstanceState != null) {
            mRequestId = savedInstanceState.getInt(SAVED_STATE_REQUEST_ID, -1);
            mErrorDialogTitle = savedInstanceState.getString(SAVED_STATE_ERROR_TITLE);
            mErrorDialogMessage = savedInstanceState.getString(SAVED_STATE_ERROR_MESSAGE);
        }

        mRequestManager = PoCRequestManager.from(this);
        mInflater = getLayoutInflater();

        final Object data = getLastNonConfigurationInstance();
        if (data != null) {
            RetainData retainData = (RetainData) data;

            if (retainData.rssItemArray != null & retainData.rssItemArray.length > 0) {
                final RssItemListAdapter adapter = (RssItemListAdapter) getListAdapter();
                adapter.setNotifyOnChange(false);
                for (RssItem rssItem : retainData.rssItemArray) {
                    adapter.add(rssItem);
                }
                adapter.notifyDataSetChanged();
            }
        }
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

                if (mMemoryProvider.rssFeed == null) {
                    showDialog(DialogConfig.DIALOG_CONNEXION_ERROR);
                } else {
                    final ArrayList<RssItem> rssItemList = mMemoryProvider.rssFeed.rssItemList;

                    final RssItemListAdapter adapter = (RssItemListAdapter) getListAdapter();
                    adapter.setNotifyOnChange(false);
                    for (RssItem rssItem : rssItemList) {
                        adapter.add(rssItem);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
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
    }

    class RetainData {
        public RssItem[] rssItemArray;
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        final RssItemListAdapter adapter = (RssItemListAdapter) getListAdapter();
        final int count = adapter.getCount();

        final RetainData retainData = new RetainData();
        retainData.rssItemArray = new RssItem[count];

        for (int i = 0; i < count; i++) {
            retainData.rssItemArray[i] = adapter.getItem(i);
        }

        return retainData;
    }

    private void bindViews() {
        mSpinnerFeedUrl = (Spinner) findViewById(R.id.sp_url);

        mButtonLoad = (Button) findViewById(R.id.b_load);
        mButtonLoad.setOnClickListener(this);

        mButtonClearMemory = (Button) findViewById(R.id.b_clear_memory);
        mButtonClearMemory.setOnClickListener(this);

        getListView().setOnItemClickListener(this);
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
                        callRssFeedWS();
                    }
                });
                b.setTitle(R.string.dialog_error_connexion_error_title);
                b.setMessage(R.string.dialog_error_connexion_error_message);
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

    private void callRssFeedWS() {
        ((RssItemListAdapter) getListAdapter()).clear();

        setProgressBarIndeterminateVisibility(true);
        mRequestManager.addOnRequestFinishedListener(this);
        mRequestId = mRequestManager.getRssFeed(mFeedUrlArray[mSpinnerFeedUrl.getSelectedItemPosition()]);
    }

    @Override
    public void onClick(final View view) {
        if (view == mButtonLoad) {
            callRssFeedWS();
        } else if (view == mButtonClearMemory) {
            mMemoryProvider.rssFeed = null;
            ((RssItemListAdapter) getListAdapter()).clear();
        }
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {

        final RssItem rssItem = ((RssItemListAdapter) parent.getAdapter()).getItem(position);
        if (rssItem != null) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(rssItem.link)));
        }
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
                final RssFeed rssFeed = payload.getParcelable(PoCRequestManager.RECEIVER_EXTRA_RSS_FEED_DATA);

                final RssItemListAdapter adapter = (RssItemListAdapter) getListAdapter();
                adapter.setNotifyOnChange(false);
                for (RssItem rssItem : rssFeed.rssItemList) {
                    adapter.add(rssItem);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    class ViewHolder {
        private TextView mTextViewTitle;
        private TextView mTextViewDescription;

        public ViewHolder(final View view) {
            mTextViewTitle = (TextView) view.findViewById(R.id.tv_title);
            mTextViewDescription = (TextView) view.findViewById(R.id.tv_description);
        }

        public void populateViews(final RssItem rssItem) {
            mTextViewTitle.setText(rssItem.title);
            mTextViewDescription.setText(Html.fromHtml(rssItem.description));
        }
    }

    class RssItemListAdapter extends ArrayAdapter<RssItem> {

        public RssItemListAdapter(final Context context) {
            super(context, 0);
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.rss_feed_list_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.populateViews(getItem(position));

            return convertView;
        }
    }
}
