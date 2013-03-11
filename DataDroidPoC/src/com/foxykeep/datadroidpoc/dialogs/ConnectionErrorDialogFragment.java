
package com.foxykeep.datadroidpoc.dialogs;

import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroidpoc.R;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public final class ConnectionErrorDialogFragment extends DialogFragment {

    public interface ConnectionErrorDialogListener {
        public void connectionErrorDialogCancel(Request request);

        public void connectionErrorDialogRetry(Request request);
    }

    private static final String FRAGMENT_TAG =
            "com.foxykeep.datadroidpoc.dialogs.connectionErrorDialog";

    private ConnectionErrorDialogListener mConnexionErrorDialogListener;
    private Request mRequest;

    private static ConnectionErrorDialogFragment newInstance(Request request,
            ConnectionErrorDialogListener connexionErrorDialogListener) {
        ConnectionErrorDialogFragment dialogFragment = new ConnectionErrorDialogFragment();
        dialogFragment.mConnexionErrorDialogListener = connexionErrorDialogListener;
        dialogFragment.mRequest = request;
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Builder b = new Builder(getActivity());
        b.setTitle(R.string.dialog_error_connection_error_title);
        b.setMessage(R.string.dialog_error_connection_error_message);
        b.setIcon(android.R.drawable.ic_dialog_alert);
        setCancelable(true);
        b.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (mConnexionErrorDialogListener != null) {
                    mConnexionErrorDialogListener.connectionErrorDialogCancel(mRequest);
                }
            }
        });
        b.setPositiveButton(R.string.dialog_button_retry, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (mConnexionErrorDialogListener != null) {
                    mConnexionErrorDialogListener.connectionErrorDialogRetry(mRequest);
                }
            }
        });
        return b.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (mConnexionErrorDialogListener != null) {
            mConnexionErrorDialogListener.connectionErrorDialogCancel(mRequest);
        }
    }

    public static void show(FragmentActivity activity, Request request,
            ConnectionErrorDialogListener connexionErrrorDialogListener) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment prev = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);

        ConnectionErrorDialogFragment.newInstance(request, connexionErrrorDialogListener)
                .show(fragmentManager, FRAGMENT_TAG);
    }

    public static void dismiss(FragmentActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment prev = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.commit();
    }
}
