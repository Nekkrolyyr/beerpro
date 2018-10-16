package ch.beerpro.presentation.profile.myFridge;

import android.widget.ImageView;

import ch.beerpro.domain.models.Beer;

interface OnMyFridgeItemInteractionListener {

    void onMoreClickedListener(ImageView photo, Beer item);

    void onWishClickedListener(Beer item);
}
