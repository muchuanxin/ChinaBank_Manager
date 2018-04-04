package com.xd.aselab.chinabankmanager.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2017/10/8.
 */

public class Constants {
    //upload_photo
    public final static int LOCAL_PHOTO = 3;
    public static final int TAKE_PICTURE = 0x000001;
    public static final int FROM_MAIN_ME_TO_LOGIN = 004;
    public static final int LOGIN_TO_MAIN_ME = 005;
    public static final int MIAN_ME_TO_INFO = 006;
    public static final int INFO_TO_MAIN = 007;
    public static final int LOGIN_TO_MAIN_HOME = 1001;
    public static final int EXIT_TO_LOGIN = 1002;
    //me_uploadHeadPhoto
    public static String uploadHeadPhoto = ConnectUtil.UploadHeadPhoto + "?";
    public static String ChangeGroupHeadPhoto = ConnectUtil.ChangeGroupHeadPhoto + "?";

    public static List<String> chooseSortList = new ArrayList<String>(Arrays.asList("推荐业务数降序", "成功业务数降序","成功金额数降序", "推荐成功率降序"));
    public static List<String> shengHangChooseSortList = new ArrayList<String>(Arrays.asList("成功业务数量降序", "成功业务金额降序"));
    public static List<String> chooseTimeList = new ArrayList<String>(Arrays.asList("近一周业绩", "近一月业绩","近一季度业绩", "近一年业绩"));
    public static List<String> shengHangChooseSortListcopy = new ArrayList<String>(Arrays.asList("确认业务数量降序","拒绝业务数量降序","备注业务数量降序","成功业务数量降序", "成功业务金额降序"));
    public static List<String> chooseRankList = new ArrayList<String>(Arrays.asList("确认业务数量降序","拒绝业务数量降序","备注业务数量降序","成功业务数降序", "成功金额数降序"));

    public static final int ChatToSelectPicture  = 19945;
    public static final int ChatToRecordVoice  = 19946;
    public static final int ChatToRecordVideo  = 19947;

    public static String RECOMMEND =  "RECOMMEND"; //推荐
    public static String CHAT = "SINGLECHAT"; //单聊
    public static String GROUPCHAT = "GROUPCHAT";//群聊
    public static String CreateChatGroup = "CreateChatGroup";//创建群之后给群成员推送的消息类型
    public static String DissolveChatGroup = "DissolveChatGroup";//解散群之后给群成员推送的消息类型
    public static String InviteMemberToGroup = "InviteMemberToGroup";//群主拉人给被拉的人的推送
    public static String ExpelMemberFromGroup = "ExpelMemberFromGroup";//群主踢人给被踢的人的推送


    public static final int ActivityCompatRequestPermissionsCode = 1003;

}
