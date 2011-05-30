/*
 * 2011 Foxykeep (http://www.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.dataproxypoc.data.service;

import android.content.Intent;
import android.util.Log;

import com.foxykeep.dataproxy.service.WorkerService;
import com.foxykeep.dataproxypoc.data.requestmanager.PoCRequestManager;
import com.foxykeep.dataproxypoc.data.worker.PersonWorker;

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
    public static final int WORKER_TYPE_PERSONS = 0;

    // Worker params
    public static final String INTENT_EXTRA_PERSONS_MIN_AGE = "personsMinAge";
    public static final String INTENT_EXTRA_PERSONS_RETURN_FORMAT = "personsReturnFormat";

    public PoCService() {
        super(MAX_THREADS);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        final int workerType = intent.getIntExtra(INTENT_EXTRA_WORKER_TYPE, -1);

        try {
            switch (workerType) {
                case WORKER_TYPE_PERSONS:
                    sendSuccess(intent, PersonWorker.start(this, intent.getIntExtra(INTENT_EXTRA_PERSONS_MIN_AGE, -1),
                            intent.getIntExtra(INTENT_EXTRA_PERSONS_RETURN_FORMAT, PersonWorker.RETURN_FORMAT_XML)));
                    break;
            }
        } catch (final Exception e) {
            Log.e(LOG_TAG, "Erreur", e);
            sendFailure(intent, null);
        }
    }
}
