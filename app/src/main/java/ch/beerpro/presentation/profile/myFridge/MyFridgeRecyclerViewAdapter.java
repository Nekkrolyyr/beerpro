package ch.beerpro.presentation.profile.myFridge;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import butterknife.ButterKnife;
import ch.beerpro.GlideApp;
import ch.beerpro.R;
import ch.beerpro.domain.models.Beer;
import ch.beerpro.domain.models.FridgeBeer;
import ch.beerpro.domain.models.MyBeer;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import ch.beerpro.domain.models.MyBeerFromRating;
import ch.beerpro.domain.models.MyBeerFromWishlist;
import ch.beerpro.presentation.utils.DrawableHelpers;

public class MyFridgeRecyclerViewAdapter extends ListAdapter<Pair<FridgeBeer,Beer>, MyFridgeRecyclerViewAdapter.ViewHolder> {

    private final OnMyFridgeItemInteractionListener listener;
    private FirebaseUser user;

    private static final String TAG = "MyFridgeRecyclerViewAdap";

    private static final DiffUtil.ItemCallback<Pair<FridgeBeer,Beer>> DIFF_CALLBACK = new DiffUtil.ItemCallback<Pair<FridgeBeer,Beer>>() {
        @Override
        public boolean areItemsTheSame(@NonNull Pair<FridgeBeer,Beer> oldItem, @NonNull Pair<FridgeBeer,Beer> newItem) {
            return oldItem.first.getId().equals(newItem.first.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Pair<FridgeBeer,Beer> oldItem, @NonNull Pair<FridgeBeer,Beer> newItem) {
            return oldItem.equals(newItem);
        }
    };

    protected MyFridgeRecyclerViewAdapter(OnMyFridgeItemInteractionListener listener, FirebaseUser user) {
        super(DIFF_CALLBACK);
        this.listener = listener;
        this.user = user;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.activity_my_beers_listentry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pair<FridgeBeer,Beer> entry = getItem(position);
        holder.bind(entry.first,entry.second, listener);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name)
        TextView name;

        @BindView(R.id.manufacturer)
        TextView manufacturer;

        @BindView(R.id.category)
        TextView category;

        @BindView(R.id.photo)
        ImageView photo;

        @BindView(R.id.ratingBar)
        RatingBar ratingBar;

        @BindView(R.id.numRatings)
        TextView numRatings;

        @BindView(R.id.addedAt)
        TextView addedAt;

        @BindView(R.id.onTheListSince)
        TextView onTheListSince;

        @BindView(R.id.removeFromWishlist)
        Button removeFromWishlist;


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }

        public void bind(FridgeBeer fb,Beer entry, OnMyFridgeItemInteractionListener listener) {

            Beer item = entry;

            name.setText(item.getName());
            manufacturer.setText(item.getManufacturer());
            category.setText(item.getCategory());
            GlideApp.with(itemView).load(item.getPhoto()).apply(new RequestOptions().override(240, 240).centerInside())
                    .into(photo);
            ratingBar.setNumStars(5);
            ratingBar.setRating(item.getAvgRating());
            numRatings.setText(itemView.getResources().getString(R.string.fmt_num_ratings, item.getNumRatings()));
            itemView.setOnClickListener(v -> listener.onMoreClickedListener(photo, item));
            removeFromWishlist.setVisibility(View.GONE);

            String formattedDate =
                    DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT).format(fb.getAddedAt());
            addedAt.setText(formattedDate);

            onTheListSince.setText("im Kühlschrank seit");

        }
    }
}
