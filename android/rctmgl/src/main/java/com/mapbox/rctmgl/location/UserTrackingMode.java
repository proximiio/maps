package com.mapbox.rctmgl.location;

/*
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
*/

import com.mapbox.mapboxsdk.location.modes.RenderMode;

/**
 * Created by nickitaliano on 12/13/17.
 */

public class UserTrackingMode {
    public static final int NONE = 0;
    public static final int FOLLOW = 1;
    public static final int FollowWithCourse = 2;
    public static final int FollowWithHeading = 3;

    public static int getMapLayerMode(int mode, boolean isShowUserLocation) {
        if (!isShowUserLocation) {
            return -1;
        } else if (mode == NONE) {
            return -1;
        } else if (mode == FollowWithCourse) {
            return RenderMode.GPS;
        } else if (mode == FollowWithHeading) {
            return RenderMode.COMPASS;
        } else {
            return RenderMode.NORMAL;
        }
    }

    public static boolean isUserGesture(int reason) {
        return reason == 1 || reason == 2; // user gesture or animation
    }

    public static String toString(int value) {
        switch (value) {
            case UserTrackingMode.FOLLOW:
                return "normal";
            case UserTrackingMode.FollowWithCourse:
                return "course";
            case UserTrackingMode.FollowWithHeading:
                return "compass";
        }
        return null;
    }

    public static int fromString(String value) {
        if (value == null) value = "";
        switch (value) {
            case "course":
                return UserTrackingMode.FollowWithCourse;
            case "normal":
                return UserTrackingMode.FOLLOW;
            case "compass":
                return UserTrackingMode.FollowWithHeading;
            default:
                return UserTrackingMode.NONE;
        }
    }
}
