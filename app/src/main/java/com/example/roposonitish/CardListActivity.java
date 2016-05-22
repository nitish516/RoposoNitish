package com.example.roposonitish;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.roposonitish.data.Story;
import com.example.roposonitish.data.User;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An activity representing a list of Cards. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link CardDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class CardListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    static final int USER_COUNT = 2;
    private int mPosition;
//    SharedPreferences preferences;
    SimpleItemRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        View recyclerView = findViewById(R.id.card_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.card_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
//        preferences = getSharedPreferences("text", 0);
//        String value = preferences.getString(
//                getString(R.string.preference_key), null);
//        if (value == null) {
//            // the key does not exist
//        } else {
//            ((RoposoApplication)getApplication()).followSet = (Set<String>) Arrays.asList(value);
//        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        parseJson();
        mAdapter = new SimpleItemRecyclerViewAdapter(this,((RoposoApplication)getApplication()).storyList);
        recyclerView.setAdapter(mAdapter);
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Story> mValues;
        Context mContext;

        public SimpleItemRecyclerViewAdapter(Context context, List<Story> items) {
            mContext = context;
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
//            holder.mItem = mValues.get(position);
//            holder.mIdView.setText(Integer.toString(mValues.get(position).getLikes_count()));
            final Story story = mValues.get(position);
            holder.placeName.setText(story.getTitle());
//            holder.placeImage.setImageResource(R.drawable.roposo);
            Picasso.with(mContext).load(story.getSi()).into(holder.placeImage);
            String db = story.getDb();
            final User user  = ((RoposoApplication)getApplication()).userList.get(db);
            if(user != null){
//                Picasso.with(mContext)
//                        .load (user.getImage())
//                        .transform(new PaletteGeneratorTransformation(1))
//                        .into (holder.roundImage, new PaletteGeneratorTransformation.Callback (holder.roundImage) {
//                            @Override public void onPalette (final Palette palette) {
//                                holder.uholder.roundImageserTitle.setTextColor(palette.getVibrantColor(1));
//                            }
//                        });
                Picasso.with(mContext).load(user.getImage()).into(holder.roundImage);
                holder.userTitle.setText(user.getUsername());
            }

            Set<String> set = ((RoposoApplication)getApplication()).followSet;
            if (set.contains(user.getId())) {
                holder.followImage.setImageResource(R.drawable.following);
            }
            else{
                holder.followImage.setImageResource(R.drawable.follow);
            }



            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        mPosition = position;
                        Bundle arguments = new Bundle();
                        arguments.putString(CardDetailFragment.ARG_ITEM_ID, String.valueOf(story.getId()));
                        CardDetailFragment fragment = new CardDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.card_detail_container, fragment)
                                .commit();
                    } else {
                        mPosition = position;
                        Intent intent = new Intent(mContext, CardDetailActivity.class);
                        intent.putExtra(CardDetailFragment.ARG_ITEM_ID, story.getId());

                        mContext.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
//            public final TextView mIdView;
            public final TextView placeName;
//            public final LinearLayout placeHolder;
            public final LinearLayout placeNameHolder;
            public final ImageView placeImage;
            public Story mItem;
            public final RoundedImageView roundImage;
            public final TextView userTitle;
            public final ImageView followImage;

            public ViewHolder(View view) {
                super(view);
                mView = view;

//                placeHolder = (LinearLayout) view.findViewById(R.id.mainHolder);
                placeName = (TextView) view.findViewById(R.id.placeName);
                placeNameHolder = (LinearLayout) view.findViewById(R.id.placeNameHolder);
                placeImage = (ImageView) view.findViewById(R.id.placeImage);
                roundImage = (RoundedImageView) view.findViewById(R.id.roundImage);
                userTitle = (TextView) view.findViewById(R.id.userTitle);
                followImage = (ImageView) view.findViewById(R.id.followImage);

//                mIdView = (TextView) view.findViewById(R.id.id);
//                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + placeName.getText() + "'";
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    public void parseJson(){ //JSONObject jsonObject
        try {
            String strJson = "[  \n" +
                    "   {  \n" +
                    "      \"about\":\"Mother, actor, entrepreneur, fitness enthusiast and an eternal positive thinker\",\n" +
                    "      \"id\":\"238bb4ca-606d-4817-afad-78bee2898264\",\n" +
                    "      \"username\":\"Shilpa shetty kundra \",\n" +
                    "      \"followers\":35215,\n" +
                    "      \"following\":5,\n" +
                    "      \"image\":\"http://img.ropose.com/userImages/13632253306661657730401415143533525274225757_circle.png\",\n" +
                    "      \"url\":\"http://www.roposo.com/profile/shilpa-shetty-kundra-/238bb4ca-606d-4817-afad-78bee2898264\",\n" +
                    "      \"handle\":\"@shilpashettykundra\",\n" +
                    "      \"is_following\":false,\n" +
                    "      \"createdOn\":1439530320545\n" +
                    "   },\n" +
                    "   {  \n" +
                    "      \"about\":\"Www.Nargisfakhri.com Instagram- @NargisFakhri Twitter- @NargisFakhri FaceBook- Nargis Fakhri \",\n" +
                    "      \"id\":\"cda99c24-955a-4c58-a6a8-c811938df530\",\n" +
                    "      \"username\":\"Nargis Fakhri\",\n" +
                    "      \"followers\":83878,\n" +
                    "      \"following\":50,\n" +
                    "      \"image\":\"http://img5.ropose.com/userImages/16557067377322653404501466127117042162245336_circle_100x100.png\",\n" +
                    "      \"url\":\"http://www.roposo.com/profile/nargis-fakhri/cda99c24-955a-4c58-a6a8-c811938df530\",\n" +
                    "      \"handle\":\"@nargisfakhri\",\n" +
                    "      \"is_following\":false,\n" +
                    "      \"createdOn\":1446559001561\n" +
                    "   },\n" +
                    "   {  \n" +
                    "      \"description\":\"Celebrating Black Friday in this lovely black cut out romper and floral accessories. I love black and i think it is the easiest thing to wear when i am in doubt. So a black romper solves my dilemma of what to wear when i am short of time to decide and outfit. When you wear black add some fun accessories to keep the outfit fun and lively.\",\n" +
                    "      \"id\":\"fa6d9bdf-eae3-4d6c-a668-9be94cfaf980\",\n" +
                    "      \"verb\":\"created this story on 25 October\",\n" +
                    "      \"db\":\"238bb4ca-606d-4817-afad-78bee2898264\",\n" +
                    "      \"url\":\"http://www.roposo.com/story/throwback-/fa6d9bdf-eae3-4d6c-a668-9be94cfaf980\",\n" +
                    "      \"si\":\"http://img0.ropose.com/story/1445748867804_800_238bb4ca-606d-4817-afad-78bee2898264.jpeg\",\n" +
                    "      \"type\":\"story\",\n" +
                    "      \"title\":\"Throwback \",\n" +
                    "      \"like_flag\":false,\n" +
                    "      \"likes_count\":1099,\n" +
                    "      \"comment_count\":10\n" +
                    "   },\n" +
                    "   {  \n" +
                    "      \"description\":\"Today's outfit! #ootd Sheer shirts are always so sexy. Team it up with a bustier inside and it looks elegant and sexy at the same time. I wore my purple sheer shirt with an embellished maxi skirt and statement necklace. Sheer shirts looks fabulous with statement necklaces or layered chains.\",\n" +
                    "      \"id\":\"066f2bac-1fa8-44f8-b2b6-780df3324c71\",\n" +
                    "      \"verb\":\"created this story on 21 October\",\n" +
                    "      \"db\":\"238bb4ca-606d-4817-afad-78bee2898264\",\n" +
                    "      \"url\":\"http://www.roposo.com/story/red-riding-hood-/066f2bac-1fa8-44f8-b2b6-780df3324c71\",\n" +
                    "      \"si\":\"http://img0.ropose.com/story/1445413834463_443_238bb4ca-606d-4817-afad-78bee2898264.jpeg\",\n" +
                    "      \"type\":\"story\",\n" +
                    "      \"title\":\"Red riding hood \",\n" +
                    "      \"like_flag\":false,\n" +
                    "      \"likes_count\":896,\n" +
                    "      \"comment_count\":19\n" +
                    "   },\n" +
                    "   {  \n" +
                    "      \"description\":\"\",\n" +
                    "      \"id\":\"1a799ff8-89c8-41e6-a73e-0be387b40496\",\n" +
                    "      \"verb\":\"created on 21 January\",\n" +
                    "      \"db\":\"cda99c24-955a-4c58-a6a8-c811938df530\",\n" +
                    "      \"url\":\"http://www.roposo.com/story/love-this-outfit-by-anita-dongre/1a799ff8-89c8-41e6-a73e-0be387b40496\",\n" +
                    "      \"si\":\"http://img0.ropose.com/story/1453385755839_945_cda99c24-955a-4c58-a6a8-c811938df530.jpeg\",\n" +
                    "      \"type\":\"story\",\n" +
                    "      \"title\":\"Love this outfit by Anita dongre\",\n" +
                    "      \"like_flag\":false,\n" +
                    "      \"likes_count\":234,\n" +
                    "      \"comment_count\":5\n" +
                    "   },\n" +
                    "   {  \n" +
                    "      \"description\":\"DVF dress , Patric Kaiser bag ands Reiss shoes\",\n" +
                    "      \"id\":\"37d334b0-c35a-4d6a-96bc-bacaae6e3c79\",\n" +
                    "      \"verb\":\"created this story 1 day ago\",\n" +
                    "      \"db\":\"238bb4ca-606d-4817-afad-78bee2898264\",\n" +
                    "      \"url\":\"http://www.roposo.com/story/black-/37d334b0-c35a-4d6a-96bc-bacaae6e3c79\",\n" +
                    "      \"si\":\"http://img0.ropose.com/story/1449202499818_285_238bb4ca-606d-4817-afad-78bee2898264.jpeg\",\n" +
                    "      \"type\":\"story\",\n" +
                    "      \"title\":\"So roposo \",\n" +
                    "      \"like_flag\":false,\n" +
                    "      \"likes_count\":2280,\n" +
                    "      \"comment_count\":38\n" +
                    "   },\n" +
                    "   {  \n" +
                    "      \"description\":\"\",\n" +
                    "      \"id\":\"74598462-abcc-4170-92ea-901b32dcf433\",\n" +
                    "      \"verb\":\"updated this story on 7 November\",\n" +
                    "      \"db\":\"238bb4ca-606d-4817-afad-78bee2898264\",\n" +
                    "      \"url\":\"http://www.roposo.com/story/so-roposo-/74598462-abcc-4170-92ea-901b32dcf433\",\n" +
                    "      \"si\":\"http://img0.ropose.com/story/1446695866824_645_238bb4ca-606d-4817-afad-78bee2898264.jpeg\",\n" +
                    "      \"type\":\"story\",\n" +
                    "      \"title\":\"Happy Karvachauth !! \",\n" +
                    "      \"like_flag\":false,\n" +
                    "      \"likes_count\":858,\n" +
                    "      \"comment_count\":9\n" +
                    "   },\n" +
                    "   {  \n" +
                    "      \"description\":\"The Karvachauth look :-) \",\n" +
                    "      \"id\":\"386b11c2-24bb-40ec-958f-788b2e743db3\",\n" +
                    "      \"verb\":\"created this story on 30 October\",\n" +
                    "      \"db\":\"238bb4ca-606d-4817-afad-78bee2898264\",\n" +
                    "      \"url\":\"http://www.roposo.com/story/happy-karvachauth-/386b11c2-24bb-40ec-958f-788b2e743db3\",\n" +
                    "      \"si\":\"http://img0.ropose.com/story/1446203590488_320_238bb4ca-606d-4817-afad-78bee2898264.jpeg\",\n" +
                    "      \"type\":\"story\",\n" +
                    "      \"title\":\"Happy Karvachauth !! \",\n" +
                    "      \"like_flag\":false,\n" +
                    "      \"likes_count\":1322,\n" +
                    "      \"comment_count\":58\n" +
                    "   },\n" +
                    "   {  \n" +
                    "      \"description\":\"At Amitji's Diwali party wearing a lovely #manishmalhotra and jewellery by #anmol \",\n" +
                    "      \"id\":\"ae30f007-9922-47c0-8fab-934b61d8cc96\",\n" +
                    "      \"verb\":\"created this story on 14 November\",\n" +
                    "      \"db\":\"238bb4ca-606d-4817-afad-78bee2898264\",\n" +
                    "      \"url\":\"http://www.roposo.com/story/diwali-look-/ae30f007-9922-47c0-8fab-934b61d8cc96\",\n" +
                    "      \"si\":\"http://img0.ropose.com/story/1447479080598_229_238bb4ca-606d-4817-afad-78bee2898264.jpeg\",\n" +
                    "      \"type\":\"story\",\n" +
                    "      \"title\":\"Diwali Look! :D\",\n" +
                    "      \"like_flag\":false,\n" +
                    "      \"likes_count\":3267,\n" +
                    "      \"comment_count\":49\n" +
                    "   },\n" +
                    "   {  \n" +
                    "      \"description\":\"\",\n" +
                    "      \"id\":\"c6f4d22c-fec8-4161-8c89-80c3e7956aaf\",\n" +
                    "      \"verb\":\"created this story on 14 November\",\n" +
                    "      \"db\":\"238bb4ca-606d-4817-afad-78bee2898264\",\n" +
                    "      \"url\":\"http://www.roposo.com/story/at-jagdamba-saree-25yrs-celebration-in-surat-/c6f4d22c-fec8-4161-8c89-80c3e7956aaf\",\n" +
                    "      \"si\":\"http://img0.ropose.com/story/1446441786706_684_238bb4ca-606d-4817-afad-78bee2898264.jpeg\",\n" +
                    "      \"type\":\"story\",\n" +
                    "      \"title\":\"At Jagdamba saree 25yrs celebration in surat \",\n" +
                    "      \"like_flag\":false,\n" +
                    "      \"likes_count\":1404,\n" +
                    "      \"comment_count\":33\n" +
                    "   },\n" +
                    "   {  \n" +
                    "      \"description\":\"Wearing a stunning #zulekhashariff styled by #sanjanabatra \",\n" +
                    "      \"id\":\"1bc4d79a-1dc9-46fb-ba1c-4f88a25c55e2\",\n" +
                    "      \"verb\":\"created this story on 1 November\",\n" +
                    "      \"db\":\"238bb4ca-606d-4817-afad-78bee2898264\",\n" +
                    "      \"url\":\"http://www.roposo.com/story/at-the-sony-event-last-night-/1bc4d79a-1dc9-46fb-ba1c-4f88a25c55e2\",\n" +
                    "      \"si\":\"http://img0.ropose.com/story/1446355020803_843_238bb4ca-606d-4817-afad-78bee2898264.jpeg\",\n" +
                    "      \"type\":\"story\",\n" +
                    "      \"title\":\"At the sony event last night \",\n" +
                    "      \"like_flag\":false,\n" +
                    "      \"likes_count\":958,\n" +
                    "      \"comment_count\":22\n" +
                    "   },\n" +
                    "   {  \n" +
                    "      \"description\":\"\",\n" +
                    "      \"id\":\"75b460ed-e3a5-4282-bc60-d1537110fb7d\",\n" +
                    "      \"verb\":\"created on 20 January\",\n" +
                    "      \"db\":\"cda99c24-955a-4c58-a6a8-c811938df530\",\n" +
                    "      \"url\":\"http://www.roposo.com/story/love-my-new-nail-color-purple-magic/75b460ed-e3a5-4282-bc60-d1537110fb7d\",\n" +
                    "      \"si\":\"http://img0.ropose.com/story/1453385755839_945_cda99c24-955a-4c58-a6a8-c811938df530.jpeg\",\n" +
                    "      \"type\":\"story\",\n" +
                    "      \"title\":\"Love my new nail color! Purple magic\",\n" +
                    "      \"like_flag\":false,\n" +
                    "      \"likes_count\":234,\n" +
                    "      \"comment_count\":5\n" +
                    "   },\n" +
                    "   {  \n" +
                    "      \"description\":\"Customized jacket by #masabagupta and do not miss the SSK bracelet :-) \",\n" +
                    "      \"id\":\"1258dbda-b7d4-473b-9e4c-dc6b421ddd1b\",\n" +
                    "      \"verb\":\"created this story on 1 November\",\n" +
                    "      \"db\":\"238bb4ca-606d-4817-afad-78bee2898264\",\n" +
                    "      \"url\":\"http://www.roposo.com/story/ethnic-look/1258dbda-b7d4-473b-9e4c-dc6b421ddd1b\",\n" +
                    "      \"si\":\"http://img0.ropose.com/story/1446090876919_374_238bb4ca-606d-4817-afad-78bee2898264.jpeg\",\n" +
                    "      \"type\":\"story\",\n" +
                    "      \"title\":\"Ethnic look\",\n" +
                    "      \"like_flag\":false,\n" +
                    "      \"likes_count\":1266,\n" +
                    "      \"comment_count\":18\n" +
                    "   }\n" +
                    "]\n" +
                    "\n";
            JSONArray  jsonRootArray = new JSONArray(strJson);


            //Iterate the jsonArray and print the info of JSONObjects
            for(int i=0; i < jsonRootArray.length(); i++) {
                JSONObject tmpJsonObject = jsonRootArray.getJSONObject(i);
                if (i < USER_COUNT) {
                    parseUser(tmpJsonObject);
                } else {
                    parseStory(tmpJsonObject);
                }
            }
        } catch (JSONException e) {e.printStackTrace();}
    }

    private void parseStory(JSONObject jsonObject) throws JSONException {
        Story story = new Story();
        story.setDescription(jsonObject.optString("description").toString());
        story.setId(jsonObject.optString("id").toString());
        story.setVerb(jsonObject.optString("verb").toString());
        story.setDb(jsonObject.optString("db").toString());
        story.setUrl(jsonObject.optString("url").toString());
        story.setSi(jsonObject.optString("si").toString());
        story.setType(jsonObject.optString("type").toString());
        story.setTitle(jsonObject.optString("title").toString());
        story.setLike_flag(Boolean.getBoolean(jsonObject.getString("like_flag").toString()));
        String intStr = jsonObject.optString("likes_count").toString();
        story.setLikes_count(Integer.parseInt(intStr));
        story.setComments_count(Integer.parseInt(jsonObject.optString("comment_count").toString()));
        ((RoposoApplication)getApplication()).storyList.add(story);
    }

    private void parseUser(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.setAbout(jsonObject.optString("about").toString());
        String id = jsonObject.optString("id").toString();
        user.setId(id);
        user.setUsername(jsonObject.optString("username").toString());
        user.setFollowers(Integer.parseInt(jsonObject.optString("followers").toString()));
        user.setFollowing(Integer.parseInt(jsonObject.optString("following").toString()));
        user.setImage(jsonObject.optString("image").toString());
        user.setUrl(jsonObject.optString("url").toString());
        user.setHandle(jsonObject.optString("handle").toString());
        user.setIs_Following(Boolean.getBoolean(jsonObject.getString("is_following").toString()));
        user.setCreatedOn(Long.getLong(jsonObject.getString("createdOn").toString()));
        ((RoposoApplication)getApplication()).userList.put(id, user);
    }

}
