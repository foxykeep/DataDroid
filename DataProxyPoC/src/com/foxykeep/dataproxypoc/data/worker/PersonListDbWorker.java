/*
 * 2011 Foxykeep (http://www.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.dataproxypoc.data.worker;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import android.content.ContentValues;
import android.content.Context;

import org.json.JSONException;
import org.xml.sax.SAXException;

import com.foxykeep.dataproxy.exception.RestClientException;
import com.foxykeep.dataproxy.network.NetworkConnection;
import com.foxykeep.dataproxy.network.NetworkConnection.NetworkConnectionResult;
import com.foxykeep.dataproxypoc.config.WSConfig;
import com.foxykeep.dataproxypoc.data.factory.PersonJsonFactory;
import com.foxykeep.dataproxypoc.data.factory.PersonXmlFactory;
import com.foxykeep.dataproxypoc.data.model.Person;
import com.foxykeep.dataproxypoc.data.provider.PoCContent.PersonDao;

public class PersonListDbWorker {

    public static final int RETURN_FORMAT_XML = 0;
    public static final int RETURN_FORMAT_JSON = 1;

    public static void start(final Context context, final int returnFormat) throws IllegalStateException, IOException,
            URISyntaxException, RestClientException, ParserConfigurationException, SAXException, JSONException {

        NetworkConnectionResult wsResult = NetworkConnection.retrieveResponseFromService(
                returnFormat == RETURN_FORMAT_XML ? WSConfig.WS_PERSON_LIST_URL_XML : WSConfig.WS_PERSON_LIST_URL_JSON,
                NetworkConnection.METHOD_GET);

        ArrayList<Person> personList = null;
        if (returnFormat == RETURN_FORMAT_XML) {
            personList = PersonXmlFactory.parseResult(wsResult.mWsResponse);
        } else {
            personList = PersonJsonFactory.parseResult(wsResult.mWsResponse);
        }

        // Clear the table
        context.getContentResolver().delete(PersonDao.CONTENT_URI, null, null);

        // Adds the persons in the database
        final int personListSize = personList.size();
        if (personList != null && personListSize > 0) {
            ContentValues[] valuesArray = new ContentValues[personListSize];
            for (int i = 0; i < personListSize; i++) {
                valuesArray[i] = PersonDao.getContentValues(personList.get(i));
            }
            context.getContentResolver().bulkInsert(PersonDao.CONTENT_URI, valuesArray);
        }
    }
}
