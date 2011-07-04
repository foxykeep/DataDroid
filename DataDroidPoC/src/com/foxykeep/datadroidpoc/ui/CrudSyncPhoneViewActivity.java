/*
 * 2011 Foxykeep (http://www.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.datadroidpoc.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.foxykeep.datadroidpoc.R;
import com.foxykeep.datadroidpoc.config.DialogConfig;
import com.foxykeep.datadroidpoc.data.model.Phone;

public class CrudSyncPhoneViewActivity extends Activity {

    public static final String INTENT_EXTRA_PHONE = "com.foxykeep.datadroidpoc.ui.extras.phone";

    private static final int ACTIVITY_FOR_RESULT_EDIT = 1;

    private Phone mPhone;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crud_phone_view);

        Intent intent = getIntent();
        if (intent != null) {
            mPhone = intent.getParcelableExtra(INTENT_EXTRA_PHONE);
        }

        populateViews();
    }

    private void populateViews() {
        ((TextView) findViewById(R.id.tv_name)).setText(mPhone.name);
        ((TextView) findViewById(R.id.tv_manufacturer)).setText(mPhone.manufacturer);
        ((TextView) findViewById(R.id.tv_android_version)).setText(mPhone.androidVersion);
        ((TextView) findViewById(R.id.tv_screen_size)).setText(getString(
                R.string.crud_phone_view_tv_screen_size_format, mPhone.screenSize));
        ((TextView) findViewById(R.id.tv_price)).setText(getString(R.string.crud_phone_view_tv_price_format,
                mPhone.price));
    }

    @Override
    protected Dialog onCreateDialog(final int id) {
        Builder b;
        switch (id) {
            case DialogConfig.DIALOG_DELETE_CONFIRM:
                b = new Builder(this);
                b.setIcon(android.R.drawable.ic_dialog_alert);
                b.setTitle(R.string.crud_phone_view_dialog_delete_confirm_title);
                b.setMessage(getString(R.string.crud_phone_view_dialog_delete_confirm_message, mPhone.name));
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        // TODO
                    }
                });
                b.setNegativeButton(android.R.string.cancel, null);
                b.setCancelable(true);
                return b.create();
            default:
                return super.onCreateDialog(id);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.crud_phone_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_edit:
                final Intent intent = new Intent(this, CrudSyncPhoneAddEditActivity.class);
                intent.putExtra(CrudSyncPhoneAddEditActivity.INTENT_EXTRA_PHONE, mPhone);
                startActivityForResult(intent, ACTIVITY_FOR_RESULT_EDIT);
                return true;
            case R.id.menu_delete:
                showDialog(DialogConfig.DIALOG_DELETE_CONFIRM);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
