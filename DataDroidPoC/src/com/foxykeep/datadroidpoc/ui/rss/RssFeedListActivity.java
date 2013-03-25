/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.ui.rss;

import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager.RequestListener;
import com.foxykeep.datadroidpoc.R;
import com.foxykeep.datadroidpoc.data.model.RssFeed;
import com.foxykeep.datadroidpoc.data.model.RssItem;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestFactory;
import com.foxykeep.datadroidpoc.dialogs.ConnectionErrorDialogFragment;
import com.foxykeep.datadroidpoc.dialogs.ConnectionErrorDialogFragment.ConnectionErrorDialogListener;
import com.foxykeep.datadroidpoc.ui.DataDroidActivity;

import android.content.Context;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public final class RssFeedListActivity extends DataDroidActivity implements RequestListener,
        OnClickListener, OnItemClickListener, ConnectionErrorDialogListener {

    private static final String SAVED_STATE_RSS_ITEM_LIST = "savedStateRssItemList";

    private Spinner mSpinnerFeedUrl;
    private ListView mListView;
    private RssItemListAdapter mListAdapter;

    private String[] mFeedUrlArray;

    private LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.rss_feed_list);
        bindViews();

        mFeedUrlArray = getResources().getStringArray(R.array.rss_feed_url);

        mInflater = getLayoutInflater();
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (int i = 0; i < mRequestList.size(); i++) {
            Request request = mRequestList.get(i);

            if (mRequestManager.isRequestInProgress(request)) {
                mRequestManager.addRequestListener(this, request);
                setProgressBarIndeterminateVisibility(true);
            } else {
                mRequestManager.callListenerWithCachedData(this, request);
                i--;
                mRequestList.remove(request);
            }
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

        ArrayList<RssItem> rssItemList = new ArrayList<RssItem>();
        for (int i = 0, n = mListAdapter.getCount(); i < n; i++) {
            rssItemList.add(mListAdapter.getItem(i));
        }

        outState.putParcelableArrayList(SAVED_STATE_RSS_ITEM_LIST, rssItemList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        ArrayList<RssItem> rssItemList = savedInstanceState
                .getParcelableArrayList(SAVED_STATE_RSS_ITEM_LIST);
        mListAdapter.setNotifyOnChange(false);
        for (int i = 0, length = rssItemList.size(); i < length; i++) {
            mListAdapter.add(rssItemList.get(i));
        }
        mListAdapter.notifyDataSetChanged();
    }

    private void bindViews() {
        mSpinnerFeedUrl = (Spinner) findViewById(R.id.sp_url);

        ((Button) findViewById(R.id.b_load)).setOnClickListener(this);
        ((Button) findViewById(R.id.b_clear_memory)).setOnClickListener(this);

        mListView = (ListView) findViewById(android.R.id.list);
        mListAdapter = new RssItemListAdapter(this);
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setEmptyView(findViewById(android.R.id.empty));
    }

    private void callRssFeedWS() {
        mListAdapter.clear();
        setProgressBarIndeterminateVisibility(true);

        Request request = PoCRequestFactory.getRssFeedRequest(
                mFeedUrlArray[mSpinnerFeedUrl.getSelectedItemPosition()]);
        mRequestManager.execute(request, this);
        mRequestList.add(request);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_load:
                callRssFeedWS();
                break;
            case R.id.b_clear_memory:
                mListAdapter.clear();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        RssItem rssItem = ((RssItemListAdapter) parent.getAdapter()).getItem(position);
        if (rssItem != null) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(rssItem.link)));
        }
    }

    @Override
    public void onRequestFinished(Request request, Bundle resultData) {
        if (mRequestList.contains(request)) {
            setProgressBarIndeterminateVisibility(false);
            mRequestList.remove(request);

            RssFeed rssFeed = resultData
                    .getParcelable(PoCRequestFactory.BUNDLE_EXTRA_RSS_FEED_DATA);

            mListAdapter.setNotifyOnChange(false);
            for (RssItem rssItem : rssFeed.rssItemList) {
                mListAdapter.add(rssItem);
            }
            mListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestConnectionError(Request request, int statusCode) {
        if (mRequestList.contains(request)) {
            setProgressBarIndeterminateVisibility(false);
            mRequestList.remove(request);

            ConnectionErrorDialogFragment.show(this, request, this);
        }
    }

    @Override
    public void onRequestDataError(Request request) {
        if (mRequestList.contains(request)) {
            setProgressBarIndeterminateVisibility(false);
            mRequestList.remove(request);

            showBadDataErrorDialog();
        }
    }

    @Override
    public void onRequestCustomError(Request request, Bundle resultData) {
        // Never called.
    }

    @Override
    public void connectionErrorDialogCancel(Request request) {}

    @Override
    public void connectionErrorDialogRetry(Request request) {
        callRssFeedWS();
    }

    class ViewHolder {
        private TextView mTextViewTitle;
        private TextView mTextViewDescription;

        public ViewHolder(View view) {
            mTextViewTitle = (TextView) view.findViewById(R.id.tv_title);
            mTextViewDescription = (TextView) view.findViewById(R.id.tv_description);
        }

        public void populateViews(RssItem rssItem) {
            mTextViewTitle.setText(rssItem.title);
            mTextViewDescription.setText(Html.fromHtml(rssItem.description));
        }
    }

    class RssItemListAdapter extends ArrayAdapter<RssItem> {

        public RssItemListAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
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
