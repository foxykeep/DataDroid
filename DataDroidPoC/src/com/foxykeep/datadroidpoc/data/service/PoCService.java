/*
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.datadroidpoc.data.service;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;

import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.xml.sax.SAXException;

import com.foxykeep.datadroid.exception.RestClientException;
import com.foxykeep.datadroid.network.NetworkConnection;
import com.foxykeep.datadroid.service.WorkerService;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestManager;
import com.foxykeep.datadroidpoc.data.worker.CityListWorker;
import com.foxykeep.datadroidpoc.data.worker.CrudSyncPhoneAddEditWorker;
import com.foxykeep.datadroidpoc.data.worker.CrudSyncPhoneDeleteWorker;
import com.foxykeep.datadroidpoc.data.worker.CrudSyncPhoneListWorker;
import com.foxykeep.datadroidpoc.data.worker.PersonListWorker;
import com.foxykeep.datadroidpoc.data.worker.RssFeedWorker;

/**
 * This class is called by the {@link PoCRequestManager} through the {@link Intent} system. Get the parameters stored in the {@link Intent} and call
 * the right Worker.
 * 
 * @author Foxykeep
 */
public class PoCService extends WorkerService {

    private static final String LOG_TAG = PoCService.class.getSimpleName();

    // Max number of parallel threads used
    private static final int MAX_THREADS = 3;

    // Worker types
    public static final int WORKER_TYPE_PERSON_LIST = 0;
    public static final int WORKER_TYPE_CITY_LIST = 1;
    public static final int WORKER_TYPE_CRUD_SYNC_PHONE_LIST = 2;
    public static final int WORKER_TYPE_CRUD_SYNC_PHONE_DELETE = 3;
    public static final int WORKER_TYPE_CRUD_SYNC_PHONE_ADD = 4;
    public static final int WORKER_TYPE_CRUD_SYNC_PHONE_EDIT = 5;
    public static final int WORKER_TYPE_RSS_FEED = 10;

    // Worker params
    // - PersonList WS params
    public static final String INTENT_EXTRA_PERSON_LIST_RETURN_FORMAT = "com.foxykeep.datadroidpoc.extras.personsReturnFormat";
    // - CrudSyncPhoneList WS params
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_LIST_USER_ID = "com.foxykeep.datadroidpoc.extras.crudPhoneListUserId";
    // - CrudSyncPhoneDelete WS params
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_DELETE_USER_ID = "com.foxykeep.datadroidpoc.extras.crudPhoneDeleteUserId";
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_DELETE_PHONE_ID_LIST = "com.foxykeep.datadroidpoc.extras.crudPhoneDeletePhoneIdList";
    // - CrudSyncPhoneAdd WS params
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_USER_ID = "com.foxykeep.datadroidpoc.extras.crudPhoneAddUserId";
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_NAME = "com.foxykeep.datadroidpoc.extras.crudPhoneAddName";
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_MANUFACTURER = "com.foxykeep.datadroidpoc.extras.crudPhoneAddManufacturer";
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_ANDROID_VERSION = "com.foxykeep.datadroidpoc.extras.crudPhoneAddAndroidVersion";
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_SCREEN_SIZE = "com.foxykeep.datadroidpoc.extras.crudPhoneAddScreenSize";
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_PRICE = "com.foxykeep.datadroidpoc.extras.crudPhoneAddPrice";
    // - CrudSyncPhoneEdit WS params
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_USER_ID = "com.foxykeep.datadroidpoc.extras.crudPhoneEditUserId";
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_ID = "com.foxykeep.datadroidpoc.extras.crudPhoneEditId";
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_NAME = "com.foxykeep.datadroidpoc.extras.crudPhoneEditName";
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_MANUFACTURER = "com.foxykeep.datadroidpoc.extras.crudPhoneEditManufacturer";
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_ANDROID_VERSION = "com.foxykeep.datadroidpoc.extras.crudPhoneEditAndroidVersion";
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_SCREEN_SIZE = "com.foxykeep.datadroidpoc.extras.crudPhoneEditScreenSize";
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_PRICE = "com.foxykeep.datadroidpoc.extras.crudPhoneEditPrice";
    // - RssFeed WS params
    public static final String INTENT_EXTRA_RSS_FEED_URL = "com.foxykeep.datadroidpoc.extras.rssFeedUrl";

