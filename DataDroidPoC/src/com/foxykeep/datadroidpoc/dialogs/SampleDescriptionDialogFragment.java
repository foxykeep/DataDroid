
package com.foxykeep.datadroidpoc.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SampleDescriptionDialogFragment extends DialogFragment {

    public interface OnCancelListener {
        public void onCancel();
    }

    private static final String FRAGMENT_TAG =
            "com.foxykeep.datadroidpoc.dialogs.sampleDescription";

    private static final String BUNDLE_DESCRIPTION_RES_ID = "descriptionResId";

    private static SampleDescriptionDialogFragment newInstance(int descriptionResId) {
        SampleDescriptionDialogFragment dialogFragment = new SampleDescriptionDialogFragment();

        Bundle args = new Bundle();
        args.putInt(BUNDLE_DESCRIPTION_RES_ID, descriptionResId);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO create layout (textview in scrollview)
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public static void show(FragmentActivity activity, int descriptionResId) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment prev = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);

        SampleDescriptionDialogFragment.newInstance(descriptionResId)
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
