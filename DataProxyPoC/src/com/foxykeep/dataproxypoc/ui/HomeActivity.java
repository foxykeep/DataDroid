package com.foxykeep.dataproxypoc.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.foxykeep.dataproxypoc.R;

public class HomeActivity extends Activity implements OnClickListener {

    private Button mButtonPersonList;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        bindViews();
    }

    private void bindViews() {
        mButtonPersonList = (Button) findViewById(R.id.b_person_db_list);
        mButtonPersonList.setOnClickListener(this);
    }

    @Override
    public void onClick(final View view) {
        Intent intent = null;
        if (view == mButtonPersonList) {
            intent = new Intent(this, PersonDbListActivity.class);
        }

        if (intent != null) {
            startActivity(intent);
        }
    }
}
