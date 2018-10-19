package ch.beerpro.presentation.profile.myFridge;

import android.util.Pair;

import com.google.common.base.Strings;

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
    private final MutableLiveData<String> searchTerm = new MutableLiveData<>();

    private final WishlistRepository wishlistRepository;
    //private final LiveData<List<Beer>> myFilteredBeers;
    LiveData<List<FridgeBeer>> fridgeBeers;
    LiveData<List<Beer>> allBeers;

    public MyFridgeViewModel() {

        wishlistRepository = new WishlistRepository();
        BeersRepository beersRepository = new BeersRepository();
        MyFridgeRepository myFridgeRepository = new MyFridgeRepository();

        allBeers = beersRepository.getAllBeers();
        MutableLiveData<String> currentUserId = new MutableLiveData<>();
        fridgeBeers = myFridgeRepository.getMyFridge(currentUserId);
        
        /*
        myFilteredBeers = map(allBeers,listBeers ->{
            LiveData<List<Beer>> outputBeers = map(fridgeBeers,listFridge->{
                List<Beer> filteredBeers = new ArrayList<>();
                    for(FridgeBeer fb : listFridge){
                        for(Beer b : listBeers){
                            if(fb.getBeerId().equals(b.getId())){
                                filteredBeers.add(b);
                                break;
                            }
                        }
                    }
                return filteredBeers;
            });

            return outputBeers.getValue();
        });
*/
    }

    /*public LiveData<List<Beer>> getMyFilteredBeers() {
        return myFilteredBeers;
    }*/
    public LiveData<List<Beer>> getMyBeers() {
        return allBeers;
    }
    public LiveData<List<FridgeBeer>> getMyFridgeBeers() {
        return fridgeBeers;
    }
    public void toggleItemInWishlist(String beerId) {
        wishlistRepository.toggleUserWishlistItem(getCurrentUser().getUid(), beerId);
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm.setValue(searchTerm);
    }
}
