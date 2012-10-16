/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.data.worker;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;

import com.foxykeep.datadroid.exception.RestClientException;
import com.foxykeep.datadroid.network.NetworkConnection;
import com.foxykeep.datadroid.network.NetworkConnection.NetworkConnectionResult;
import com.foxykeep.datadroidpoc.config.WSConfig;
import com.foxykeep.datadroidpoc.data.factory.PersonListJsonFactory;
import com.foxykeep.datadroidpoc.data.factory.PersonListXmlFactory;
import com.foxykeep.datadroidpoc.data.model.Person;
import com.foxykeep.datadroidpoc.data.provider.PoCContent.PersonDao;
import com.foxykeep.datadroidpoc.data.provider.PoCProvider;

import org.json.JSONException;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

public final class PersonListWorker {

    private PersonListWorker() {
        // No public constructor
    }

    public static final int RETURN_FORMAT_XML = 0;
    public static final int RETURN_FORMAT_JSON = 1;

    public static void start(final Context context, final int returnFormat)
            throws IllegalStateException, IOException, URISyntaxException,
            RestClientException, ParserConfigurationException, SAXException, JSONException,
            RemoteException, OperationApplicationException {

        NetworkConnectionResult wsResult = NetworkConnection.retrieveResponseFromService(
                returnFormat == RETURN_FORMAT_XML ? WSConfig.WS_PERSON_LIST_URL_XML
                        : WSConfig.WS_PERSON_LIST_URL_JSON, NetworkConnection.METHOD_GET);

        ArrayList<Person> personList = null;
        if (returnFormat == RETURN_FORMAT_XML) {
            personList = PersonListXmlFactory.parseResult(wsResult.body);
        } else {
            personList = PersonListJsonFactory.parseResult(wsResult.body);
        }

        // Clear the table
        context.getContentResolver().delete(PersonDao.CONTENT_URI, null, null);

        // Adds the persons in the database
        final int personListSize = personList.size();
        if (personList != null && personListSize > 0) {
            final ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
            for (int i = 0; i < personListSize; i++) {
                operationList.add(ContentProviderOperation.newInsert(PersonDao.CONTENT_URI)
                        .withValues(PersonDao.getContentValues(personList.get(i)))
                        .build());
            }
            context.getContentResolver().applyBatch(PoCProvider.AUTHORITY, operationList);
        }
    }
}
