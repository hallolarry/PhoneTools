package larry.phonetools;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.util.Log;

public class SMSHelper {
	final static String SMS_URI_ALL = "content://sms/";
	final static String SMS_URI_INBOX = "content://sms/inbox";
	final static String SMS_URI_SEND = "content://sms/sent";
	final static String SMS_URI_DRAFT = "content://sms/draft";
	final static String SMS_URI_OUTBOX = "content://sms/outbox";
	final static String SMS_URI_FAILED = "content://sms/failed";
	final static String SMS_URI_QUEUED = "content://sms/queued";

	public static String showSms(Context ctx) {
		StringBuilder smsBuilder = new StringBuilder();
		try {
			Uri uri = Uri.parse(SMS_URI_ALL);
			String[] projection = new String[] { "_id", "thread_id", "address",
					"person", "body", "date", "type" };
			Cursor cur = ctx.getContentResolver().query(uri, projection, null,
					null, "date desc"); // 获取手机内部短信

			if (cur.moveToFirst()) {
				int indexID = cur.getColumnIndex("_id");
				int indexThreadID = cur.getColumnIndex("thread_id");
				int indexAddress = cur.getColumnIndex("address");
				int indexPerson = cur.getColumnIndex("person");
				int indexBody = cur.getColumnIndex("body");
				int indexDate = cur.getColumnIndex("date");
				int indexType = cur.getColumnIndex("type");

				do {
					Long id = cur.getLong(indexID);
					Long threadID = cur.getLong(indexThreadID);
					String strAddress = cur.getString(indexAddress);
					int intPerson = cur.getInt(indexPerson);
					String strbody = cur.getString(indexBody);
					long longDate = cur.getLong(indexDate);
					int intType = cur.getInt(indexType);

					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd hh:mm:ss");
					Date d = new Date(longDate);
					String strDate = dateFormat.format(d);

					String strType = "";
					if (intType == 1) {
						strType = "接收";
					} else if (intType == 2) {
						strType = "发送";
					} else {
						strType = "null";
					}

					smsBuilder.append("[ ");
					smsBuilder.append(id + ", ");
					smsBuilder.append(threadID + ", ");
					smsBuilder.append(strAddress + ", ");
					smsBuilder.append(intPerson + ", ");
					smsBuilder.append(strType + ", ");
					smsBuilder.append(strDate + ", ");
					smsBuilder.append(strbody);
					smsBuilder.append(" ]\n\n");
				} while (cur.moveToNext());

				if (!cur.isClosed()) {
					cur.close();
					cur = null;
				}
			} else {
				smsBuilder.append("no result!");
			} // end if

			smsBuilder.append("getSmsInPhone has executed!");

		} catch (SQLiteException ex) {
			Log.d("SQLiteException in getSmsInPhone", ex.getMessage());
		}

		return smsBuilder.toString();
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
			Log.d("deleteSMS", "count:: " + c);
			return c;
		} catch (Exception e) {
			Log.d("deleteSMS", "Exception:: " + e);
		}
		return 0;
	}
}
