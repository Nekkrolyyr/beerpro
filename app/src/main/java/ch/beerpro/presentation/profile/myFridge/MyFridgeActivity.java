package ch.beerpro.presentation.profile.myFridge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import ch.beerpro.R;
import ch.beerpro.presentation.profile.mybeers.MyBeersViewModel;

import android.os.Bundle;
import android.widget.Toolbar;

public class MyFridgeActivity extends AppCompatActivity implements OnMyFridgeItemInteractionListender{

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private MyFridgeViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_fridge);

        ButterKnife.bind(this);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_myfridge));

        model = ViewModelProviders.of(this).get(MyFridgeViewModel.class);
    }




}
