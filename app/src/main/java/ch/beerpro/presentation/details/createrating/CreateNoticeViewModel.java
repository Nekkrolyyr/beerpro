package ch.beerpro.presentation.details.createrating;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Collections;
import java.util.Date;

import androidx.lifecycle.ViewModel;
import ch.beerpro.domain.models.Beer;
import ch.beerpro.domain.models.Notice;
import ch.beerpro.domain.models.Rating;
import ch.beerpro.presentation.utils.EntityClassSnapshotParser;

public class CreateNoticeViewModel extends ViewModel {

    private static final String TAG = "CreateRatingViewModel";

    private EntityClassSnapshotParser<Notice> parser = new EntityClassSnapshotParser<>(Notice.class);
    private Beer item;

    public Beer getItem() {
        return item;
    }

    public void setItem(Beer item) {
        this.item = item;
    }


    public Task<Notice> saveNotice(Beer item, String comment) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;

        Notice newNotice = new Notice(null, item.getId(), item.getName(), user.getUid(), user.getDisplayName(),user.getPhotoUrl().toString(),
                comment, new Date());
        Log.i(TAG, "Adding new rating: " + newNotice.toString());

        Task<Notice> noticeTask = FirebaseFirestore.getInstance().collection(Notice.COLLECTION).add(newNotice).continueWithTask(task -> {
            if (task.isSuccessful()) {
                return task.getResult().get();
            } else {
                throw task.getException();
            }
        }).continueWithTask(task -> {

            if (task.isSuccessful()) {
                return Tasks.forResult(parser.parseSnapshot(task.getResult()));
            } else {
                throw task.getException();
            }
        });

        return noticeTask;

    }
}