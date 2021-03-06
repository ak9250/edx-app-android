package org.edx.mobile.view;

import android.app.ActionBar;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import org.edx.mobile.R;
import org.edx.mobile.http.OkHttpUtil;
import org.edx.mobile.model.api.EnrolledCoursesResponse;
import org.edx.mobile.model.course.CourseComponent;
import org.edx.mobile.model.course.VideoBlockModel;
import org.junit.Ignore;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static org.assertj.android.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeNotNull;

// We should add mock downloads, mock play, and state retention tests
// later. Also, online/offline transition tests; although the
// onOnline() and onOffline() methods don't seem to be called from
// anywhere yet?

// The SDK version needs to be lesser than Lollipop because of this
// issue: https://github.com/robolectric/robolectric/issues/1810
@Config(sdk = 19)
@Ignore // These tests require videos, which are not present in current mock data
public class CourseUnitVideoFragmentTest extends UiTest {
    /**
     * Method for iterating through the mock course response data, and
     * returning the first video block leaf.
     *
     * @return The first {@link VideoBlockModel} leaf in the mock course data
     */
    private VideoBlockModel getVideoUnit() {
        EnrolledCoursesResponse courseData;
        CourseComponent courseComponent;
        try {
            courseData = api.getEnrolledCourses().get(0);
            courseComponent = serviceManager.getCourseStructure(
                    courseData.getCourse().getId(),
                    OkHttpUtil.REQUEST_CACHE_TYPE.IGNORE_CACHE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return (VideoBlockModel) courseComponent.getVideos().get(0);
    }

    /**
     * Testing initialization
     */
    @Test
    public void initializeTest() {
        CourseUnitVideoFragment fragment = CourseUnitVideoFragment.newInstance(getVideoUnit());
        SupportFragmentTestUtil.startVisibleFragment(fragment);
        assertTrue(fragment.getRetainInstance());

        View view = fragment.getView();
        assertNotNull(view);
        View messageContainer = view.findViewById(R.id.message_container);
        assertNotNull(messageContainer);
    }


    /**
     * Generic method to assert action bar visibility state on a specified orientation
     *
     * @param orientation The orientation it should be tested on
     * @param expected The expected visibility state
     */
    private void assertActionBarShowing(int orientation, boolean expected) {
        FragmentActivity activity = Robolectric.setupActivity(FragmentUtilActivity.class);
        activity.getResources().getConfiguration().orientation = orientation;
        CourseUnitVideoFragment fragment = CourseUnitVideoFragment.newInstance(getVideoUnit());
        activity.getSupportFragmentManager()
                .beginTransaction().add(1, fragment, null).commit();
        assertTrue(fragment.getRetainInstance());
        ActionBar bar = activity.getActionBar();
        assumeNotNull(bar);
        assertEquals(expected, bar.isShowing());
    }

    /**
     * Testing whether action bar is displayed in portrait orientation
     */
    @Test
    @Config(qualifiers = "port")
    public void showActionBarOnPortraitTest() {
        assertActionBarShowing(Configuration.ORIENTATION_PORTRAIT, true);
    }

    /**
     * Testing whether action bar is hidden in landscape orientation
     */
    @Test
    @Config(qualifiers = "land")
    public void hideActionBarOnLandscapeTest() {
        assertActionBarShowing(Configuration.ORIENTATION_LANDSCAPE, false);
    }

    /**
     * Generic method for testing setup on orientation changes
     *
     * @param fragment The current fragment
     * @param orientation The orientation change to test
     */
    private void testOrientationChange(
            CourseUnitVideoFragment fragment, int orientation) {
        Resources resources = fragment.getResources();
        Configuration config = resources.getConfiguration();
        assertNotEquals(orientation, config.orientation);
        config.orientation = orientation;
        fragment.onConfigurationChanged(config);

        boolean isLandscape = config.orientation ==
                Configuration.ORIENTATION_LANDSCAPE;
        View view = fragment.getView();
        assertNotNull(view);
        View messageContainer = view.findViewById(R.id.message_container);
        if (isLandscape) {
            assertThat(messageContainer).isNotVisible();
        } else {
            assertThat(messageContainer).isVisible();
        }
        if (Build.VERSION.SDK_INT < 16) {
            Window window = fragment.getActivity().getWindow();
            int windowAttributes = window.getAttributes().flags;
            int expectedFullscreenFlag = isLandscape ?
                    WindowManager.LayoutParams.FLAG_FULLSCREEN :
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
            assertTrue((windowAttributes & expectedFullscreenFlag) > 0);
        } else {
            View decorView = fragment.getActivity().getWindow().getDecorView();
            int expectedVisibilityFlag = isLandscape ?
                    View.SYSTEM_UI_FLAG_FULLSCREEN : View.VISIBLE;
            assertEquals(expectedVisibilityFlag, decorView.getSystemUiVisibility());
        }

        View playerContainer = view.findViewById(R.id.player_container);
        if (playerContainer != null) {
            assertThat(playerContainer).isInstanceOf(ViewGroup.class);
            ViewGroup.LayoutParams layoutParams = playerContainer.getLayoutParams();
            assertNotNull(layoutParams);
            assertThat(layoutParams).hasWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            int height = isLandscape ? displayMetrics.heightPixels :
                    (displayMetrics.widthPixels * 9 / 16);
            assertThat(layoutParams).hasHeight(height);
        }
    }

    /**
     * Testing orientation changes
     */
    @Test
    public void orientationChangeTest() {
        CourseUnitVideoFragment fragment = CourseUnitVideoFragment.newInstance(getVideoUnit());
        SupportFragmentTestUtil.startVisibleFragment(fragment);
        assertNotEquals(Configuration.ORIENTATION_LANDSCAPE,
                fragment.getResources().getConfiguration().orientation);

        testOrientationChange(fragment, Configuration.ORIENTATION_LANDSCAPE);
        testOrientationChange(fragment, Configuration.ORIENTATION_PORTRAIT);
    }

    private static class FragmentUtilActivity extends FragmentActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            LinearLayout view = new LinearLayout(this);
            // noinspection ResourceType
            view.setId(1);

            setContentView(view);
        }
    }
}
