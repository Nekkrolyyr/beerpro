package ch.beerpro.presentation;

import android.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ch.beerpro.data.repositories.*;
import ch.beerpro.domain.models.Beer;
import ch.beerpro.domain.models.FridgeBeer;
import ch.beerpro.domain.models.MyBeer;
import ch.beerpro.domain.models.Notice;
import ch.beerpro.domain.models.Rating;
import ch.beerpro.domain.models.Wish;
import com.google.android.gms.tasks.Task;
import static androidx.lifecycle.Transformations.map;


import java.util.ArrayList;
import java.util.List;

/**
 * This is the viewmodel for the {@link MainActivity}, which is also used by the three pages/fragments contained in it.
 */
public class MainViewModel extends ViewModel implements CurrentUser {

    private static final String TAG = "MainViewModel";

    private final BeersRepository beersRepository;
    private final LikesRepository likesRepository;
    private final RatingsRepository ratingsRepository;
    private final WishlistRepository wishlistRepository;
    private final MyFridgeRepository fridgeRepository;

    private final LiveData<List<Wish>> myWishlist;
    private final LiveData<List<Rating>> myRatings;
    private final LiveData<List<Notice>> myNotices;
    private final LiveData<List<MyBeer>> myBeers;
    private final LiveData<List<FridgeBeer>> myFridge;

    public MainViewModel() {
        /*
         * TODO We should really be injecting these!
         */
        beersRepository = new BeersRepository();
        likesRepository = new LikesRepository();
        wishlistRepository = new WishlistRepository();
        ratingsRepository = new RatingsRepository();
        fridgeRepository = new MyFridgeRepository();
        MyBeersRepository myBeersRepository = new MyBeersRepository();

        LiveData<List<Beer>> allBeers = beersRepository.getAllBeers();

        MutableLiveData<String> currentUserId = new MutableLiveData<>();
        myWishlist = wishlistRepository.getMyWishlist(currentUserId);
        myRatings = ratingsRepository.getMyRatings(currentUserId);
        myNotices = ratingsRepository.getMyNotices(currentUserId);
        myFridge = fridgeRepository.getMyFridge(currentUserId);

        LiveData<List<Rating>> newRatings = map(myNotices, myNotices -> {
            List<Rating> ratings = new ArrayList<>();
            for(Notice n : myNotices){
                Rating r = new Rating();
                r.setBeerId(n.getBeerId());
                r.setBeerName(n.getBeerName());
                r.setCreationDate(n.getCreationDate());
                r.setUserId(n.getUserId());
                ratings.add(r);
            }
            return ratings;
        });

        MediatorLiveData mergedRatings = new MediatorLiveData();
        mergedRatings.addSource(myRatings , value -> {mergedRatings.setValue(value);});
        mergedRatings.addSource(newRatings , value -> {mergedRatings.setValue(value);});

        myBeers = myBeersRepository.getMyBeers(allBeers, myWishlist, mergedRatings);

        /*
         * Set the current user id, which is used as input for the getMyWishlist and getMyRatings calls above.
         * Settings the id does not yet cause any computation or data fetching, only when an observer is subscribed
         * to the LiveData will the data be fetched and computations depending on it will be executed. LiveData works
         * similar to Java 8 streams or Rx observables in that regard, but have a less rich API for combining such
         * streams of data.
         * */
        currentUserId.setValue(getCurrentUser().getUid());
    }

    public LiveData<List<MyBeer>> getMyBeers() {
        return myBeers;
    }

    public LiveData<List<Rating>> getMyRatings() {
        return myRatings;
    }

    public LiveData<List<Wish>> getMyWishlist() {
        return myWishlist;
    }

    public LiveData<List<String>> getBeerCategories() {
        return beersRepository.getBeerCategories();
    }

    public LiveData<List<String>> getBeerManufacturers() {
        return beersRepository.getBeerManufacturers();
    }

    public void toggleLike(Rating rating) {
        likesRepository.toggleLike(rating);
    }

    public Task<Void> toggleItemInWishlist(String itemId) {
        return wishlistRepository.toggleUserWishlistItem(getCurrentUser().getUid(), itemId);
    }

    public LiveData<List<Pair<Rating, Wish>>> getAllRatingsWithWishes() {
        return ratingsRepository.getAllRatingsWithWishes(myWishlist);
    }

    public LiveData<List<FridgeBeer>> getMyFridge() {
        return myFridge;
    }
}