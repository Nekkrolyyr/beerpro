package ch.beerpro.data.repositories;

import android.util.Log;
import android.util.Pair;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import ch.beerpro.domain.models.Beer;
import ch.beerpro.domain.models.FridgeBeer;
import ch.beerpro.domain.utils.FirestoreQueryLiveData;
import ch.beerpro.domain.utils.FirestoreQueryLiveDataArray;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static androidx.lifecycle.Transformations.map;
import static androidx.lifecycle.Transformations.switchMap;

public class BeersRepository {

    private final static Function<List<Beer>, List<String>> mapBeersToCategories = (List<Beer> beers) -> {
        Set<String> filtered = new HashSet<>();
        for (Beer beer : beers) {
            filtered.add(beer.getCategory());
        }
        String[] strings = filtered.toArray(new String[0]);
        return Arrays.asList(strings).subList(0, Math.min(8, strings.length));
    };


    private final static Function<List<Beer>, List<String>> mapBeersToManufacturers = (List<Beer> beers) -> {
        Set<String> filtered = new HashSet<>();
        for (Beer beer : beers) {
            if(beer.getManufacturer()!=null) {
                filtered.add(beer.getManufacturer());
            }else{
                Log.i("BEER Info",beer.toString());
            }
        }
        String[] strings = filtered.toArray(new String[0]);
        Arrays.sort(strings);
        return Arrays.asList(strings);
    };

    private final FirestoreQueryLiveDataArray<Beer> allBeers =
            new FirestoreQueryLiveDataArray<>(FirebaseFirestore.getInstance().collection(Beer.COLLECTION), Beer.class);

    private static LiveData<Beer> getBeerById(String beerId) {
        return new FirestoreQueryLiveData<>(
                FirebaseFirestore.getInstance().collection(Beer.COLLECTION).document(beerId), Beer.class);
    }

    public LiveData<List<Beer>> getAllBeers() {
        return allBeers;
    }

    public LiveData<Beer> getBeer(LiveData<String> beerId) {
        return switchMap(beerId, BeersRepository::getBeerById);
    }

    public LiveData<List<String>> getBeerCategories() {
        return map(allBeers, mapBeersToCategories);
    }

    public LiveData<List<String>> getBeerManufacturers() {
        return map(allBeers, mapBeersToManufacturers);
    }

    public LiveData<List<Pair<FridgeBeer,Beer>>> getBeersbyIds(List<FridgeBeer> beerIds) {
        return map(allBeers, beerList -> {
            ArrayList<Pair<FridgeBeer,Beer>> filteredBeers = new ArrayList<>();
            for(FridgeBeer fb : beerIds){
                for(Beer b: beerList){
                    if(fb.getBeerId().equals(b.getId())){
                        Pair<FridgeBeer,Beer> item = new Pair<>(fb,b);
                        filteredBeers.add(item);
                        break;
                    }
                }
            }
            return filteredBeers;
        });
    }
}
