
/***************************************************************************
 *   Copyright 2006-2013 by Christian Ihle                                 *
 *   contact@kouchat.net                                                   *
 *                                                                         *
 *   This file is part of KouChat.                                         *
 *                                                                         *
 *   KouChat is free software; you can redistribute it and/or modify       *
 *   it under the terms of the GNU Lesser General Public License as        *
 *   published by the Free Software Foundation, either version 3 of        *
 *   the License, or (at your option) any later version.                   *
 *                                                                         *
 *   KouChat is distributed in the hope that it will be useful,            *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU      *
 *   Lesser General Public License for more details.                       *
 *                                                                         *
 *   You should have received a copy of the GNU Lesser General Public      *
 *   License along with KouChat.                                           *
 *   If not, see <http://www.gnu.org/licenses/>.                           *
 ***************************************************************************/

package net.usikkert.kouchat.android.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.android.service.ChatService;
import net.usikkert.kouchat.android.service.ChatServiceBinder;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.ShadowPreferenceManager;
import org.robolectric.tester.android.content.TestSharedPreferences;

import android.content.ServiceConnection;

/**
 * Test of {@link SettingsController}.
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class SettingsControllerTest {

    private SettingsController controller;

    private AndroidUserInterface ui;
    private ServiceConnection serviceConnection;

    @Before
    public void setUp() {
        controller = new SettingsController();

        ui = mock(AndroidUserInterface.class);

        final ChatServiceBinder serviceBinder = mock(ChatServiceBinder.class);
        when(serviceBinder.getAndroidUserInterface()).thenReturn(ui);
        Robolectric.getShadowApplication().setComponentNameAndServiceForBindService(null, serviceBinder);

        serviceConnection = mock(ServiceConnection.class);
    }

    @Test
    @Ignore("This does not work with Robolectric yet.")
    public void onCreateShouldBindChatServiceToSetAndroidUserInterface() {
        assertTrue(TestUtils.fieldValueIsNull(controller, "androidUserInterface"));

        controller.onCreate(null); // findPreference() returns null, giving NullPointerException

        assertSame(ui, TestUtils.getFieldValue(controller, AndroidUserInterface.class, "androidUserInterface"));

        final ShadowIntent startedServiceIntent =
                Robolectric.shadowOf(Robolectric.getShadowApplication().getNextStartedService());
        assertEquals(ChatService.class, startedServiceIntent.getIntentClass());
    }

    @Test
    @Ignore("This does not work with Robolectric yet.")
    public void onResumeShouldSetControllerAsListener() {
        final TestSharedPreferences sharedPreferences = getTestSharedPreferences();

        assertFalse(sharedPreferences.hasListener(controller));

        controller.onResume(); // getPreferenceScreen() returns null, giving NullPointerException

        assertTrue(sharedPreferences.hasListener(controller));
    }

    @Test
    @Ignore("This does not work with Robolectric yet.")
    public void onPauseShouldRemoveControllerAsListener() {
        final TestSharedPreferences sharedPreferences = getTestSharedPreferences();

        assertFalse(sharedPreferences.hasListener(controller));
        sharedPreferences.registerOnSharedPreferenceChangeListener(controller);
        assertTrue(sharedPreferences.hasListener(controller));

        controller.onPause(); // getPreferenceScreen() returns null, giving NullPointerException

        assertFalse(sharedPreferences.hasListener(controller));
    }

    @Test
    public void onDestroyShouldUnregister() {
        setupMocks();

        controller.onDestroy();

        assertEquals(1, Robolectric.getShadowApplication().getUnboundServiceConnections().size());
    }

    @Test
    public void onDestroyShouldSetAllFieldsToNull() {
        setupMocks();
        assertTrue(TestUtils.allFieldsHaveValue(controller));

        controller.onDestroy();

        assertTrue(TestUtils.allFieldsAreNull(controller));
    }

    @Test
    public void onDestroyShouldNotFailIfServiceHasNotBeenBound() {
        assertTrue(TestUtils.fieldValueIsNull(controller, "androidUserInterface"));

        controller.onDestroy();

        assertEquals(0, Robolectric.getShadowApplication().getUnboundServiceConnections().size());
    }

    private void setupMocks() {
        TestUtils.setFieldValue(controller, "androidUserInterface", ui);
        TestUtils.setFieldValue(controller, "serviceConnection", serviceConnection);
    }

    private TestSharedPreferences getTestSharedPreferences() {
        return (TestSharedPreferences) ShadowPreferenceManager
                .getDefaultSharedPreferences(Robolectric.application.getApplicationContext());
    }
}
