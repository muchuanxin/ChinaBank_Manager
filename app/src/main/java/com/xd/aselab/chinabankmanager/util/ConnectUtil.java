package com.xd.aselab.chinabankmanager.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 发起HTTP请求的工具类
 */
public class ConnectUtil {
	private static String DLTAG = "com.xd.connect.ConnectUtil";

	public static final String HOST_ERROR = "HOST_ERROR";
	public static String HTTP_RE_ERROE_CODE = "";

	public static final String UTF_8 = "UTF-8";
	public static final String POST = "POST";
	public static final String GET = "GET";

	private final static int READ_TIMEOUT = 3000;
	private final static int CONNECT_TIMEOUT = 3000;
	private final static int TEST_TIMEOUT = 3000;

	public final static String HTTP = "http://";
	public final static String HTTPS = "https://";

	//public final static String API_HOST = "http://117.34.117.107/ChinaBank/Manager/";
//	public final static String IP = "http://47.93.31.35/";
	public final static String IP = "http://192.168.0.129/";
	//public final static String IP = "http://192.168.0.73:8080/";
	//public final static String IP = "http://10.170.13.113:8080/";
	//public final static String IP = "http://117.34.117.107/";
	//public final static String IP = "http://juyuntechinfo.gotoip1.com/";
	//public final static String IP = "http://www.juyunjinrongapp.com/";
	//public final static String IP = "http://javaapp.gotoip2.com/";
	public final static String SERVER = IP +"ChinaBank/";
	public final static String API_HOST = SERVER+"Manager/";
    public final static String API_Chat = SERVER+"Chat/";

	public final static String API_HOST_IMAGE=API_HOST+"images/";
    public final static String API_HOST_DOWNLOAD=API_HOST+"download/";

    public final static String Chat=SERVER+"Chat/";

    public final static String CreateChatGroup=Chat+"CreateChatGroup";
    public final static String ExpelMemberFromGroup=Chat+"ExpelMemberFromGroup";
    public final static String DissolveChatGroup=Chat+"DissolveChatGroup";
    public final static String LeaveChatGroup=Chat+"LeaveChatGroup";
    public final static String GetGroupMember=Chat+"GetGroupMember";
    public final static String GetMyCreateGroup=Chat+"GetMyCreateGroup";
    public final static String GetMyJoinGroup=Chat+"GetMyJoinGroup";
    public final static String InviteMemberToGroup=Chat+"InviteMemberToGroup";
    public final static String ChangeGroupHeadPhoto=Chat+"ChangeGroupHeadPhoto";

    public final static String COMMON = SERVER + "Common/";
    public final static String CHINA_BANK_BENEFIT = COMMON + "GetActivity";
    public final static String CHINA_BANK_NETWORK = COMMON + "GetBankInformation";
    public final static String GetRollingPicture = COMMON + "GetRollingPicture";

	public static String IS_AVAILABLE = API_HOST+"ManagerLogin";
	public static String MANAGER_LOGIN = API_HOST+"ManagerLogin";
	public static String CHANGE_PASSWORD = API_HOST+"ReviseManagerPassword";
	public static String CHANGE_TELEPHONE = API_HOST+"ReviseManagerTeleNumber";
	public static String MY_CONTACT = API_HOST+"GetMyBoss";
	public static String MY_PERFORMANCE = API_HOST+"GetManagerPerformance";
	public static String PERFORMANCE_RANKING_CARD = API_HOST+"GetManagerRankingByCards";
	public static String PERFORMANCE_RANKING_SHOP = API_HOST+"GetManagerRankingByShops";
	public static String BASE_INFO = API_HOST+"GetAllBasicManager";
	public static String SHOP_INFO = API_HOST+"GetShopPerformance";
	public static String RELEASE_SHOP = API_HOST+"RelieveShop";
	public static String SHOP_JOIN_LIST = API_HOST+"GetJoinShopList";
	public static String SHOP_RELEASE_LIST = API_HOST+"GetRelieveShopList";
	public static String AGREE_JOIN = API_HOST+"JoinShop";
	public static String REFUSE_JOIN = API_HOST+"RefuseShop";
	public static String RefuseRelieveShop = API_HOST+"RefuseRelieveShop";
	public static String SetSecureQuestion = API_HOST+"SetManagerSecureQuestion";
	public static String GetSecureQuestion = API_HOST+"GetManagerSecureQuestion";

