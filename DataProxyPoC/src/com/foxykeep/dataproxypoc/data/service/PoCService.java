/*
 * 2011 Foxykeep (http://www.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.dataproxypoc.data.service;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;

import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.xml.sax.SAXException;

import com.foxykeep.dataproxy.exception.RestClientException;
import com.foxykeep.dataproxy.service.WorkerService;
import com.foxykeep.dataproxypoc.data.requestmanager.PoCRequestManager;
import com.foxykeep.dataproxypoc.data.worker.PersonListWorker;

/**
 * This class is called by the {@link PoCRequestManager} through the
 * {@link Intent} system. Get the parameters stored in the {@link Intent} and
 * call the right Worker.
 * 
 * @author Foxykeep
 */
public class PoCService extends WorkerService {

    private static final String LOG_TAG = PoCService.class.getSimpleName();

    // Max number of parallel threads used
    private static final int MAX_THREADS = 3;

    // Worker types
    public static final int WORKER_TYPE_PERSON_LIST = 0;

    // Worker params
    // - Person WS params
    public static final String INTENT_EXTRA_PERSON_LIST_RETURN_FORMAT = "com.foxykeep.dataproxypoc.extras.personsReturnFormat";

    public PoCService() {
        super(MAX_THREADS);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        final int workerType = intent.getIntExtra(INTENT_EXTRA_WORKER_TYPE, -1);

        try {
            switch (workerType) {
                case WORKER_TYPE_PERSON_LIST:
                    PersonListWorker.start(this, intent.getIntExtra(INTENT_EXTRA_PERSON_LIST_RETURN_FORMAT,
                            PersonListWorker.RETURN_FORMAT_XML));
                    sendSuccess(intent, null);
                    break;
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
    }
}
