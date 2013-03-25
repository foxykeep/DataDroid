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
import com.foxykeep.datadroidpoc.config.WSConfig;
import com.foxykeep.datadroidpoc.data.factory.PersonListJsonFactory;
import com.foxykeep.datadroidpoc.data.factory.PersonListXmlFactory;
import com.foxykeep.datadroidpoc.data.model.Person;
import com.foxykeep.datadroidpoc.data.provider.PoCContent.DbPerson;
import com.foxykeep.datadroidpoc.data.provider.PoCProvider;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;

import java.util.ArrayList;

public final class PersonListOperation implements Operation {

    public static final String PARAM_RETURN_FORMAT =
            "com.foxykeep.datadroidpoc.extra.returnFormat";
    public static final int RETURN_FORMAT_XML = 0;
    public static final int RETURN_FORMAT_JSON = 1;

    @Override
    public Bundle execute(Context context, Request request) throws ConnectionException,
            DataException {
        int returnFormat = request.getInt(PARAM_RETURN_FORMAT);

        String url = returnFormat == RETURN_FORMAT_XML ? WSConfig.WS_PERSON_LIST_URL_XML
                : WSConfig.WS_PERSON_LIST_URL_JSON;
        NetworkConnection networkConnection = new NetworkConnection(context, url);
        ConnectionResult result = networkConnection.execute();

        ArrayList<Person> personList;
        if (returnFormat == RETURN_FORMAT_XML) {
            personList = PersonListXmlFactory.parseResult(result.body);
        } else {
            personList = PersonListJsonFactory.parseResult(result.body);
        }

        // Clear the table
        context.getContentResolver().delete(DbPerson.CONTENT_URI, null, null);

        // Adds the persons in the database
        int personListSize = personList.size();
        if (personListSize > 0) {
            ArrayList<ContentProviderOperation> operationList =
                    new ArrayList<ContentProviderOperation>();

            for (int i = 0; i < personListSize; i++) {
                operationList.add(ContentProviderOperation.newInsert(DbPerson.CONTENT_URI)
                        .withValues(personList.get(i).toContentValues()).build());
            }

            try {
                context.getContentResolver().applyBatch(PoCProvider.AUTHORITY, operationList);
            } catch (RemoteException e) {
                throw new DataException(e);
            } catch (OperationApplicationException e) {
                throw new DataException(e);
            }
        }

        return null;
    }
}
