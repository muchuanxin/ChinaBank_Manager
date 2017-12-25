package com.xd.aselab.chinabankmanager.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.xd.aselab.chinabankmanager.activity.MainActivity;
import com.xd.aselab.chinabankmanager.activity.MainActivity_all;
import com.xd.aselab.chinabankmanager.chat.ui.ChatActivity;
import com.xd.aselab.chinabankmanager.chat.ui.GroupChatActivity;
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.activity.NewNotificationDetailActivity;
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.activity.NewNotificationDetailNewActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "www";
    private String myExtra_str;
    private String type,groupID;
	private SharePreferenceUtil spu;
	private Handler handler;
	private Set<String> groupSet_add = new HashSet<String>() ;
	private Set<String> groupSet_exit = new HashSet<String>() ;

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.d(TAG,"MyReceiver----onReceive");
		try {
			Bundle bundle = intent.getExtras();
			Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

			if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
				String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
				Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
				//send the Registration Id to your server...

			} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
				Log.d(TAG, "extras 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
				processCustomMessage(context, bundle);

			} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
				Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
				int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
				Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
				String s = bundle.getString(JPushInterface.EXTRA_EXTRA);
				Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的EXTRA_EXTRA: " + s);
				JSONObject obj = new JSONObject(s);
				String info = obj.getString("extra");
				JSONObject json_extra = new JSONObject(info);
				try {
					type = json_extra.getString("message_type");
					groupID = json_extra.getString("group_id");
					Log.d(TAG, "NOTIFICATION_type---"+type);
				}catch(JSONException e){

				}
				if(Constants.CreateChatGroup.equals(type)||Constants.InviteMemberToGroup.equals(type)){
					Log.d(TAG, "新加进去的群---"+groupID);
					groupSet_add.add(groupID);
					JPushInterface.addTags(context,new Random().nextInt(),groupSet_add);
				}else if(Constants.DissolveChatGroup.equals(type)||Constants.ExpelMemberFromGroup.equals(type)){
					Log.d(TAG, "后退的群---"+groupID);
					groupSet_exit.add(groupID);
					JPushInterface.deleteTags(context,new Random().nextInt(),groupSet_exit);
				}

			} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
				Log.d(TAG, "[MyReceiver] 用户点击打开了通知");

				//打开自定义的Activity
				String s = bundle.getString(JPushInterface.EXTRA_EXTRA);
				Log.d(TAG, "JPushInterface.EXTRA_EXTRA---"+s);
				JSONObject obj = new JSONObject(s);
				String info = obj.getString("extra");
				JSONObject json_extra = new JSONObject(info);
				try {
					type = json_extra.getString("message_type");
					Log.d(TAG, "message_type---"+type);
				}catch(JSONException e){

				}
				// 在这里可以自己写代码去定义用户点击后的行为
				Bundle bundle2 = new Bundle();
				//bundle2.putString("taskId" , taskId);
				if(Constants.RECOMMEND.equals(type)){
					Intent i = new Intent(context, NewNotificationDetailNewActivity.class);  //自定义打开推荐分期的界面
					i.putExtras(bundle2);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					context.startActivity(i);
				}else if(Constants.CHAT.equals(type)){
					Log.e(TAG,"Constants.CHAT");
					Intent i = new Intent(context, ChatActivity.class);  //自定义打开单聊界面
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.putExtra("receiver",json_extra.getString("sender"));
					i.putExtra("receiver_name",json_extra.getString("sender_name"));
					i.putExtra("receiver_head",json_extra.getString("sender_head"));
					context.startActivity(i);
				}else if(Constants.GROUPCHAT.equals(type)){
					Log.e(TAG,"Constants.GROUPCHAT");
					Intent i = new Intent(context, GroupChatActivity.class);  //自定义打开群聊界面
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.putExtra("group_id",json_extra.getString("group_id"));
					i.putExtra("group_name",json_extra.getString("group_name"));
					i.putExtra("group_head",json_extra.getString("group_head"));
					Log.e("www","group_name1:---"+json_extra.getString("group_name"));
					//i.putExtras(bundle2);
					context.startActivity(i);
				}else if(Constants.CreateChatGroup.equals(type)){
					Log.e(TAG,"Constants.CreateChatGroup");
					Intent i = new Intent(context, GroupChatActivity.class);  //自定义打开群聊界面
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.putExtra("group_id",json_extra.getString("group_id"));
					i.putExtra("group_name",json_extra.getString("group_name"));
					i.putExtra("group_head",json_extra.getString("group_head"));
					Log.e("www","group_name2:---"+json_extra.getString("group_name"));
					//i.putExtras(bundle2);
					context.startActivity(i);
				}else if(Constants.InviteMemberToGroup.equals(type)){
					Log.e(TAG,"Constants.InviteMemberToGroup");
					Intent i = new Intent(context, GroupChatActivity.class);  //自定义打开群聊界面
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.putExtra("group_id",json_extra.getString("group_id"));
					i.putExtra("group_name",json_extra.getString("group_name"));
					i.putExtra("group_head",json_extra.getString("group_head"));
					Log.e("www","group_name3:---"+json_extra.getString("group_name"));
					//i.putExtras(bundle2);
					context.startActivity(i);
				}


			} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
				Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
				//在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

			} else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
				boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
				Log.d(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
			} else {
				Log.e(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
			}
		} catch (Exception e){

		}

	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
					Log.i(TAG, "This message has no Extra data");
					continue;
				}

				try {
					JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
					Iterator<String> it =  json.keys();

					while (it.hasNext()) {
						String myKey = it.next();
						sb.append("\nkey:" + key + ", value: [" +
								myKey + " - " +json.optString(myKey) + "]");
					}
				} catch (JSONException e) {
					Log.d(TAG, "Get message extra JSON error!");
				}

			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
	
	//send msg to MainActivity、NewNotificationDetailActivity
	private void processCustomMessage(Context context, Bundle bundle) {
		String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);

		if (MainActivity.isForeground) {
			Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
			if (extras!=null) {
				try {
					JSONObject extraJson = new JSONObject(extras);
					if (extraJson.length() > 0) {
						//将附加消息发送到首页
						msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
						Log.e(TAG,"----130----"+extras.replace("\\",""));
					}
				} catch (JSONException e) {

				}
			}
			LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
		}
		else if(ChatActivity.isForeground){
			Log.e("www", "聊天页面在前台-单聊");
			Intent msgIntent = new Intent(MainActivity.MESSAGE_UPDATE_MESSAGCHAT_ACTION);
			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
			msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
			context.sendBroadcast(msgIntent);
			LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
		}else if(GroupChatActivity.isForeground){
			Log.e("www", "聊天页面在前台-群聊");
			String message_type = "";
			String group_id = "";
			try {
				JSONObject obj = new JSONObject(extras);
				String extra_info = obj.getString("extra");
				JSONObject extra_json = new JSONObject(extra_info);
				message_type = extra_json.getString("message_type");
				group_id = extra_json.getString("group_id");
				Log.d(TAG, "群聊message_type---"+message_type);
			}catch(JSONException e){
				Log.e("www", "群聊bug");
			}
			if(Constants.DissolveChatGroup.equals(message_type)){
				Log.e("www", "---群聊---DissolveChatGroup---解散群之后给群成员推送 ");
				Intent msgIntent = new Intent(MainActivity.Dissolve_GROUP_MESSAGCHAT_ACTION);
				msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
				msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
				msgIntent.putExtra("group_id", group_id);
				Log.e("www", "myReceiver要解散的群----"+group_id+"-----");
				context.sendBroadcast(msgIntent);
				LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);

			}else if(Constants.GROUPCHAT.equals(message_type)){
				Intent msgIntent = new Intent(MainActivity.GROUP_MESSAGE_UPDATE_MESSAGCHAT_ACTION);
				msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
				msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
				Log.e("www", "---聊天页面在前台-群聊:---"+extras);
				context.sendBroadcast(msgIntent);
				LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
			}

		}

		else{
			Log.e("www", "没有需要刷新的界面在前台");
		}

	}

}
