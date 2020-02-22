package com.example.group_32.chatloca;


import android.app.Instrumentation;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.group_32.chatloca.activities.MessageActivity;
import com.example.group_32.chatloca.activities.Sign_inActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class SignInActivityTest {
    @Rule
    public ActivityTestRule<Sign_inActivity> activityTestRule = new ActivityTestRule<>(Sign_inActivity.class, true, true);

    @Test
    public void LogInEmailSuccessfully(){
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(MessageActivity.class.getName(), null, false);

        onView(withId(R.id.editText_SignInUsername)).perform(typeText("ad@min.vn"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.editText_SignInPassword)).perform(typeText("123456"));

        //  Close SoftKeyboard to avoid click on the SoftKeyboard when click button SigIn
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.button_mail_signIn)).perform(click());

        MessageActivity messageActivity = (MessageActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 4000);
        assertNotNull(messageActivity);

        // Logout, Dont pay attention to it
        onView(withId(R.id.image_avatar)).perform(click());
        onView(withId(R.id.button_Logout)).perform(click());
    }

    @Test
    public void LogInEmailFail(){
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(MessageActivity.class.getName(), null, false);

        onView(withId(R.id.editText_SignInUsername)).perform(typeText("yasuo"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.editText_SignInPassword)).perform(typeText("leesin"));

        //  Close SoftKeyboard to avoid click on the SoftKeyboard when click button SigIn
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.button_mail_signIn)).perform(click());

        MessageActivity messageActivity = (MessageActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 4000);
        assertNull(messageActivity);
    }
}
