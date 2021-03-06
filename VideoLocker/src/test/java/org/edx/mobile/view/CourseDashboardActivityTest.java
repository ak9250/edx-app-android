package org.edx.mobile.view;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.edx.mobile.R;
import org.edx.mobile.model.api.EnrolledCoursesResponse;
import org.edx.mobile.third_party.iconify.IconView;
import org.edx.mobile.third_party.iconify.Iconify;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import static org.assertj.android.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class CourseDashboardActivityTest extends CourseBaseActivityTest {
    /**
     * Method for defining the subclass of {@link CourseDashboardActivity} that
     * is being tested. Should be overridden by subclasses.
     *
     * @return The {@link CourseDashboardActivity} subclass that is being tested
     */
    @Override
    protected Class<? extends CourseDashboardActivity> getActivityClass() {
        return CourseDashboardActivity.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean appliesPrevTransitionOnRestart() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void initializeTest() {
        super.initializeTest();

        Intent intent = getIntent();
        Bundle data = intent.getBundleExtra(Router.EXTRA_BUNDLE);
        EnrolledCoursesResponse courseData = (EnrolledCoursesResponse)
                data.getSerializable(Router.EXTRA_ENROLLMENT);
        ActivityController<? extends CourseDashboardActivity> controller =
                Robolectric.buildActivity(getActivityClass()).withIntent(intent);
        CourseDashboardActivity activity = controller.get();
        controller.create(null).postCreate(null);
        Fragment fragment = activity.getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        assertNotNull(fragment);
        assertThat(fragment).isInstanceOf(CourseDashboardFragment.class);
        assertTrue(fragment.getRetainInstance());
        Bundle args = fragment.getArguments();
        assertNotNull(args);
        assertEquals(courseData, args.getSerializable(CourseDashboardFragment.CourseData));

        controller.resume().postResume().visible();
        View fragmentView = fragment.getView();
        assertNotNull(fragmentView);

        // TODO: Create a separate test suite for CourseDashboardFragment views

        // Test navigation to courseware
        View rowsContainer = fragmentView.findViewById(R.id.dashboard_detail);
        assertNotNull(rowsContainer);
        assertThat(rowsContainer).isInstanceOf(ViewGroup.class);
        ViewGroup rowsContainerGroup = (ViewGroup) rowsContainer;

        View coursewareRowView = rowsContainerGroup.getChildAt(0);
        assertRow(coursewareRowView, Iconify.IconValue.fa_list_alt,
                R.string.courseware_title, R.string.courseware_subtitle);
        View discussionRowView = rowsContainerGroup.getChildAt(1);
        assertRow(discussionRowView, Iconify.IconValue.fa_comments_o,
                R.string.discussion_title, R.string.discussion_subtitle);
        View handoutsRowView = rowsContainerGroup.getChildAt(2);
        assertRow(handoutsRowView, Iconify.IconValue.fa_file_text_o,
                R.string.handouts_title, R.string.handouts_subtitle);
        View announcementRowView = rowsContainerGroup.getChildAt(3);
        assertRow(announcementRowView, Iconify.IconValue.fa_bullhorn,
                R.string.announcement_title, R.string.announcement_subtitle);

        assertTrue(coursewareRowView.performClick());
        Intent newIntent = assertNextStartedActivity(activity, CourseOutlineActivity.class);
        Bundle newData = newIntent.getBundleExtra(Router.EXTRA_BUNDLE);
        assertNotNull(newData);
        assertEquals(courseData, newData.getSerializable(Router.EXTRA_ENROLLMENT));
        // We don't seem to have one-to-one mapping between the course list and detail ids
        /*assertEquals(courseData.getCourse().getId(),
                newData.getString(Router.EXTRA_COURSE_COMPONENT_ID));*/
    }

    /**
     * Generic method for asserting the properties of the row views given a
     * certain row layout and the expected properties.
     *
     * @param rowView The root {link View} of the row
     * @param icon The Font Awesome icon key for the row
     * @param titleRes The string resource id for the row title
     * @param subtitleRes The string resource id for the row subtitle
     */
    private void assertRow(View rowView, Iconify.IconValue icon,
            int titleRes, int subtitleRes) {
        assertNotNull(rowView);
        assertThat(rowView).isInstanceOf(ViewGroup.class);

        View iconView = rowView.findViewById(R.id.row_type);
        assertNotNull(iconView);
        assertThat(iconView).isInstanceOf(IconView.class);
        assertEquals(icon, ((IconView) iconView).getIcon());

        Resources res = rowView.getContext().getResources();

        View titleView = rowView.findViewById(R.id.row_title);
        assertNotNull(titleView);
        assertThat(titleView).isInstanceOf(TextView.class);
        assertThat((TextView) titleView).hasText(res.getText(titleRes));

        View subtitleView = rowView.findViewById(R.id.row_subtitle);
        assertNotNull(subtitleView);
        assertThat(subtitleView).isInstanceOf(TextView.class);
        assertThat((TextView) subtitleView).hasText(res.getText(subtitleRes));
    }
}
