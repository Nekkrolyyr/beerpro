package ch.beerpro.data.repositories;

import android.util.Pair;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.lifecycle.LiveData;
import ch.beerpro.domain.models.Beer;
import ch.beerpro.domain.models.Entity;
import ch.beerpro.domain.models.FridgeBeer;
import ch.beerpro.domain.models.MyBeer;
import ch.beerpro.domain.models.MyBeerFromRating;
import ch.beerpro.domain.models.MyBeerFromWishlist;
import ch.beerpro.domain.models.Rating;
import ch.beerpro.domain.models.Wish;
import ch.beerpro.domain.utils.FirestoreQueryLiveData;
import ch.beerpro.domain.utils.FirestoreQueryLiveDataArray;

import static androidx.lifecycle.Transformations.map;
import static androidx.lifecycle.Transformations.switchMap;
import static ch.beerpro.domain.utils.LiveDataExtensions.combineLatest;

public class MyFridgeRepository {

    private static LiveData<List<FridgeBeer>> getFridgeByUser(String userId) {
        return new FirestoreQueryLiveDataArray<>(FirebaseFirestore.getInstance().collection(FridgeBeer.COLLECTION)
        .orderBy(FridgeBeer.FIELD_ADDED_AT, Query.Direction.DESCENDING).whereEqualTo(FridgeBeer.FIELD_USER_ID, userId),
                FridgeBeer.class);
    }

    private static LiveData<FridgeBeer> getUserFridgeFor(Pair<String, Beer> input) {
            String userId = input.first;
            Beer beer = input.second;
        DocumentReference document = FirebaseFirestore.getInstance().collection(FridgeBeer.COLLECTION)
                .document(FridgeBeer.generateId(userId, beer.getId()));
        return new FirestoreQueryLiveData<>(document, FridgeBeer.class);
    }

    public Task<Void> toggleUserFridgeItem(String userId, String itemId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String fridgeId = FridgeBeer.generateId(userId, itemId);
        DocumentReference fridgeEntryQuery = db.collection(FridgeBeer.COLLECTION).document(fridgeId);

        return fridgeEntryQuery.get().continueWithTask(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                return fridgeEntryQuery.delete();
            } else if (task.isSuccessful()) {
                return fridgeEntryQuery.set(new FridgeBeer(userId, itemId, new Date()));
            } else {
                throw task.getException();
            }
        });
    }

    public LiveData<List<Pair<FridgeBeer, Beer>>> getMyFridgeWithBeers(LiveData<String> currentUserId,
                                                                       LiveData<List<Beer>> allBeers) {
        return map(combineLatest(getMyFridge(currentUserId), map(allBeers, Entity::entitiesById)), input -> {
            List<FridgeBeer>  fridgeBeers = input.first;
            HashMap<String, Beer> beersById = input.second;

            ArrayList<Pair<FridgeBeer, Beer>> result = new ArrayList<>();
            for (FridgeBeer fridgeBeer : fridgeBeers) {
                Beer beer = beersById.get(fridgeBeer.getBeerId());
                result.add(Pair.create(fridgeBeer, beer));
            }
            return result;
        });
    }

    public LiveData<List<FridgeBeer>> getMyFridge(LiveData<String> currentUserId) {
        return switchMap(currentUserId, MyFridgeRepository::getFridgeByUser);
    }

    public LiveData<FridgeBeer> getMyFridgeForBeer(LiveData<String> currentUserId, LiveData<Beer> beer) {
        return switchMap(combineLatest(currentUserId, beer), MyFridgeRepository::getUserFridgeFor);
    }

    private static List<MyBeer> getMyBeers(Triple<List<Wish>, List<Rating>, HashMap<String, Beer>> input) {
        List<Wish> wishlist = input.getLeft();
        List<Rating> ratings = input.getMiddle();
        HashMap<String, Beer> beers = input.getRight();

        ArrayList<MyBeer> result = new ArrayList<>();
        Set<String> beersAlreadyOnTheList = new HashSet<>();
        for (Wish wish : wishlist) {
            String beerId = wish.getBeerId();
            result.add(new MyBeerFromWishlist(wish, beers.get(beerId)));
            beersAlreadyOnTheList.add(beerId);
        }

        for (Rating rating : ratings) {
            String beerId = rating.getBeerId();
            if (beersAlreadyOnTheList.contains(beerId)) {
                // if the beer is already on the wish list, don't add it again
            } else {
                result.add(new MyBeerFromRating(rating, beers.get(beerId)));
                // we also don't want to see a rated beer twice
                beersAlreadyOnTheList.add(beerId);
            }
        }
        Collections.sort(result, (r1, r2) -> r2.getDate().compareTo(r1.getDate()));
        return result;
    }


    public LiveData<List<MyBeer>> getMyFridgeBeers(LiveData<List<Beer>> allBeers, LiveData<List<Wish>> myWishlist,
                                             LiveData<List<Rating>> myRatings) {
        return map(combineLatest(myWishlist, myRatings, map(allBeers, Entity::entitiesById)),
                MyFridgeRepository::getMyBeers);
    }
}
