package ch.beerpro.presentation.profile.myFridge;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import ch.beerpro.R;
import ch.beerpro.domain.models.Beer;
import ch.beerpro.presentation.details.DetailsActivity;

public class MyFridgeActivity extends AppCompatActivity implements OnMyFridgeItemInteractionListener{


    /*
    ToDo:
        - Bier hinzufügen -> DetailsActivity sollte ein hinzufügen enthalten
        - Repository von myFridge anpassen
        - Suche hängt app auf
     */

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private MyFridgeViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_fridge);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_myfridge));

        model = ViewModelProviders.of(this).get(MyFridgeViewModel.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_fridge, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                model.setSearchTerm(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                model.setSearchTerm(null);
                return false;
            }
        });

        searchView.setOnCloseListener(() -> {
            model.setSearchTerm(null);
            return false;
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMoreClickedListener(ImageView photo, Beer item) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.ITEM_ID, item.getId());
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, photo, "image");
        startActivity(intent, options.toBundle());
    }

    @Override
    public void onWishClickedListener(Beer item) {
        model.toggleItemInWishlist(item.getId());
    }
}