	//二期新增
	public static String UploadHeadPhoto = COMMON+"UploadHeadPhoto";//上传头像
	public static String GetInstallmentMyPerformance = API_HOST+"GetInstallmentMyPerformance";//卡分期--我的业绩
	public static String GetInstallmentWorkerPerformanceDetail = API_HOST+"GetInstallmentWorkerPerformanceDetail";//卡分期--推广员业绩详情
	public static String GetInstallmentJoinWorker = API_HOST+"GetInstallmentJoinWorker";//卡分期——获取待加盟审核的推广员
	public static String AgreeInstallmentJoinWorker = API_HOST+"AgreeInstallmentJoinWorker";//卡分期--同意加盟
	public static String RefuseInstallmentJoinWorker = API_HOST+"RefuseInstallmentJoinWorker";//卡分期--拒绝加盟
	public static String GetInstallmentReleaseWorker = API_HOST+"GetInstallmentReleaseWorker";//卡分期——获取待解约审核的推广员
	public static String AgreeInstallmentReleaseWorker = API_HOST+"AgreeInstallmentReleaseWorker";//卡分期--同意解约
	public static String RefuseInstallmentReleaseWorker = API_HOST+"RefuseInstallmentReleaseWorker";//卡分期--拒绝解约
	public static String GetInstallmentWorkerRecommendList = API_HOST+"GetInstallmentWorkerRecommendList";//卡分期--获取某个推广员推荐的业务
	public static String ConfirmInstallmentRecommend = API_HOST+"ConfirmInstallmentRecommend";//卡分期--确认或拒绝该笔推荐的业务
	public static String GetInstallmentWorkerPerformance= API_HOST+"GetInstallmentWorkerPerformance";//卡分期--4s店销售业绩（前台处理四种排序）
	public static String GetInstallmentWorker = API_HOST+"GetInstallmentWorker";
	public static String PublishAnnouncement = API_HOST+"PublishAnnouncement";
	public static String AddInstallmentRecommendRemark = API_HOST+"AddInstallmentRecommendRemark";//卡分期--添加备注
	public static String InstallmentActivelyRelieveWorker = API_HOST+"InstallmentActivelyRelieveWorker";
	public static String GetInstallmentBasicPerformance = API_HOST+"GetInstallmentBasicPerformance";//卡分期--基层经理业绩（排名）
	public static String GetInstallmentWorkerRecommendDetail = API_HOST+"GetInstallmentWorkerRecommendDetail";
	public static String GetMarketingGuide = API_HOST+"GetMarketingGuide";//营销导航
	public static String GetLoanBasicPerformance= API_HOST+"GetLoanBasicPerformance";

	//微聊
	public static String SingleChat = API_Chat+"SingleChat";
	public static String GetHistoryChatLog = API_Chat+"GetHistoryChatLog";
	public static String GroupChat = API_Chat+"GroupChat";
	public static String GetGroupHistoryChatLog = API_Chat+"GetGroupHistoryChatLog";


	//省行管理者
	public static String GetInstallmentSecondaryBankPerformace=API_HOST+"GetInstallmentSecondaryBankPerformace";
	public static String GetInstallmentBossManagerPerformace=API_HOST+"GetInstallmentBossManagerPerformace";
	public static String ForceReleaseBetweenBankAnd4SShop=API_HOST+"ForceReleaseBetweenBankAnd4SShop";
    public static String GetInstallment4SShopPerformace=API_HOST+"GetInstallment4SShopPerformace";

	public static String ForceReleaseBetweenBankAnd4SShopByID=API_HOST+"ForceReleaseBetweenBankAnd4SShopByID";
	public static String GetInstallmentWorkerPerformanceIn4SShop=API_HOST+"GetInstallmentWorkerPerformanceIn4SShop";



