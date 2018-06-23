/*
 * Permission Please v0.9
 * By: Jafar Abdulrasoul [Jimmar]
 * Original code from https://stackoverflow.com/questions/35027043/implementing-android-6-0-permissions-in-unity3d
 * Special thanks to Noodlecake studios
 */

package net.jimmar.unityplugins;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;


import com.unity3d.player.UnityPlayer;

public class PermissionPlease
{
    private final static String UNITY_CALLBACK_GAMEOBJECT_NAME = "PermissionPlease";
    private final static String UNITY_CALLBACK_METHOD_NAME = "PermissionRequestCallbackInternal";
    public final static String PERMISSION_GRANTED = "PERMISSION_GRANTED";
    public final static String PERMISSION_DENIED = "PERMISSION_DENIED";
    private final static String[] permissionsArr = {
            //Calendar
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
            //Camera
            Manifest.permission.CAMERA,
            //Contacts
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.GET_ACCOUNTS,
            //Location
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            //Microphone
            Manifest.permission.RECORD_AUDIO,
            //Phone
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ANSWER_PHONE_CALLS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.ADD_VOICEMAIL,
            Manifest.permission.USE_SIP,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            //Sensors
            Manifest.permission.BODY_SENSORS,
            //SMS
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_WAP_PUSH,
            Manifest.permission.RECEIVE_MMS,
            //Storage
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    
    
    public static boolean logging = false;
    
    public static String getPermissionStringFromEnumInt(int permissionEnum) throws Exception
    {
        if(permissionEnum < permissionsArr.length)
            return permissionsArr[permissionEnum];

        if(logging) Log.e("[PermissionPlease]", "Error. Unknown permissionEnum " + permissionEnum);
        throw new Exception(String.format("Error. Unknown permissionEnum %d",permissionEnum));
    }
    
    public static void UnitySendMessage(String message){
        UnityPlayer.UnitySendMessage(UNITY_CALLBACK_GAMEOBJECT_NAME, UNITY_CALLBACK_METHOD_NAME, message);
    }
    
    public static void grantPermission(Activity currentActivity, int permissionEnum, boolean enableLogging)
    {
        logging = enableLogging;
        if(logging) Log.i("[PermissionPlease]","grantPermission " + permissionEnum);
        if (Build.VERSION.SDK_INT < 23) {
            if(logging) Log.i("[PermissionPlease]","Build.VERSION.SDK_INT < 23 (" + Build.VERSION.SDK_INT+")");
            UnitySendMessage(PERMISSION_GRANTED);
            return;
        }

        try
        {
            final int PERMISSIONS_REQUEST_CODE = permissionEnum;
            final String permissionFromEnumInt = getPermissionStringFromEnumInt(permissionEnum);
            if (currentActivity.checkCallingOrSelfPermission(permissionFromEnumInt) == PackageManager.PERMISSION_GRANTED) {
                if(logging) Log.i("[PermissionPlease]", "already granted");
                UnitySendMessage(PERMISSION_GRANTED);
                return;
            }

            final FragmentManager fragmentManager = currentActivity.getFragmentManager();
            final Fragment request = new InnerFragment(permissionEnum, permissionFromEnumInt, fragmentManager);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(0, request);
            fragmentTransaction.commit();
        }
        catch(Exception error)
        {
            if(logging) Log.w("[PermissionPlease]", String.format("Unable to request permission: %s", error.getMessage()));
            UnitySendMessage(PERMISSION_DENIED);
        }
    }

    public static class InnerFragment extends Fragment{
        String permissionFromEnumInt;
        int PERMISSIONS_REQUEST_CODE;
        FragmentManager fragmentManager;

        public InnerFragment(int permissionEnum, String permissionFromEnumInt, FragmentManager fragmentManager){
            PERMISSIONS_REQUEST_CODE = permissionEnum;
            this.permissionFromEnumInt = permissionFromEnumInt;
            this.fragmentManager = fragmentManager;
        }

        @Override public void onStart()
        {
            super.onStart();
            String[] permissionsToRequest = new String [] {permissionFromEnumInt};
            requestPermissions(permissionsToRequest, PERMISSIONS_REQUEST_CODE);
        }

        @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
        {
            if (requestCode != PERMISSIONS_REQUEST_CODE)
                return;

            if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted
                if(logging) Log.i("[PermissionPlease]", PERMISSION_GRANTED);
                UnitySendMessage(PERMISSION_GRANTED);
            } else {

                // permission denied
                if(logging) Log.i("[PermissionPlease]",PERMISSION_DENIED);
                UnitySendMessage(PERMISSION_DENIED);
            }

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(this);
            fragmentTransaction.commit();
        }
    }
}
