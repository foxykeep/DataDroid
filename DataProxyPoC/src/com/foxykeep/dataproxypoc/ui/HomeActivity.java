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
    private Button mButtonCityList;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        bindViews();
    }

    private void bindViews() {
        mButtonPersonList = (Button) findViewById(R.id.b_person_list);
        mButtonPersonList.setOnClickListener(this);

        mButtonCityList = (Button) findViewById(R.id.b_city_list);
        mButtonCityList.setOnClickListener(this);
    }

    @Override
    public void onClick(final View view) {
        Intent intent = null;
        if (view == mButtonPersonList) {
            intent = new Intent(this, PersonListActivity.class);
        } else if (view == mButtonCityList) {
            intent = new Intent(this, CityListActivity.class);
        }

        if (intent != null) {
            startActivity(intent);
        }
    }
}