	public static String httpRequest(String url, PostParameter[] postParams, String httpMethod) {
		InputStream input = null;
		String jsonSource = null;
		try {
			HttpURLConnection conn = null;
			OutputStream output = null;
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setDoInput(true);
            // Log.i("mmm", "conn.getResponseCode():" + conn.getResponseCode());
            if (null != postParams || POST.equals(httpMethod)) {
                conn.setRequestMethod(POST);
                conn.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				conn.setRequestProperty("Authorization","Basic c2l0ZWFkbWluOjExMA==");
                conn.setRequestProperty("Charset", "UTF-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECT_TIMEOUT);
                String postParam = "";
                if (postParams != null) {
                    postParam = encodeParameters(postParams);
                }
                byte[] bytes = postParam.getBytes(UTF_8);
                conn.setRequestProperty("Content-Length",
                        Integer.toString(bytes.length));

                Log.i("mcx", "url----" + conn.getURL() + "?" + postParam);
                output = conn.getOutputStream();
                output.write(bytes);
                output.flush();
                output.close();
            }
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                jsonSource="";
                input = conn.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(
                        input, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(
                        inputStreamReader);
                String temp = "";

                while ((temp = bufferedReader.readLine()) != null) {
                    jsonSource += temp;
                }
                bufferedReader.close();
                input.close();
                Log.i("www", "jsonString=" + jsonSource);
            } else {
                HTTP_RE_ERROE_CODE = String.valueOf(conn.getResponseCode());
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return jsonSource;
		}

	}

	public static String encodeParameters(PostParameter[] postParams) {
		StringBuffer buf = new StringBuffer();
		for (int j = 0; j < postParams.length; j++) {
			if (j != 0) {
				buf.append("&");
			}
			try {
				if (null != postParams[j]) {
					if (null != postParams[j].getKey()) {
						buf.append(
								URLEncoder.encode(postParams[j].getKey(),
										"utf-8")).append("=");
					}
					if (null != postParams[j].getValue()) {
						buf.append(URLEncoder.encode(postParams[j].getValue(),
								"utf-8"));
					}
				}
			} catch (java.io.UnsupportedEncodingException neverHappen) {
			}
		}
		return buf.toString();
	}

	public static String getEncode(String codeType, String content) {
		try {
			MessageDigest digest = MessageDigest.getInstance(codeType);
			digest.reset();
			digest.update(content.getBytes());
			StringBuilder builder = new StringBuilder();
			for (byte b : digest.digest()) {
				builder.append(Integer.toHexString((b >> 4) & 0xf));
				builder.append(Integer.toHexString(b & 0xf));
			}
			return builder.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connect = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// System.out.println("connect: " + connect);
		if (connect == null) {
			return false;
		} else// get all network info
		{
			NetworkInfo[] info = connect.getAllNetworkInfo();
			// System.out.println("info: " + info);
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static InputStream httpRequest2(String url,
			PostParameter[] postParams, String httpMethod) {
		InputStream is = null;
		try {
			HttpURLConnection con = null;
			OutputStream osw = null;
			try {
				con = (HttpURLConnection) new URL(url).openConnection();
				con.setDoInput(true);
				if (null != postParams || POST.equals(httpMethod)) {
					con.setRequestMethod(POST);
					con.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded");
					con.setDoOutput(true);
					con.setReadTimeout(READ_TIMEOUT);
					con.setConnectTimeout(CONNECT_TIMEOUT);

					String postParam = "";
					if (postParams != null) {
						postParam = encodeParameters(postParams);
					}
					byte[] bytes = postParam.getBytes("UTF-8");

					con.setRequestProperty("Content-Length",
							Integer.toString(bytes.length));

					Log.i("liuhaoxian", "url----" + con.getURL() + "?"
							+ postParam);
					osw = con.getOutputStream();
					osw.write(bytes);
					osw.flush();
					osw.close();
				}

				if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
					is = con.getInputStream();
				} else {
					HTTP_RE_ERROE_CODE = String.valueOf(con.getResponseCode());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			return is;
		}
	}
	
	/**
  	 * 给服务器发送参数，不管返回
  	 */
  	public static void httpPost(String url, PostParameter[] postParams,String httpMethod) {
        try {
            HttpURLConnection con = null;
            OutputStream osw = null;
            try {
                con = (HttpURLConnection) new URL(url).openConnection();
                con.setDoInput(true);
                if (null != postParams || POST.equals(httpMethod))
                {
                   con.setRequestMethod(POST);
                   con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                   con.setDoOutput(true);
                   con.setReadTimeout(READ_TIMEOUT);
                   con.setConnectTimeout(CONNECT_TIMEOUT);
                   
                   String postParam = "";
                   if (null!=postParams) {
                	   //将参数进行编码防止中文乱码问题
                        postParam = encodeParameters(postParams);
                   }
                   byte[] bytes = postParam.getBytes("UTF-8");
                   con.setRequestProperty("Content-Length", Integer.toString(bytes.length));
                   osw = con.getOutputStream();
                   osw.write(bytes);
                   osw.flush();
                   osw.close();
                }
				if (con.getResponseCode() == HttpURLConnection.HTTP_OK)
				{
				}
            }
            catch (Exception e){
                e.printStackTrace();
            }finally {}
        }catch (Exception e){
            e.printStackTrace();
        }
    }
  	
	/**
	 * 验证服务器是否可用
	 */
	public static boolean isHostAvailable()
	{
		boolean net = false;
		HttpURLConnection con;
		int responseCode = -1;
		try {
			con = (HttpURLConnection) new URL(IS_AVAILABLE).openConnection();
			con.setReadTimeout(TEST_TIMEOUT);
            con.setConnectTimeout(TEST_TIMEOUT);
			con.setDoInput(true);
			responseCode = con.getResponseCode();
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(HttpURLConnection.HTTP_OK==responseCode)
		{
			net = true;
		}
		return net;
	}

}
