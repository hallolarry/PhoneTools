package larry.phonetools;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class MessageFragment extends Fragment {

	protected static final String TAG = MessageFragment.class.getName();

	public static MessageFragment newInstance() {
		MessageFragment f = new MessageFragment();
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_message, null);
		final EditText mCoinViews = (EditText) v
				.findViewById(R.id.message_edittext0);
		Button mButton = (Button) v.findViewById(R.id.message_button);
		mButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				deleteMessage(mCoinViews.getText().toString());
			}
		});

		return v;
	}

	private void deleteMessage(String filter) {
		String[] tags = filter.split(",");
		SMSHelper.deleteSMS(getActivity(), tags);
		Log.d(TAG, tags.length + "");
	}
}
