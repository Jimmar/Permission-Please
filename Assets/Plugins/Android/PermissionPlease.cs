/*
 * Permission Please v0.9
 * By: Jafar Abdulrasoul [Jimmar]
 * Original code from https://stackoverflow.com/questions/35027043/implementing-android-6-0-permissions-in-unity3d
 * Special thanks to Noodlecake studios
 */

using System;
using UnityEngine;

namespace Plugins.Android.PermissionPlease
{
    public class PermissionPlease : MonoBehaviour
    {
        private static Action<bool> PermissionRequestCallback;

        public enum AndroidPermission
        {
            //Calendar
            READ_CALENDAR,
            WRITE_CALENDAR,
            //Camera
            CAMERA,
            //Contacts
            READ_CONTACTS,
            WRITE_CONTACTS,
            GET_ACCOUNTS,
            //Location
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION,
            //Microphone
            RECORD_AUDIO,
            //Phone
            READ_PHONE_STATE,
            READ_PHONE_NUMBERS,
            CALL_PHONE,
            ANSWER_PHONE_CALLS,
            READ_CALL_LOG,
            WRITE_CALL_LOG,
            ADD_VOICEMAIL,
            USE_SIP,
            PROCESS_OUTGOING_CALLS,
            //Sensors
            BODY_SENSORS,
            //SMS
            SEND_SMS,
            RECEIVE_SMS,
            READ_SMS,
            RECEIVE_WAP_PUSH,
            RECEIVE_MMS,
            //Storage
            WRITE_EXTERNAL_STORAGE,
            READ_EXTERNAL_STORAGE,
        }

        public static void GrantPermission(AndroidPermission permission, Action<bool> requestCallback = null,
            bool enableLogging = false)
        {
            #if !UNITY_ANDROID
                Debug.LogWarning("Only Android platform is supported");
                return;
            #endif
            
            #if UNITY_EDITOR
                Debug.LogWarning("Editor is not supported, test on an actual device or an Android Emulator");
                return;
            #endif
            
            if (!initialized)
                initialize();
            PermissionRequestCallback = requestCallback;
            PermissionPleaseClass.CallStatic("grantPermission", activity, (int) permission, enableLogging);
        }

        //////////////////////////////
        /// Initialization Stuff /////
        //////////////////////////////

        // it's a singleton, but no one needs to know about it. hush hush. dont touch me.
        private static PermissionPlease instance;

        private static bool initialized;

        public void Awake()
        {
            // instance is also set in initialize.
            // this ensures that it doesn't break if the component was added to the scene manually.
            instance = this;
            DontDestroyOnLoad(gameObject);
            // object name must match UnitySendMessage call in PermissionPlease.java
            if (name != PERMISSION_PLEASE_NAME)
                name = PERMISSION_PLEASE_NAME;
        }

        private static void initialize()
        {
            // runs once when you call GrantPermission
            if (instance == null)
            {
                GameObject go = new GameObject();
                // instance will also be set in awake, but having it here as well seems extra safe
                instance = go.AddComponent<PermissionPlease>();
                // object name must match UnitySendMessage call in PermissionPlease.java
                go.name = PERMISSION_PLEASE_NAME;
            }

            // get the jni stuff. we need the activty class and the PermissionPlease class.
            PermissionPleaseClass = new AndroidJavaClass("net.jimmar.unityplugins.PermissionPlease");
            AndroidJavaClass u3d = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
            activity = u3d.GetStatic<AndroidJavaObject>("currentActivity");

            initialized = true;
        }


        ///////////////////
        //// JNI Stuff ////
        ///////////////////

        private static AndroidJavaClass PermissionPleaseClass;
        private static AndroidJavaObject activity;
        private const string PERMISSION_GRANTED = "PERMISSION_GRANTED"; // must match PermissionPlease.java
        private const string PERMISSION_DENIED = "PERMISSION_DENIED"; // must match PermissionPlease.java

        // must match UnitySendMessage call in PermissionPlease.java
        private const string PERMISSION_PLEASE_NAME = "PermissionPlease";

        private void PermissionRequestCallbackInternal(string message)
        {
            // this method is caleld from the java side.
            // the method name and gameobject name must match PermissionPlease.java's UnitySendMessage
            bool permissionGranted = (message == PERMISSION_GRANTED);
            if (PermissionRequestCallback != null)
                PermissionRequestCallback(permissionGranted);
        }
    }
}