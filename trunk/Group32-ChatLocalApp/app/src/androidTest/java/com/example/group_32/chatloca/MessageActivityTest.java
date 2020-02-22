package com.example.group_32.chatloca;


import android.app.Instrumentation;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.Button;

import com.example.group_32.chatloca.activities.MessageActivity;
import com.example.group_32.chatloca.activities.Sign_inActivity;

import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Anh on 5/16/2018.
 */

@RunWith(AndroidJUnit4.class)
public class MessageActivityTest {

    @Rule
    public ActivityTestRule<Sign_inActivity> activityTestRule = new ActivityTestRule<>(Sign_inActivity.class, true, true);

    @Test
    public void SendRequestSuccessfully() throws Exception {
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(MessageActivity.class.getName(), null, false);
        try {
            onView(withId(R.id.editText_SignInUsername)).perform(typeText("demo"));
            Espresso.closeSoftKeyboard();
            onView(withId(R.id.editText_SignInPassword)).perform(typeText("123456"));
            Espresso.closeSoftKeyboard();
            onView(withId(R.id.button_mail_signIn)).perform(click());

            MessageActivity messageActivity = (MessageActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 4000);
            assertNotNull(messageActivity);
            //messageActivity.isTesting = true;
            //messageActivity.createAddUserView("demoSendRequest","JXtoRHJteqXL5ppZRr8gnT6DUsb2","demo send request");
            onView(withId(R.id.textview_add_friend_message)).perform(click());
            //  Close SoftKeyboard to avoid click on the SoftKeyboard when click button SigIn
            Espresso.closeSoftKeyboard();

            //onView(withId(R.id.searchview_Message)).perform(click());
            onView(withId(R.id.searchview_Message)).perform(typeSearchViewText("demoSendReq"));

            Button btnAdd = messageActivity.findViewById(R.id.addButton);
            onView(withId(R.id.addButton)).perform(click());
            String check = btnAdd.getText().toString();

            // Logout, Dont pay attention to it
            onView(withId(R.id.image_avatar)).perform(click());
            onView(withId(R.id.button_Logout)).perform(click());
            Assert.assertEquals("Sent", check);
        } catch (Exception e) {
            // Logout, Dont pay attention to it
            onView(withId(R.id.image_avatar)).perform(click());
            onView(withId(R.id.button_Logout)).perform(click());
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void CancelSentRequestSuccessfully() throws Exception {
        try {
            Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(MessageActivity.class.getName(), null, false);

            onView(withId(R.id.editText_SignInUsername)).perform(typeText("demo"));
            Espresso.closeSoftKeyboard();
            onView(withId(R.id.editText_SignInPassword)).perform(typeText("123456"));
            Espresso.closeSoftKeyboard();
            onView(withId(R.id.button_mail_signIn)).perform(click());

            MessageActivity messageActivity = (MessageActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 4000);
            junit.framework.Assert.assertNotNull(messageActivity);
            // messageActivity.createAddUserView("demoSendRequest","JXtoRHJteqXL5ppZRr8gnT6DUsb2","demo send request");
            onView(withId(R.id.textview_add_friend_message)).perform(click());
            //  Close SoftKeyboard to avoid click on the SoftKeyboard when click button SigIn
            Espresso.closeSoftKeyboard();

            //onView(withId(R.id.searchview_Message)).perform(click());
            onView(withId(R.id.searchview_Message)).perform(typeSearchViewText("demoSendReq"));
            onView(withId(R.id.addButton)).perform(click());

            Button btnAdd = messageActivity.findViewById(R.id.addButton);
            String check = btnAdd.getText().toString();

            // Logout, Dont pay attention to it
            onView(withId(R.id.image_avatar)).perform(click());
            onView(withId(R.id.button_Logout)).perform(click());

            Assert.assertEquals("Add", check);
        } catch (Exception e) {
            // Logout, Dont pay attention to it
            onView(withId(R.id.image_avatar)).perform(click());
            onView(withId(R.id.button_Logout)).perform(click());
            Assert.fail(e.getMessage());
        }
    }

    public static ViewAction typeSearchViewText(final String text) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                //Ensure that only apply if it is a SearchView and if it is visible.
                return allOf(isDisplayed(), isAssignableFrom(SearchView.class));
            }

            @Override
            public String getDescription() {
                return "Change view text";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((SearchView) view).setQuery(text, false);
            }
        };
    }
}
