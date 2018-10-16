package ch.beerpro.presentation.details;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import ch.beerpro.GlideApp;
import ch.beerpro.R;
import ch.beerpro.domain.models.Notice;
import ch.beerpro.domain.models.Rating;
import ch.beerpro.presentation.utils.EntityDiffItemCallback;


public class NoticeRecyclerViewAdapter extends ListAdapter<Notice, NoticeRecyclerViewAdapter.ViewHolder> {

    private static final EntityDiffItemCallback<Notice> DIFF_CALLBACK = new EntityDiffItemCallback<>();

    private FirebaseUser user;

    public NoticeRecyclerViewAdapter(FirebaseUser user) {
        super(DIFF_CALLBACK);
        this.user = user;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.activity_details_notice_listentry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.comment)
        TextView comment;

        @BindView(R.id.avatar)
        ImageView avatar;

        @BindView(R.id.authorName)
        TextView authorName;

        @BindView(R.id.date)
        TextView date;


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }

        void bind(Notice item) {
            comment.setText(item.getComment());

            String formattedDate =
                    DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT).format(item.getCreationDate());
            date.setText(formattedDate);

            authorName.setText(item.getUserName());
            GlideApp.with(itemView).load(item.getUserPhoto()).apply(new RequestOptions().circleCrop()).into(avatar);

        }
    }
}
