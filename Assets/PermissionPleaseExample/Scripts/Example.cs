using Plugins.Android.PermissionPlease;
using UnityEngine;
using UnityEngine.UI;

public class Example : MonoBehaviour
{
    [SerializeField] protected Button Button1;
    [SerializeField] protected Button Button2;

    // Use this for initialization
    void Awake()
    {
        Button1.onClick.AddListener(() => GrantPermission(PermissionPlease.AndroidPermission.READ_EXTERNAL_STORAGE));
        Button2.onClick.AddListener(() => GrantPermission(PermissionPlease.AndroidPermission.ACCESS_COARSE_LOCATION));
    }

    private void GrantPermission(PermissionPlease.AndroidPermission permission)
    {
        #if UNITY_ANDROID
        print("grantingPermission");
        PermissionPlease.GrantPermission(permission, OnPermissionCallback, true);
        #else
        // handle other device's permissions here, iOS for example
        #endif
    }

    private void OnPermissionCallback(bool granted)
    {
        if(granted)
            print("permission granted xD");
        else
            print("permission denied :(");
    }
}