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
import android.os.Bundle;
import android.os.RemoteException;

import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.CustomException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.network.NetworkConnection.ConnectionResult;
import com.foxykeep.datadroid.network.NetworkConnection.NetworkConnectionBuilder;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService.Operation;
import com.foxykeep.datadroidpoc.config.WSConfig;
import com.foxykeep.datadroidpoc.data.factory.PersonListJsonFactory;
import com.foxykeep.datadroidpoc.data.factory.PersonListXmlFactory;
import com.foxykeep.datadroidpoc.data.model.Person;
import com.foxykeep.datadroidpoc.data.provider.PoCContent.PersonDao;
import com.foxykeep.datadroidpoc.data.provider.PoCProvider;

import java.util.ArrayList;

public final class PersonListOperation implements Operation {

    public static final String PARAM_RETURN_FORMAT =
            "com.foxykeep.datadroidpoc.extras.returnFormat";
    public static final int RETURN_FORMAT_XML = 0;
    public static final int RETURN_FORMAT_JSON = 1;

    private Context mContext;

    public PersonListOperation(Context context) {
        mContext = context;
    }

    @Override
    public Bundle execute(Request request) throws ConnectionException, DataException,
            CustomException {
        final int returnFormat = request.getInt(PARAM_RETURN_FORMAT);

        NetworkConnectionBuilder builder = new NetworkConnectionBuilder(
                returnFormat == RETURN_FORMAT_XML ? WSConfig.WS_PERSON_LIST_URL_XML
                        : WSConfig.WS_PERSON_LIST_URL_JSON);
        ConnectionResult result = builder.execute();

        ArrayList<Person> personList = null;
        if (returnFormat == RETURN_FORMAT_XML) {
            personList = PersonListXmlFactory.parseResult(result.body);
        } else {
            personList = PersonListJsonFactory.parseResult(result.body);
        }

        // Clear the table
        mContext.getContentResolver().delete(PersonDao.CONTENT_URI, null, null);

        // Adds the persons in the database
        final int personListSize = personList.size();
        if (personList != null && personListSize > 0) {
            final ArrayList<ContentProviderOperation> operationList =
                    new ArrayList<ContentProviderOperation>();

            for (int i = 0; i < personListSize; i++) {
                operationList.add(ContentProviderOperation.newInsert(PersonDao.CONTENT_URI)
                        .withValues(PersonDao.getContentValues(personList.get(i)))
                        .build());
            }

            try {
                mContext.getContentResolver().applyBatch(PoCProvider.AUTHORITY, operationList);
            } catch (RemoteException e) {
                throw new DataException(e);
            } catch (OperationApplicationException e) {
                throw new DataException(e);
            }
        }

        return null;
    }
}
