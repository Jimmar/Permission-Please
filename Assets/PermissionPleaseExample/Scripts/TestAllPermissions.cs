using System;
using System.Collections;
using System.Collections.Generic;
using Plugins.Android.PermissionPlease;
using UnityEngine;
using UnityEngine.UI;

public class TestAllPermissions : MonoBehaviour
{
	public Button baseButton;
	public Transform content;
	
	// Use this for initialization
	void Start ()
	{
		foreach (PermissionPlease.AndroidPermission value in Enum.GetValues(typeof(PermissionPlease.AndroidPermission)))
		{
			PermissionPlease.AndroidPermission permision = value;
			
			Button button = Instantiate(baseButton, content);
			button.GetComponentInChildren<Text>().text = permision.ToString();
			button.onClick.AddListener(() => PermissionPlease.GrantPermission(permision, null, true));
			button.gameObject.SetActive(true);
		}
	}
}
