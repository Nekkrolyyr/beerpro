package ch.beerpro.presentation.profile.myFridge;

import android.util.Pair;

import com.google.common.base.Strings;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ch.beerpro.data.repositories.BeersRepository;
import ch.beerpro.data.repositories.CurrentUser;
import ch.beerpro.data.repositories.MyFridgeRepository;
import ch.beerpro.data.repositories.RatingsRepository;
import ch.beerpro.data.repositories.WishlistRepository;
import ch.beerpro.domain.models.Beer;
import ch.beerpro.domain.models.FridgeBeer;
import ch.beerpro.domain.models.MyBeer;
import ch.beerpro.domain.models.Rating;
import ch.beerpro.domain.models.Wish;

import static androidx.lifecycle.Transformations.map;

public class MyFridgeViewModel extends ViewModel implements CurrentUser {
    
    private static final String TAG = "MyFridgeViewModel";

    private final WishlistRepository wishlistRepository;
    LiveData<List<FridgeBeer>> fridgeBeers;
    BeersRepository beersRepository;

    public MyFridgeViewModel() {

        wishlistRepository = new WishlistRepository();
        beersRepository = new BeersRepository();
        MyFridgeRepository myFridgeRepository = new MyFridgeRepository();

        MutableLiveData<String> userId = new MutableLiveData<>();
        userId.postValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
        fridgeBeers = myFridgeRepository.getMyFridge(userId);

    }

    public LiveData<List<Pair<FridgeBeer,Beer>>> getFilteredBeers(List<FridgeBeer> beerIds) {
        return beersRepository.getBeersbyIds(beerIds);
    }

    public LiveData<List<FridgeBeer>> getMyFridgeBeers() {
        return fridgeBeers;
    }
    public void toggleItemInWishlist(String beerId) {
        wishlistRepository.toggleUserWishlistItem(getCurrentUser().getUid(), beerId);
    }

}
