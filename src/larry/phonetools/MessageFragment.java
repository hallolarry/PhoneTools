package larry.phonetools;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MessageFragment extends Fragment {

	protected static final String TAG = MessageFragment.class.getName();

	Handler mHandler;

	public static MessageFragment newInstance() {
		MessageFragment f = new MessageFragment();
		f.mHandler = new Handler();
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
		filter = filter.trim();
		String[] tags = filter == null || filter.length() == 0 ? null : filter
				.split(" ");
		final int c = SMSHelper.delete(getActivity(), tags);
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getActivity(), "已删除" + c + "条",
						Toast.LENGTH_LONG).show();
			}
		});
	}
}
