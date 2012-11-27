/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vgmoose.anonymize;

import java.util.ArrayList;
import java.util.Collections;

import com.google.ads.*;

import android.app.Activity;
import android.app.AlertDialog;
//import android.content.Intent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;

import android.text.Spanned;
import android.text.TextWatcher;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.CheckBox;
import android.widget.LinearLayout;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public final class AnonymousCall extends Activity
{

	public static final String TAG = "EasyAnon";
	public static final String PREFS_NAME = "EasyAnon";

//	private Button mAddAccountButton;
	private ListView mContactList;
	private static boolean mSortAlts;
//	private CheckBox mShowInvisibleControl;
	private EditText mNarrowDown;
	private AnonymousCall instance;

	static MenuItem mens1;
	static MenuItem mens2;

	String shortcode;


	ArrayList<String> contactslist;
	ArrayList<Spanned> sortedlist;

	/**
	 * Called when the activity is first created. Responsible for initializing the UI.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.v(TAG, "Activity State: onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_manager);

		instance=this;

		SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
		shortcode = settings.getString("shortcode", "*67");
		mSortAlts = settings.getBoolean("reverse", false);



		// Obtain handles to UI objects
		//		mAddAccountButton = (Button) findViewById(R.id.addContactButton);
		mContactList = (ListView) findViewById(R.id.contactList);
		//		mShowInvisibleControl = (CheckBox) findViewById(R.id.showInvisible);
		mNarrowDown = (EditText) findViewById(R.id.search);

		// Fast scroll
		mContactList.setFastScrollEnabled(true); 

		//		mAddAccountButton.setText(mAddAccountButton.getText()+" ("+"*67"+")");

		mNarrowDown.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);


		// Initialize class properties
		//		mShowInvisibleControl.setChecked(mSortAlts);

		mNarrowDown.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) 
			{
				filterText();

			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			public void onTextChanged(CharSequence s, int start, int before, int count){}
		}); 

		// Register handler for UI elements
		//		mens2.setOnMenuItemClickListener(new View.OnClickListener() {
		//			public void onClick(View v) {
		//				Log.d(TAG, "mAddAccountButton clicked");
		//				//				launchContactAdder();
		//			}
		//		});

		mContactList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				callNumber(position);

			}});

		//		mShowInvisibleControl.setOnI can CheckedChangeListener(new OnCheckedChangeListener() {
		//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		//				Log.d(TAG, "mShowInvisibleControl changed: " + isChecked);
		//				mSortAlts = isChecked;
		//				populateContactList();
		//			}
		//		});


		// Admob Code
		// Create the adView

//		try
//		{
			AdView av = new AdView(this, AdSize.BANNER, "a150af20799cbaa");
			// Lookup your LinearLayout assuming it’s been given
			// the attribute android:id="@+id/mainLayout"
			LinearLayout layout = (LinearLayout)findViewById(R.id.bottom);
			// Add the adView to it
			//	    layout.addView(av);
			// Initiate a generic request to load it with an ad
			AdRequest adr = new AdRequest();
			adr.addTestDevice(AdRequest.TEST_EMULATOR);
			av.loadAd(adr);
			layout.addView(av);

//		}
//		catch (Exception e)
//		{
//
//		}


		//	    RelativeLayout layout2 = (RelativeLayout)findViewById(R.id.relish);


		// Populate the contact list
		populateContactList();
	}

	public void filterText()
	{
		sortedlist = new ArrayList<Spanned>();

		String searchQuery = mNarrowDown.getText().toString();

		if (!searchQuery.equals(""))
		{

			for (String name : contactslist)
			{

				if (name.toLowerCase().contains(searchQuery.toLowerCase()))
				{
					String newString = name.replaceAll("(?i)"+searchQuery, "<b><font color=#00FFFF>"+searchQuery+"</font></b>");
					newString = newString.replace("\n","<br>");

					sortedlist.add(Html.fromHtml(newString));
				}

			}
			ArrayAdapter<Spanned> arrayAdapter = new ArrayAdapter<Spanned>(instance,android.R.layout.simple_list_item_1, sortedlist);
			mContactList.setAdapter(arrayAdapter);
		}
		else populateContactList();
	}

	public void changeHeader()
	{
		int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
		TextView yourTextView = (TextView)findViewById(titleId);
		if (yourTextView==null) return;
			
		String oldstring = yourTextView.getText().toString();

		String s = "";

		for (char c : oldstring.toCharArray())
		{
			s+=c;
			if (c=='y' || c=='l' || c=='e') break;
		}
		yourTextView.setText(s+" "+shortcode);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			mSortAlts = !mSortAlts;
			populateContactList();
			return true;
		case 2:
			//			shortcode="#31#";

			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			final EditText input = new EditText(this);

			input.setText(shortcode);

			alert.setTitle("Change VSC");
			alert.setMessage("Enter a new Vertical Service Code.\nThis application will dial the code, then the selected contact.");
			alert.setView(input);

			alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String value = input.getText().toString();
					shortcode=value;
					if (value.equals(""))
						shortcode="*67";
					changeHeader();
				}
			});
			alert.show();
			return true;
		}
		return false;
	}


	/**
	 * Populate the contact list based on account currently selected in the account spinner.
	 */
	private void populateContactList() {

		contactslist = new ArrayList<String>();

		Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);

		while (phones.moveToNext())
		{
			Contact c = new Contact();
			c.setName(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
			c.setAltName(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_ALTERNATIVE)));
			c.setNumber(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
			//			c.setType(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

			//			if (phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER) == 1)
			contactslist.add(c.getName()+"\n"+c.getNumber());
		}

		Collections.sort(contactslist);

		ArrayAdapter<String> arrayAdapter =      
			new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, contactslist);

		if (!mNarrowDown.getText().toString().equals(""))
		{
			filterText();
			return;
		}

		mContactList.setAdapter(arrayAdapter);
	}

	/**
	 * Launches the ContactAdder activity to add a new contact to the selected accont.
	 */
	//	protected void launchContactAdder() {
	//		Intent i = new Intent(this, ContactAdder.class);
	//		startActivity(i);
	//	}

	public void callNumber(int num)
	{
		Intent callIntent = new Intent(Intent.ACTION_CALL);

		String s = contactslist.get(num);
		if (!mNarrowDown.getText().toString().equals(""))
			s = sortedlist.get(num).toString();

		boolean foundit = false;
		String t = "";

		for (char c : s.toCharArray())
		{
			if (foundit)
				t+=c;
			else
				if (c=='\n')
					foundit=true;
		}

		callIntent.setData(Uri.parse("tel:"+shortcode+t));
		startActivity(callIntent);

		//		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public static boolean sortAlt()
	{
		return mSortAlts;
	}

	@Override
	public void onStop()
	{
		super.onStop();

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		//		Log.v("exit","exit");
		editor.putBoolean("reverse",mSortAlts);
		editor.putString("shortcode",shortcode);

		editor.commit();

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		mens1 = menu.add(0, 1, 2, R.string.showInvisible);
		mens2 = menu.add(0,2,2, R.string.addContactButtonLabel);
		changeHeader();
		return true;
	}
}
