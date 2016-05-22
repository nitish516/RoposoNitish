package com.example.roposonitish;

import android.app.Application;

import com.example.roposonitish.data.Story;
import com.example.roposonitish.data.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Nitish on 5/21/2016.
 */
public class RoposoApplication extends Application {
    Map<String,User> userList = new HashMap<>();
    List<Story> storyList = new ArrayList<>();
    Set<String> followSet = new HashSet<String>();
}
