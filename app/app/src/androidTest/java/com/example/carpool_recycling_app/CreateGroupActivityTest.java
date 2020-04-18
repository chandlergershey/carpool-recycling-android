package com.example.carpool_recycling_app;


import android.widget.Button;
import android.widget.TextView;

import androidx.test.rule.ActivityTestRule;

import org.junit.Test;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class CreateGroupActivityTest extends ActivityTestRule<CreateGroupActivity>{
    private CreateGroupActivity mCreateGroupActivity;

    public CreateGroupActivityTest() {
        super(CreateGroupActivity.class);
        launchActivity(getActivityIntent());
        mCreateGroupActivity = getActivity();

        getInstrumentation().waitForIdleSync();

    }



    @Override
    protected void beforeActivityLaunched() {
        super.beforeActivityLaunched();


    }

    @Test
    public void testPreconditions() {
        assertNotNull(mCreateGroupActivity);
    }

    @Test
    public void testSaveNewGroup() {
        TextView groupName = mCreateGroupActivity.findViewById(R.id.group_name_edit_text);
        groupName.setText("My New Group");

        Button submit = mCreateGroupActivity.findViewById(R.id.submit_group_button);
        submit.performClick();

        assertNotNull(mCreateGroupActivity.getGroup());
        assertEquals("My New Group", mCreateGroupActivity.getName());
    }

    @Test
    public void testSaveNewGroupError() {
        TextView groupName = mCreateGroupActivity.findViewById(R.id.group_name_edit_text);
        groupName.setText("");

        Button submit = mCreateGroupActivity.findViewById(R.id.submit_group_button);
        submit.performClick();

        assertEquals(null, mCreateGroupActivity.getGroup());
        assertEquals("Please enter a group name", groupName.getError());
    }



}
