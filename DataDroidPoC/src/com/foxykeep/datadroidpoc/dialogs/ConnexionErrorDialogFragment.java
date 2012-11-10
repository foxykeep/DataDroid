
package com.foxykeep.datadroidpoc.dialogs;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroidpoc.R;

public final class ConnexionErrorDialogFragment extends DialogFragment {

    public interface ConnexionErrorDialogListener {
        public void onConnexionErrorDialogCancel();

        public void onConnexionErrorDialogRetry(Request request);
    }

    private static final String FRAGMENT_TAG =
            "com.foxykeep.datadroidpoc.dialogs.connexionErrorDialog";

    private ConnexionErrorDialogListener mConnexionErrorDialogListener;
    private Request mRequest;

    private static ConnexionErrorDialogFragment newInstance(Request request,
            ConnexionErrorDialogListener connexionErrorDialogListener) {
        ConnexionErrorDialogFragment dialogFragment = new ConnexionErrorDialogFragment();
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
        b.setCancelable(true);
        b.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (mConnexionErrorDialogListener != null) {
                    mConnexionErrorDialogListener.onConnexionErrorDialogCancel();
                }
            }
        });
        b.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (mConnexionErrorDialogListener != null) {
                    mConnexionErrorDialogListener.onConnexionErrorDialogCancel();
                }
            }
        });
        b.setPositiveButton(R.string.dialog_button_retry, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (mConnexionErrorDialogListener != null) {
                    mConnexionErrorDialogListener.onConnexionErrorDialogRetry(mRequest);
                }
            }
        });
        return b.create();
    }

    public static void show(FragmentActivity activity, Request request,
            ConnexionErrorDialogListener connexionErrrorDialogListener) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment prev = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);

        ConnexionErrorDialogFragment.newInstance(request, connexionErrrorDialogListener)
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
