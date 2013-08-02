package larry.phonetools;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SMSHelper {
	final static String MMS_SMS_ALL = "content://mms-sms/conversations";
	final static String SMS_URI_ALL = "content://sms/";
	final static String SMS_URI_INBOX = "content://sms/inbox";
	final static String SMS_URI_SEND = "content://sms/sent";
	final static String SMS_URI_DRAFT = "content://sms/draft";
	final static String SMS_URI_OUTBOX = "content://sms/outbox";
	final static String SMS_URI_FAILED = "content://sms/failed";
	final static String SMS_URI_QUEUED = "content://sms/queued";
	private static final String TAG = SMSHelper.class.getName();

	public static void showSms(Context ctx) {
		try {
			Uri uri = Uri.parse(MMS_SMS_ALL);

			Cursor cur = ctx.getContentResolver().query(uri, null, null, null,
					null); // 获取手机内部短信

			if (cur.moveToFirst()) {
				int indexID = cur.getColumnIndex("_id");
				int indexThreadID = cur.getColumnIndex("thread_id");
				int indexAddress = cur.getColumnIndex("address");
				int indexPerson = cur.getColumnIndex("person");
				int indexType = cur.getColumnIndex("type");

				do {
					Long id = cur.getLong(indexID);
					Long threadID = cur.getLong(indexThreadID);
					String strAddress = cur.getString(indexAddress);
					int intPerson = cur.getInt(indexPerson);
					int intType = cur.getInt(indexType);

					String strType = "";
					if (intType == 1) {
						strType = "接收";
					} else if (intType == 2) {
						strType = "发送";
					} else {
						strType = "null";
					}

					StringBuilder smsBuilder = new StringBuilder();
					smsBuilder.append("[ ");
					smsBuilder.append(id + ", ");
					smsBuilder.append(threadID + ", ");
					smsBuilder.append(strAddress + ", ");
					smsBuilder.append(intPerson + ", ");
					smsBuilder.append(strType + ", ");
					smsBuilder.append(" ]\n\n");
					Log.d(TAG, smsBuilder.toString());
				} while (cur.moveToNext());

				if (!cur.isClosed()) {
					cur.close();
					cur = null;
				}
			} else {
			}
		} catch (SQLiteException ex) {
			Log.d(TAG, ex.getMessage());
		}

	}

	public static int delete(Context ctx, String[] tags) {
		int count = 0;
		try {
			ContentResolver cr = ctx.getContentResolver();
			Uri uri = Uri.parse(MMS_SMS_ALL);

			if (tags == null || tags.length == 0) {
				return cr.delete(uri, null, null);
			}

			String[] projection = new String[] { "_id", "address", "ct_t" };

			Cursor cur = cr.query(uri, projection, null, null, null); // 获取手机内部短信

			if (cur.moveToFirst()) {
				int ind_id = cur.getColumnIndex("_id");
				int ind_type = cur.getColumnIndex("ct_t");
				int ind_address = cur.getColumnIndex("address");

				do {
					Long id = cur.getLong(ind_id);
					String type = cur.getString(ind_type);
					String address = cur.getString(ind_address);
					if ("application/vnd.wap.multipart.related".equals(type)) {
						// it's MMS
						address = getAddressNumber(ctx, id);
					}

					if (!stringInArray(tags, address)) {
						int c = cr.delete(uri, "_id=" + id, null);
						count += c;
					}

				} while (cur.moveToNext());

				if (!cur.isClosed()) {
					cur.close();
					cur = null;
				}
			} else {
			} // end if

		} catch (SQLiteException ex) {
			Log.d(TAG, ex.getMessage());
		}

		return count;
	}

	private static String getAddressNumber(Context ctx, Long id) {
		String selectionAdd = new String("msg_id=" + id);
		String uriStr = MessageFormat.format("content://mms/{0}/addr", id);
		Uri uriAddress = Uri.parse(uriStr);
		Cursor cAdd = ctx.getContentResolver().query(uriAddress, null,
				selectionAdd, null, null);
		String name = null;
		if (cAdd.moveToFirst()) {
			do {
				String number = cAdd.getString(cAdd.getColumnIndex("address"));
				if (number != null) {
					try {
						Long.parseLong(number.replace("-", ""));
						name = number;
					} catch (NumberFormatException nfe) {
						if (name == null) {
							name = number;
						}
					}
				}
			} while (cAdd.moveToNext());
		}
		if (cAdd != null) {
			cAdd.close();
		}
		return name;
	}

	public static int showColumn(Context ctx) {
		int count = 0;
		try {
			Uri uri = Uri.parse(MMS_SMS_ALL);
			ContentResolver cr = ctx.getContentResolver();
			Cursor cur = cr.query(uri, null, null, null, null);
			if (cur.moveToFirst()) {
				count = cur.getColumnCount();
				for (int i = 0; i < count; i++) {
					Log.d(TAG, cur.getColumnName(i));
				}
			}
		} catch (SQLiteException ex) {
			Log.d(TAG, ex.getMessage());
		}

		return count;
	}

	private static boolean stringInArray(String[] array, String tag) {
		if (tag == null || array == null || array.length == 0)
			return false;
		for (String s : array) {
			if (tag.indexOf(s) >= 0) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Delete all SMS one by one
	 */
	public static int deleteSMS(Context ctx, String[] tags) {
		try {
			ContentResolver cr = ctx.getContentResolver();
			String[] projection = new String[] { "_id", "thread_id", "address",
					"person", "body", "date", "type" };
			// Query SMS
			Uri uri = Uri.parse(SMS_URI_ALL);
			if (tags == null || tags.length == 0) {
				int c = cr.delete(uri, null, null);
				return c;
			}
			StringBuilder where = new StringBuilder("address=? ");
			for (int i = 1; i < tags.length; i++) {
				where.append("or address =? ");
			}
			int c = cr.delete(uri, where.toString(), tags);
			Log.d(TAG, "count:: " + c);
			return c;
		} catch (Exception e) {
			Log.d(TAG, "Exception:: " + e);
		}
		return 0;
	}
}
