/*
 HostBatteryProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */

package org.deviceconnect.android.deviceplugin.host.profile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.deviceconnect.android.deviceplugin.host.HostDeviceService;
import org.deviceconnect.android.deviceplugin.host.manager.HostBatteryManager;
import org.deviceconnect.android.event.EventError;
import org.deviceconnect.android.event.EventManager;
import org.deviceconnect.android.message.MessageUtils;
import org.deviceconnect.android.profile.BatteryProfile;
import org.deviceconnect.message.DConnectMessage;
import org.deviceconnect.message.intent.message.IntentDConnectMessage;

import android.content.Intent;

/**
 * Battery Profile.
 * 
 * @author NTT DOCOMO, INC.
 */
public class HostBatteryProfile extends BatteryProfile {
    /** エラーコード. */
    private static final int ERROR_CODE = 100;

    @Override
    protected boolean onGetLevel(final Intent request, final Intent response, final String deviceId) {

        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            int mLevel = ((HostDeviceService) getContext()).getBatteryLevel();
            int mScale = ((HostDeviceService) getContext()).getBatteryScale();
            if (mScale <= 0) {
                MessageUtils.setUnknownError(response, "Scale of battery level is unknown.");
            } else if (mLevel < 0) {
                MessageUtils.setUnknownError(response, "Battery level is unknown.");
            } else {
                setResult(response, IntentDConnectMessage.RESULT_OK);
                setLevel(response, mLevel / (float) mScale);
            }
        }
        return true;
    }

    @Override
    protected boolean onGetCharging(final Intent request, final Intent response, final String deviceId) {

        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            int mStatus = ((HostDeviceService) getContext()).getBatteryStatus();
            setResult(response, IntentDConnectMessage.RESULT_OK);
            setCharging(response, getBatteryChargingStatus(mStatus));
        }
        return true;
    }

    @Override
    protected boolean onGetAll(final Intent request, final Intent response, final String deviceId) {

        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            int mLevel = ((HostDeviceService) getContext()).getBatteryLevel();
            int mScale = ((HostDeviceService) getContext()).getBatteryScale();
            if (mScale <= 0) {
                MessageUtils.setUnknownError(response, "Scale of battery level is unknown.");
            } else if (mLevel < 0) {
                MessageUtils.setUnknownError(response, "Battery level is unknown.");
            } else {
                setLevel(response, mLevel / (float) mScale);
                int mStatus = ((HostDeviceService) getContext()).getBatteryStatus();
                setCharging(response, getBatteryChargingStatus(mStatus));

                setResult(response, IntentDConnectMessage.RESULT_OK);
            }
        }
        return true;
    }

    @Override
    protected boolean onPutOnChargingChange(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (sessionKey == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            // Add event
            EventError error = EventManager.INSTANCE.addEvent(request);
            if (error == EventError.NONE) {
                ((HostDeviceService) getContext()).setDeviceId(deviceId);
                ((HostDeviceService) getContext()).registerBatteryConnectBroadcastReceiver();
                setResult(response, DConnectMessage.RESULT_OK);
            } else {
                setResult(response, DConnectMessage.RESULT_ERROR);
            }
        }
        return true;
    }

    @Override
    protected boolean onDeleteOnChargingChange(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (sessionKey == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            ((HostDeviceService) getContext()).unregisterBatteryConnectBroadcastReceiver();
            // イベントの解除
            EventError error = EventManager.INSTANCE.removeEvent(request);
            if (error == EventError.NONE) {
                setResult(response, DConnectMessage.RESULT_OK);
            } else {
                MessageUtils.setError(response, ERROR_CODE, "Can not unregister event.");
            }
        }
        return true;
    }

    @Override
    protected boolean onPutOnBatteryChange(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {

        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (sessionKey == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            // Add event
            EventError error = EventManager.INSTANCE.addEvent(request);
            if (error == EventError.NONE) {
                ((HostDeviceService) getContext()).setDeviceId(deviceId);
                ((HostDeviceService) getContext()).registerBatteryChargeBroadcastReceiver();
                setResult(response, DConnectMessage.RESULT_OK);
            } else {
                setResult(response, DConnectMessage.RESULT_ERROR);
            }
        }
        return true;
    }

    @Override
    protected boolean onDeleteOnBatteryChange(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {

        if (deviceId == null) {
            createEmptyDeviceId(response);
        } else if (!checkDeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (sessionKey == null) {
            MessageUtils.setInvalidRequestParameterError(response);
        } else {
            // イベントの解除
            ((HostDeviceService) getContext()).unregisterBatteryChargeBroadcastReceiver();
            EventError error = EventManager.INSTANCE.removeEvent(request);
            if (error == EventError.NONE) {
                setResult(response, DConnectMessage.RESULT_OK);
            } else {
                MessageUtils.setError(response, ERROR_CODE, "Can not unregister event.");
            }
        }
        return true;
    }

    /**
     * Get status of charging.
     * 
     * @param mStatus BatteryStatus
     * @return true:charging false:not charging
     */
    private boolean getBatteryChargingStatus(final int mStatus) {
        switch (mStatus) {
        case HostBatteryManager.BATTERY_STATUS_CHARGING:
        case HostBatteryManager.BATTERY_STATUS_FULL:
            return true;
        case HostBatteryManager.BATTERY_STATUS_UNKNOWN:
        case HostBatteryManager.BATTERY_STATUS_DISCHARGING:
        case HostBatteryManager.BATTERY_STATUS_NOT_CHARGING:
        default:
            return false;
        }
    }

    /**
     * Check deviceId.
     * 
     * @param deviceId DeviceId
     * @return <code>deviceId</code>がテスト用デバイスIDに等しい場合はtrue、そうでない場合はfalse
     */
    private boolean checkDeviceId(final String deviceId) {
        String regex = HostNetworkServiceDiscoveryProfile.DEVICE_ID;
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(deviceId);
        return m.find();
    }

    /**
     * デバイスIDが空の場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createEmptyDeviceId(final Intent response) {
        MessageUtils.setEmptyDeviceIdError(response);
    }

    /**
     * デバイスが発見できなかった場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createNotFoundDevice(final Intent response) {
        MessageUtils.setNotFoundDeviceError(response);
    }
}
