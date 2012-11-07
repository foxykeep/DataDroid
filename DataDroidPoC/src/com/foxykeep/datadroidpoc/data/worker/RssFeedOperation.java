/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.data.worker;

import android.os.Bundle;

import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.factory.RssFactory;
import com.foxykeep.datadroid.model.RssFeed;
import com.foxykeep.datadroid.network.NetworkConnection.Builder;
import com.foxykeep.datadroid.network.NetworkConnection.ConnectionResult;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService.Operation;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestFactory;

public final class RssFeedOperation implements Operation {

    public RssFeedOperation() {
    }

    public static final String PARAM_FEED_URL = "com.foxykeep.datadroidpoc.extras.feedUrl";

    @Override
    public Bundle execute(Request request) throws ConnectionException, DataException {
        ConnectionResult result = new Builder(request.getString(PARAM_FEED_URL)).execute();

        final Bundle bundle = new Bundle();
        final RssFeed rssFeed = RssFactory.parseResult(result.body);
        bundle.putParcelable(PoCRequestFactory.BUNDLE_EXTRA_RSS_FEED_DATA, rssFeed);
        return bundle;
    }
}
