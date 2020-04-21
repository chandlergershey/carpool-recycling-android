package com.example.carpool_recycling_app;


import android.widget.Button;
import android.widget.TextView;

import androidx.test.rule.ActivityTestRule;

import org.junit.Test;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AddRecyclablesActivityTest extends ActivityTestRule<AddRecyclablesActivity> {
    private AddRecyclablesActivity mAddRecyclablesActivity;


    public AddRecyclablesActivityTest() {
        super(AddRecyclablesActivity.class);
        launchActivity(getActivityIntent());

        mAddRecyclablesActivity = getActivity();

        getInstrumentation().waitForIdleSync();

    }


    @Override
    protected void beforeActivityLaunched() {
        super.beforeActivityLaunched();


    }

    @Test
    public void testPreconditions() {
        assertNotNull(mAddRecyclablesActivity);
    }

    @Test
    public void testNumRecycledDefault() {
        System.out.println("Thread ID in testUI:" + Thread.currentThread().getId());
        getInstrumentation().waitForIdleSync();
        assertEquals(0, mAddRecyclablesActivity.getNumAluminum());
        assertEquals(0, mAddRecyclablesActivity.getNumCardboard());
        assertEquals(0, mAddRecyclablesActivity.getNumGlass());
        assertEquals(0, mAddRecyclablesActivity.getNumPlastic());
    }

    @Test
    public void testNumRecycledInputSubmit() {
        System.out.println("Thread ID in testUI:" + Thread.currentThread().getId());
        getInstrumentation().waitForIdleSync();

        TextView numPlastic = mAddRecyclablesActivity.findViewById(R.id.usermetrics_numplastic_edittext);
        numPlastic.setText("3");

        TextView numGlass = mAddRecyclablesActivity.findViewById(R.id.usermetrics_numglass_edittext);
        numGlass.setText("5");

        Button submit = mAddRecyclablesActivity.findViewById(R.id.usermetrics_submitmetrics_button);
        submit.performClick();

        assertEquals(3, mAddRecyclablesActivity.getTotalPlastic());
        assertEquals(5, mAddRecyclablesActivity.getTotalGlass());
        assertEquals(0, mAddRecyclablesActivity.getNumAluminum());
        assertEquals(0, mAddRecyclablesActivity.getNumCardboard());
    }

}
