
package com.foxykeep.datadroidpoc.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.foxykeep.datadroidpoc.R;

@SuppressLint("ValidFragment")
public final class ProgressDialogFragment extends DialogFragment {

    private static final String FRAGMENT_TAG = "com.foxykeep.datadroidpoc.dialogs.progressDialog";

    private static final String BUNDLE_MESSAGE = "message";
    private static final String BUNDLE_IS_CANCELABLE = "isCancelable";

    private OnCancelListener mOnCancelListener;

    public ProgressDialogFragment() {
    }

    private ProgressDialogFragment(String message, OnCancelListener onCancelListener,
            boolean isCancelable) {
        Bundle args = new Bundle();
        args.putString(BUNDLE_MESSAGE, message);
        args.putBoolean(BUNDLE_IS_CANCELABLE, isCancelable);
        setArguments(args);

        mOnCancelListener = onCancelListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setTitle(R.string.progress_dialog_title);
        dialog.setMessage(args.getString(BUNDLE_MESSAGE));
        dialog.setIndeterminate(true);
        dialog.setCancelable(args.getBoolean(BUNDLE_IS_CANCELABLE));
        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        if (mOnCancelListener != null) {
            mOnCancelListener.onCancel(dialog);
        }
    }

    public static class ProgressDialogFragmentBuilder {

        private FragmentActivity mActivity;
        private String mMessage = null;
        private OnCancelListener mOnCancelListener = null;
        private boolean mCancelable = true;

        public ProgressDialogFragmentBuilder(FragmentActivity activity) {
            mActivity = activity;
        }

        public ProgressDialogFragmentBuilder setMessage(int resId) {
            mMessage = mActivity.getString(resId);
            return this;
        }

        public ProgressDialogFragmentBuilder setMessage(String text) {
            mMessage = text;
            return this;
        }

        public ProgressDialogFragmentBuilder setOnCancelListener(
                OnCancelListener onCancelListener) {
            mOnCancelListener = onCancelListener;
            return this;
        }

        public ProgressDialogFragmentBuilder setCancelable(boolean cancelable) {
            mCancelable = cancelable;
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

            new ProgressDialogFragment(mMessage, mOnCancelListener, mCancelable).show(
                    fragmentManager, FRAGMENT_TAG);
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
