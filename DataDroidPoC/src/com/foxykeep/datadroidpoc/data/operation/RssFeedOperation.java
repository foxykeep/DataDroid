/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.data.operation;

import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.network.NetworkConnection;
import com.foxykeep.datadroid.network.NetworkConnection.ConnectionResult;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService.Operation;
import com.foxykeep.datadroidpoc.data.factory.RssFactory;
import com.foxykeep.datadroidpoc.data.model.RssFeed;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestFactory;

import android.content.Context;
import android.os.Bundle;

public final class RssFeedOperation implements Operation {

    public static final String PARAM_FEED_URL = "com.foxykeep.datadroidpoc.extra.feedUrl";

    @Override
    public Bundle execute(Context context, Request request) throws ConnectionException,
            DataException {
        ConnectionResult result = new NetworkConnection(context, request.getString(PARAM_FEED_URL))
                .execute();

        Bundle bundle = new Bundle();
        RssFeed rssFeed = RssFactory.parseResult(result.body);
        bundle.putParcelable(PoCRequestFactory.BUNDLE_EXTRA_RSS_FEED_DATA, rssFeed);
        return bundle;
    }
}