    public PoCService() {
        super(MAX_THREADS);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        // This line will generate the Android User Agent which will be used in your webservice calls if you don't specify a special one
        NetworkConnection.generateDefaultUserAgent(this);

        final int workerType = intent.getIntExtra(INTENT_EXTRA_WORKER_TYPE, -1);

        try {
            switch (workerType) {
                case WORKER_TYPE_PERSON_LIST:
                    PersonListWorker.start(this, intent.getIntExtra(INTENT_EXTRA_PERSON_LIST_RETURN_FORMAT, PersonListWorker.RETURN_FORMAT_XML));
                    sendSuccess(intent, null);
                    break;
                case WORKER_TYPE_CITY_LIST:
                    sendSuccess(intent, CityListWorker.start());
                    break;
                case WORKER_TYPE_CRUD_SYNC_PHONE_LIST:
                    sendSuccess(intent, CrudSyncPhoneListWorker.start(intent.getStringExtra(INTENT_EXTRA_CRUD_SYNC_PHONE_LIST_USER_ID)));
                    break;
                case WORKER_TYPE_CRUD_SYNC_PHONE_DELETE:
                    sendSuccess(
                            intent,
                            CrudSyncPhoneDeleteWorker.start(intent.getStringExtra(INTENT_EXTRA_CRUD_SYNC_PHONE_DELETE_USER_ID),
                                    intent.getStringExtra(INTENT_EXTRA_CRUD_SYNC_PHONE_DELETE_PHONE_ID_LIST)));
                    break;
                case WORKER_TYPE_CRUD_SYNC_PHONE_ADD:
                    sendSuccess(
                            intent,
                            CrudSyncPhoneAddEditWorker.start(intent.getStringExtra(INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_USER_ID), -1,
                                    intent.getStringExtra(INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_NAME),
                                    intent.getStringExtra(INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_MANUFACTURER),
                                    intent.getStringExtra(INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_ANDROID_VERSION),
                                    intent.getDoubleExtra(INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_SCREEN_SIZE, -1),
                                    intent.getIntExtra(INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_PRICE, -1)));
                    break;
                case WORKER_TYPE_CRUD_SYNC_PHONE_EDIT:
                    sendSuccess(
                            intent,
                            CrudSyncPhoneAddEditWorker.start(intent.getStringExtra(INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_USER_ID),
                                    intent.getLongExtra(INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_ID, -1),
                                    intent.getStringExtra(INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_NAME),
                                    intent.getStringExtra(INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_MANUFACTURER),
                                    intent.getStringExtra(INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_ANDROID_VERSION),
                                    intent.getDoubleExtra(INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_SCREEN_SIZE, -1),
                                    intent.getIntExtra(INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_PRICE, -1)));
                    break;
                case WORKER_TYPE_RSS_FEED:
                    sendSuccess(intent, RssFeedWorker.start(intent.getStringExtra(INTENT_EXTRA_RSS_FEED_URL)));
            }
        } catch (final IllegalStateException e) {
            Log.e(LOG_TAG, "IllegalStateException", e);
            sendConnexionFailure(intent, null);
        } catch (final IOException e) {
            Log.e(LOG_TAG, "IOException", e);
            sendConnexionFailure(intent, null);
        } catch (final URISyntaxException e) {
            Log.e(LOG_TAG, "URISyntaxException", e);
            sendConnexionFailure(intent, null);
        } catch (final RestClientException e) {
            Log.e(LOG_TAG, "RestClientException", e);
            sendConnexionFailure(intent, null);
        } catch (final ParserConfigurationException e) {
            Log.e(LOG_TAG, "ParserConfigurationException", e);
            sendDataFailure(intent, null);
        } catch (final SAXException e) {
            Log.e(LOG_TAG, "SAXException", e);
            sendDataFailure(intent, null);
        } catch (final JSONException e) {
            Log.e(LOG_TAG, "JSONException", e);
            sendDataFailure(intent, null);
        }
        // This block (which should be the last one in your implementation)
        // will catch all the RuntimeException and send you back an error
        // that you can manage. If you remove this catch, the
        // RuntimeException will still crash the PoCService but you will not be
        // informed (as it is in 'background') so you should never remove this
        // catch
        catch (final RuntimeException e) {
            Log.e(LOG_TAG, "RuntimeException", e);
            sendDataFailure(intent, null);
        }
    }
}
