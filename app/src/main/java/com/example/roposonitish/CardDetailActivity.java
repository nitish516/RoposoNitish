package com.example.roposonitish;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.roposonitish.data.Story;
import com.example.roposonitish.data.User;
import com.squareup.picasso.Picasso;

import java.util.Set;

import static com.example.roposonitish.CardDetailFragment.*;

/**
 * An activity representing a single Card detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link CardListActivity}.
 */
public class CardDetailActivity extends AppCompatActivity {

    int mPos = 0;
    Story mStory;
    User mUser;
    ImageView mFollowImage;
    Set<String> mSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPos = getIntent().getIntExtra(ARG_ITEM_ID, 0);
        mStory = ((RoposoApplication)getApplication()).storyList.get(mPos);
        mUser = ((RoposoApplication)getApplication()).userList.get(mStory.getDb());

        setContentView(R.layout.activity_card_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageView backdrop = (ImageView) findViewById(R.id.backdrop);
        Picasso.with(this).load(mStory.getSi()).into(backdrop);
        Picasso.with(this).load(mUser.getImage()).into((ImageView) findViewById(R.id.roundImage));
        ((TextView)findViewById(R.id.userTitle)).setText(mUser.getUsername());
        mSet = ((RoposoApplication) getApplication()).followSet;
        mFollowImage = (ImageView)findViewById(R.id.followImage);
        if (mSet.contains(mUser.getId())) {
            mFollowImage.setImageResource(R.drawable.following);
        }
        mFollowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<String> set = ((RoposoApplication)getApplication()).followSet;
                if (set.contains(mUser.getId())) {
                    ((RoposoApplication) getApplication()).followSet.remove(mUser.getId());
                    mFollowImage.setImageResource(R.drawable.follow_w);
                } else {
                    ((RoposoApplication) getApplication()).followSet.add(mUser.getId());
                    mFollowImage.setImageResource(R.drawable.following);
                }
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putInt(ARG_ITEM_ID,mPos);
            CardDetailFragment fragment = new CardDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.card_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, CardListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
