package com.example.seg2105_d1;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.seg2105_d1.ViewController.LoginPage;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest2 {
    @Rule
    public ActivityScenarioRule<LoginPage> mActivityTestRule = new ActivityScenarioRule<>(LoginPage.class);

    @Test
    public void emailIsInvalid() throws Exception{
        onView(withId(R.id.editTextTextEmailAddress)).perform(typeText("email@"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.editTextTextPassword)).perform(typeText("pass"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.logInButton)).perform(click());
        Thread.sleep(3000);
        onView(withText("User not found")).check(matches(isDisplayed()));
    }
}
