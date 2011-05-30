package com.foxykeep.dataproxypoc.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.foxykeep.dataproxypoc.R;

public class MainActivity extends Activity implements OnClickListener {

    private Button mButtonListPerson;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        bindViews();
    }

    private void bindViews() {
        mButtonListPerson = (Button) findViewById(R.id.b_person_list);
        mButtonListPerson.setOnClickListener(this);
    }

    @Override
    public void onClick(final View view) {
        Intent intent = null;
        if (view == mButtonListPerson) {
            // TODO a activer une fois l'activity créée
            // intent = new Intent(this, PersonListActivity.class);
        }

        if (intent != null) {
            startActivity(intent);
        }
    }
}
