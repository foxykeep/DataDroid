
package com.foxykeep.datadroidpoc.dialogs;

import com.foxykeep.datadroidpoc.R;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class SampleDescriptionDialogFragment extends DialogFragment {

    public interface OnCancelListener {
        public void onCancel();
    }

    private static final String FRAGMENT_TAG =
            "com.foxykeep.datadroidpoc.dialogs.sampleDescription";

    private static final String BUNDLE_TITLE_RES_ID = "titleResId";
    private static final String BUNDLE_DESCRIPTION_RES_ID = "descriptionResId";

    private static SampleDescriptionDialogFragment newInstance(int titleResId,
            int descriptionResId) {
        SampleDescriptionDialogFragment dialogFragment = new SampleDescriptionDialogFragment();

        Bundle args = new Bundle();
        args.putInt(BUNDLE_TITLE_RES_ID, titleResId);
        args.putInt(BUNDLE_DESCRIPTION_RES_ID, descriptionResId);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();

        Builder b = new Builder(getActivity());
        b.setTitle(args.getInt(BUNDLE_TITLE_RES_ID));

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.home_dialog_sample_description, null);
        TextView textView = (TextView) view.findViewById(android.R.id.message);
        textView.setText(args.getInt(BUNDLE_DESCRIPTION_RES_ID));
        b.setView(view);

        b.setNeutralButton(android.R.string.ok, null);

        return b.create();
    }

    public static void show(FragmentActivity activity, int titleResId, int descriptionResId) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment prev = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);

        SampleDescriptionDialogFragment.newInstance(titleResId, descriptionResId)
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
