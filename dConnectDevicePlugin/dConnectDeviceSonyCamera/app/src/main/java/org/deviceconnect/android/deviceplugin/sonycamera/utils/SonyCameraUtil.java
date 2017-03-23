/*
SonyCameraUtil
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package org.deviceconnect.android.deviceplugin.sonycamera.utils;

import org.json.JSONObject;

/**
 * ユーティリティクラス.
 * @author NTT DOCOMO, INC.
 */
public final class SonyCameraUtil {
    /** SonyCameraのWiFiのプレフィックス. */
    private static final String WIFI_PREFIX = "DIRECT-";

    /**
     * Camera Remote APIに対応したWiFiのSSIDのサフィックスを定義.
     */
    private static final String[] CAMERA_SUFFIX = {"HDR-AS100", "ILCE-6000", "DSC-HC60V", "DSC-HX400", "ILCE-5000",
            "DSC-QX10", "DSC-QX100", "HDR-AS15", "HDR-AS30", "HDR-MV1", "NEX-5R", "NEX-5T", "NEX-6", "ILCE-7R/B",
            "ILCE-7/B" };

    /** 撮影画像サイズ20Mの場合のピクセル数. */
    private static final int PIXELS_20_M = 20000000;
    /** 撮影画像サイズ18Mの場合のピクセル数. */
    private static final int PIXELS_18_M = 18000000;
    /** 撮影画像サイズ17Mの場合のピクセル数. */
    private static final int PIXELS_17_M = 17000000;
    /** 撮影画像サイズ13Mの場合のピクセル数. */
    private static final int PIXELS_13_M = 13000000;
    /** 撮影画像サイズ7.5Mの場合のピクセル数. */
    private static final int PIXELS_7_5_M = 7500000;
    /** 撮影画像サイズ25Mの場合のピクセル数. */
    private static final int PIXELS_5_M = 5000000;
    /** 撮影画像サイズ4.2Mの場合のピクセル数. */
    private static final int PIXELS_4_2_M = 4200000;
    /** 撮影画像サイズ3.7Mの場合のピクセル数. */
    private static final int PIXELS_3_7_M = 3700000;

    /** 動画撮影モード. */
    private static final String SONY_CAMERA_SHOOT_MODE_MOVIE = "movie";
    /** 静止画撮影モード. */
    private static final String SONY_CAMERA_SHOOT_MODE_PIC = "still";

    /** 撮影中. */
    private static final String SONY_CAMERA_STATUS_RECORDING = "MovieRecording";
    /** 停止中. */
    private static final String SONY_CAMERA_STATUS_IDLE = "IDLE";


    public enum SonyCameraStatus {
        Recording(SONY_CAMERA_STATUS_RECORDING),
        Idle(SONY_CAMERA_STATUS_IDLE);

        private String mValue;

        SonyCameraStatus(final String value) {
            mValue = value;
        }

        public static SonyCameraStatus getStatus(final String status) {
            for (SonyCameraStatus s : values()) {
                if (s.mValue.equals(status)) {
                    return s;
                }
            }
            return null;
        }
    }


    /**
     * コンストラクタ. ユーティリティクラスなのでprivateにしておく。
     */
    private SonyCameraUtil() {
    }

    /**
     * 指定されたSSIDがSonyCameraデバイスのWifiのSSIDかチェックする.
     * 
     * @param ssid SSID
     * @return SonyCameraデバイスのSSIDの場合はtrue、それ以外はfalse
     */
    public static boolean checkSSID(final String ssid) {
        if (ssid == null) {
            return false;
        }
        String id = ssid.replace("\"", "");
        if (id.startsWith(WIFI_PREFIX)) {
            for (String aCAMERA_SUFFIX : CAMERA_SUFFIX) {
                if (id.indexOf(aCAMERA_SUFFIX) > 0) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * ピクセル数計算用メソッド.
     *
     * @param widthVal width
     * @param heightVal height
     * @param size aspect
     * @return stillSize
     */
    public static double pixelValueCalculate(final int widthVal, final int heightVal, final String size) {
        int pixels;
        double pixelValue = 0;

        switch (size) {
            case "20M":
                pixels = PIXELS_20_M;
                pixelValue = Math.sqrt(pixels / (widthVal * heightVal));
                break;
            case "18M":
                pixels = PIXELS_18_M;
                pixelValue = Math.sqrt(pixels / (widthVal * heightVal));
                break;
            case "17M":
                pixels = PIXELS_17_M;
                pixelValue = Math.sqrt(pixels / (widthVal * heightVal));
                break;
            case "13M":
                pixels = PIXELS_13_M;
                pixelValue = Math.sqrt(pixels / (widthVal * heightVal));
                break;
            case "7.5M":
                pixels = PIXELS_7_5_M;
                pixelValue = Math.sqrt(pixels / (widthVal * heightVal));
                break;
            case "5M":
                pixels = PIXELS_5_M;
                pixelValue = Math.sqrt(pixels / (widthVal * heightVal));
                break;
            case "4.2M":
                pixels = PIXELS_4_2_M;
                pixelValue = Math.sqrt(pixels / (widthVal * heightVal));
                break;
            case "3.7M":
                pixels = PIXELS_3_7_M;
                pixelValue = Math.sqrt(pixels / (widthVal * heightVal));
                break;
        }

        return pixelValue;
    }

    /**
     * カメラの状態をDeviceConnectの状態に変換する.
     * @param cameraState カメラの状態
     * @return DeviceConnectの状態
     */
    public static String convertCameraState(final String cameraState) {
        if (cameraState == null) {
            return "unknown";
        } else if (cameraState.equals("Error") || cameraState.equals("NotReady")
                || cameraState.equals("MovieSaving") || cameraState.equals("AudioSaving")
                || cameraState.equals("StillSaving") || cameraState.equals("IDLE")) {
            return "inactive";
        } else if (cameraState.equals("StillCapturing") || cameraState.equals("MediaRecording")
                || cameraState.equals("AudioRecording") || cameraState.equals("IntervalRecording")) {
            return "recording";
        } else if (cameraState.equals("MovieWaitRecStart") || cameraState.equals("MoviewWaitRecStop")
                || cameraState.equals("AudioWaitRecStart") || cameraState.equals("AudioRecWaitRecStop")
                || cameraState.equals("IntervalWaitRecStart")
                || cameraState.equals("IntervalWaitRecStop")) {
            return "paused";
        } else {
            return "unknown";
        }
    }

    /**
     * SonyCameraからの返り値のエラーチェック.
     *
     * @param replyJson レスポンスJSON
     * @return エラーの場合はtrue、それ以外はfalse
     */
    public static boolean isErrorReply(final JSONObject replyJson) {
        return (replyJson != null && replyJson.has("error"));
    }

}
