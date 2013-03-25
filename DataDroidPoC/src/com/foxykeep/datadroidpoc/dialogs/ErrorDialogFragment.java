
package com.foxykeep.datadroidpoc.dialogs;

import com.foxykeep.datadroidpoc.R;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class ErrorDialogFragment extends DialogFragment {

    public interface OnCancelListener {
        public void onCancel();
    }

    private static final String FRAGMENT_TAG = "com.foxykeep.datadroidpoc.dialogs.errorDialog";

    private static final String BUNDLE_TITLE = "title";
    private static final String BUNDLE_MESSAGE = "message";

    private OnClickListener mOnClickListener;
    private OnCancelListener mOnCancelListener;

    private static ErrorDialogFragment newInstance(String title, String message,
            OnClickListener onClickListener, OnCancelListener onCancelListener) {
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        dialogFragment.mOnClickListener = onClickListener;

        Bundle args = new Bundle();
        args.putString(BUNDLE_TITLE, title);
        args.putString(BUNDLE_MESSAGE, message);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        Builder b = new Builder(getActivity());
        b.setTitle(args.getString(BUNDLE_TITLE));
        b.setMessage(args.getString(BUNDLE_MESSAGE));
        b.setIcon(android.R.drawable.ic_dialog_alert);
        setCancelable(true);
        b.setNeutralButton(getActivity().getString(android.R.string.ok), mOnClickListener);
        return b.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (mOnCancelListener != null) {
            mOnCancelListener.onCancel();
        }
    }

    public static class ErrorDialogFragmentBuilder {
        private FragmentActivity mActivity;
        private String mTitle;
        private String mMessage;
        private OnClickListener mOnClickListener;
        private OnCancelListener mOnCancelListener;

        public ErrorDialogFragmentBuilder(FragmentActivity activity) {
            mActivity = activity;

            mTitle = activity.getString(R.string.dialog_error_title);
        }

        public ErrorDialogFragmentBuilder setTitle(int resId) {
            mTitle = mActivity.getString(resId);
            return this;
        }

        public ErrorDialogFragmentBuilder setTitle(String text) {
            mTitle = text;
            return this;
        }

        public ErrorDialogFragmentBuilder setMessage(int resId) {
            mMessage = mActivity.getString(resId);
            return this;
        }

        public ErrorDialogFragmentBuilder setMessage(String text) {
            mMessage = text;
            return this;
        }

        public ErrorDialogFragmentBuilder setOnClickListener(OnClickListener onClickListener) {
            mOnClickListener = onClickListener;
            return this;
        }

        public ErrorDialogFragmentBuilder setOnCancelListener(OnCancelListener onCancelListener) {
            mOnCancelListener = onCancelListener;
            return this;
        }

        public void show() {
            FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Fragment prev = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
            if (prev != null) {
                fragmentTransaction.remove(prev);
            }
            fragmentTransaction.addToBackStack(null);

            ErrorDialogFragment.newInstance(mTitle, mMessage, mOnClickListener, mOnCancelListener)
                    .show(fragmentManager, FRAGMENT_TAG);
        }
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
