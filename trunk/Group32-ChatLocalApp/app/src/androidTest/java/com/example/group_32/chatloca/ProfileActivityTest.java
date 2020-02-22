package com.example.group_32.chatloca;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.group_32.chatloca.activities.ProfileActivity;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ProfileActivityTest{
    @Rule
    public ActivityTestRule<ProfileActivity> activityTestRule = new ActivityTestRule<>(ProfileActivity.class, true, true);

    @Test
    public void changePhone() throws Exception{
        onView(withId(R.id.TextView_EditPhoneProfile)).perform(click());

        ViewInteraction viEditNewPhone = onView(withId(R.id.EditText_NewPhoneProfile));
        String strNewPhone = "01659999999";
        viEditNewPhone.perform(replaceText(strNewPhone));

        onView(withId(R.id.button_ConfirmPhoneProfile)).perform(click());

        onView(withId(R.id.TextView_PhoneProfile)).check(matches(withText(strNewPhone)));
    }
    @Test
    public void changePhoneButPressCancel() throws Exception{
        ProfileActivity profileActivity = activityTestRule.getActivity();

        String strCurrentPhone = profileActivity.getPhonenumber();

        onView(withId(R.id.TextView_EditPhoneProfile)).perform(click());

        ViewInteraction viEditNewPhone = onView(withId(R.id.EditText_NewPhoneProfile));

        String strNewPhone = "01659999999";

        viEditNewPhone.perform(replaceText(strNewPhone));

        onView(withId(R.id.button_CancelPhoneProfile)).perform(click());

        onView(withId(R.id.TextView_PhoneProfile)).check(matches(withText(strCurrentPhone)));
    }

    @Test
    public void changePasswordSuccesfully() throws Exception{
        ProfileActivity profileActivity = activityTestRule.getActivity();

        String passCurrent = profileActivity.getPassCurrent();
        onView(withId(R.id.TextView_ChangePasswordProfile)).perform(click());

        onView(withId(R.id.EditText_CurrentPasswordProfile)).perform(typeText(passCurrent));

        String passNew = "123456789";
        onView(withId(R.id.EditText_NewPasswordProfile)).perform(typeText(passNew));
        onView(withId(R.id.EditText_ConfirmPasswordProfile)).perform(typeText(passNew));

        onView(withId(R.id.button_ConfirmPasswordProfile)).perform(click());

        Assert.assertEquals(passNew, profileActivity.getPassCurrent());
    }

}
