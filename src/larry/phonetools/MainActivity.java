package larry.phonetools;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends FragmentActivity {

	public static final String TAG = MainActivity.class.getName();
	private int mThemePos = R.style.LightTheme;
	int[] mThemes = new int[] { R.style.LightTheme, R.style.ClassicTheme,
			R.style.DarkTheme };
	Handler sHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sHandler = new Handler();
		mThemePos = getIntent().getIntExtra("theme", 0);
		setTheme(mThemes[mThemePos]);
		setContentView(R.layout.activity_main);

		Spinner theme = (Spinner) findViewById(R.id.main_top_theme);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.theme, R.layout.list_item_spinner);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		theme.setAdapter(adapter);
		theme.setSelection(mThemePos);
		theme.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view, int pos,
					long id) {
				changeTheme(pos);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});

		Fragment f = HomeFragment.newInstance();
		addFrament(f, false);
	}

	private void addFrament(Fragment fragment, boolean back) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.setCustomAnimations(R.anim.fragment_slide_left_enter,
				R.anim.fragment_slide_left_exit);
		if (back)
			ft.addToBackStack(null);
		ft.add(R.id.main_fragment, fragment).commit();

	}

	private void showDialog(DialogFragment fragment) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		// ft.setCustomAnimations(R.anim.translucent_zoom_in,
		// R.anim.translucent_zoom_exit);
		ft.addToBackStack(null);
		fragment.show(ft, "dialog");
	}

	/*
	 * @see android.support.v4.app.FragmentActivity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		back(null);
	}

	public void back(View view) {
		FragmentManager fm = getSupportFragmentManager();
		int count = fm.getBackStackEntryCount();
		if (count > 0) {
			fm.popBackStack(fm.getBackStackEntryAt(count - 1).getId(),
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
		} else {
			finish();
		}
	}

	private void replaceFrament(Fragment fragment, boolean back) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		if (back)
			ft.addToBackStack(null);
		ft.replace(R.id.main_fragment, fragment).commit();
	}



	public void showMessage(View view) {
		Fragment f = MessageFragment.newInstance();
		replaceFrament(f, true);
	}


	public void changeTheme(int pos) {
		if (pos == mThemePos)
			return;
		Intent i = new Intent(this, MainActivity.class);
		i.putExtra("theme", pos);
		startActivity(i);
		finish();
	}
}
