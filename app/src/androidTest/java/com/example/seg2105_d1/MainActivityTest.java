package com.example.seg2105_d1;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import android.widget.TextView;
import org.junit.*;

import androidx.annotation.UiThread;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.seg2105_d1.ViewController.LoginPage;
import com.example.seg2105_d1.ViewController.MainPage;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<LoginPage> mActivityTestRule= new ActivityScenarioRule<LoginPage>(LoginPage.class);
    private LoginPage mActivity = null;
    private TextView text;

    @Before
    public void setUp() throws Exception{
        mActivityTestRule.getScenario().onActivity(activity ->{
           mActivity = activity;
           text = activity.findViewById(R.id.editTextTextEmailAddress);
        });
    }

    @Test
    public void testCheckEmailAddress() throws Exception{
        assertNotNull(mActivity.findViewById(R.id.editTextTextEmailAddress));
        mActivityTestRule.getScenario().onActivity(activity -> {
            TextView text = activity.findViewById(R.id.editTextTextEmailAddress);
            text.setText("tutor1@uottawa.ca");
            String name = text.getText().toString();
            assertNotEquals("user", name);
        });
    }

    @Test
    public void testCheckPassword() throws Exception{
        assertNotNull(mActivity.findViewById(R.id.editTextTextPassword));
        mActivityTestRule.getScenario().onActivity(activity -> {
            TextView text = activity.findViewById(R.id.editTextTextPassword);
            text.setText("tutor123");
            String name = text.getText().toString();
            assertNotEquals("password", name);
        });
    }




}
