/*
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.datadroidpoc.data.worker;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;

import android.os.Bundle;

import org.json.JSONException;
import org.xml.sax.SAXException;

import com.foxykeep.datadroid.exception.RestClientException;
import com.foxykeep.datadroid.factory.RssFactory;
import com.foxykeep.datadroid.model.RssFeed;
import com.foxykeep.datadroid.network.NetworkConnection;
import com.foxykeep.datadroid.network.NetworkConnection.NetworkConnectionResult;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestManager;

public class RssFeedWorker {

    public static final int RETURN_FORMAT_XML = 0;
    public static final int RETURN_FORMAT_JSON = 1;

    public static Bundle start(final String feedUrl) throws IllegalStateException, IOException, URISyntaxException, RestClientException,
            ParserConfigurationException, SAXException, JSONException {

        NetworkConnectionResult wsResult = NetworkConnection.retrieveResponseFromService(feedUrl, NetworkConnection.METHOD_GET);

        final Bundle bundle = new Bundle();
        final RssFeed rssFeed = RssFactory.parseResult(wsResult.wsResponse);
        bundle.putParcelable(PoCRequestManager.RECEIVER_EXTRA_RSS_FEED_DATA, rssFeed);
        return bundle;
    }
}
