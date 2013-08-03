package in.co.praveenkumar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import in.co.praveenkumar.MyLocation.LocationResult;
import in.co.praveenkumar.lifeguard360.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class LifeGuardActivity extends Activity {
	LocationManager locationManager;
	String Location = "";
	String Lat = "";
	String Lng = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button helpMeButton = (Button) findViewById(R.id.helpMeButton);
		helpMeButton.setOnClickListener(helpMeButtonListener);
	}

	protected boolean isRouteDisplayed() {
		return false;
	}

	private OnClickListener helpMeButtonListener = new OnClickListener() {
		public void onClick(View v) {

			LocationResult locationResult = new LocationResult() {
				@Override
				public void gotLocation(Location location) {
					// Got the location!
					AppPreferences appPrefs;
					appPrefs = new AppPreferences(getApplicationContext());
					getAddress(location);
					Log.d("Location value", Location);
					String message = appPrefs.getPrefs("message") + "\n" + "BestKnownLocation : \n"
							+ Location;
					for (int i = 1; i < 6; i++) {
						String contactNumber = appPrefs.getPrefs("contact" + i);
						if (contactNumber.compareTo("") != 0)
							sendSMS(contactNumber, message);
					}
				}
			};
			MyLocation myLocation = new MyLocation();
			myLocation.getLocation(getApplicationContext(), locationResult);

		}
	};

	public void getAddress(Location location) {
		Geocoder geocoder = new Geocoder(getApplicationContext(),
				Locale.getDefault());
		Lat = location.getLatitude() + "";
		Lng = location.getLongitude() + "";
		try {
			List<Address> listAddresses = geocoder.getFromLocation(
					location.getLatitude(), location.getLongitude(), 1);
			if (null != listAddresses && listAddresses.size() > 0) {
				Location = listAddresses.get(0).getAddressLine(0) + "\n"
						+ listAddresses.get(0).getAddressLine(1) + "\n"
						+ listAddresses.get(0).getAddressLine(2) + "\n"
						+ "Latitude : " + Lat + "\n" + "Longitude : " + Lng;
				Toast.makeText(getBaseContext(), Location, Toast.LENGTH_SHORT)
						.show();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendSMS(String phoneNumber, String message) {
		Toast.makeText(getBaseContext(),
				"Sending a message to \n" + phoneNumber, Toast.LENGTH_SHORT)
				.show();
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, null, null);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.Settings:
			Intent i = new Intent(this, settings.class);
			startActivityForResult(i, 11);
			break;
		case R.id.About:
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.about);
			dialog.setTitle("About LifeGuard360");
			dialog.show();
			break;
		}
		return true;
	}
}