/*
 FPLUGSystemProfile.java
 Copyright (c) 2015 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.fplug.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.deviceconnect.android.deviceplugin.fplug.setting.FPLUGServiceListActivity;
import org.deviceconnect.android.profile.SystemProfile;

/**
 * System Profile.
 *
 * @author NTT DOCOMO, INC.
 */
public class FPLUGSystemProfile extends SystemProfile {

    @Override
    protected Class<? extends Activity> getSettingPageActivity(final Intent request, final Bundle param) {
        return FPLUGServiceListActivity.class;
    }

}
