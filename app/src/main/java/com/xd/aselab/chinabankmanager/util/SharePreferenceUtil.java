package com.xd.aselab.chinabankmanager.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtil {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public SharePreferenceUtil(Context context, String file) {
        sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    // 账号
    public void setAccount(String account) {
        editor.putString("account", account);
        editor.commit();
    }

    public String getAccount() {
        return sp.getString("account", "");
    }

    // 密码  存储的是没加密的
    public void setPassword(String password) {
        editor.putString("password", password);
        editor.commit();
    }

    public String getPassword() {
        return sp.getString("password", "");
    }

    // 经理类型
    public void setType(String type) {
        editor.putString("type", type);
        editor.commit();
    }

    public String getType() {
        return sp.getString("type", "");
    }
    //二级行管理者是MANAGER
    //银行卡客户经理是BASIC

    // Cookie
    public void setCookie(String cookie) {
        editor.putString("cookie", cookie);
        editor.commit();
    }

    public String getCookie() {
        return sp.getString("cookie", "");
    }

    // 工号
    public void setJobNumber(String jobNumber) {
        editor.putString("jobNumber", jobNumber);
        editor.commit();
    }

    public String getJobNumber() {
        return sp.getString("jobNumber", "");
    }

    // 姓名
    public void setName(String name) {
        editor.putString("name", name);
        editor.commit();
    }

    public String getName() {
        return sp.getString("name", "");
    }

    // 性别
    public void setSex(String sex) {
        editor.putString("sex", sex);
        editor.commit();
    }

    public String getSex() {
        return sp.getString("sex", "");
    }

    // 年龄
    public void setAge(String age) {
        editor.putString("age", age);
        editor.commit();
    }

    public String getAge() {
        return sp.getString("age", "");
    }

    // 生日
    public void setBirth(String birthday) {
        editor.putString("birthday", birthday);
        editor.commit();
    }

    public String getBirth() {
        return sp.getString("birthday", "");
    }

    // 电话
    public void setTel(String telephone) {
        editor.putString("telephone", telephone);
        editor.commit();
    }

    public String getTel() {
        return sp.getString("telephone", "");
    }

    // 地址
    public void setAddress(String address) {
        editor.putString("address", address);
        editor.commit();
    }

    public String getAddress() {
        return sp.getString("address", "");
    }

    // QQ
    public void setQQ(String qq) {
        editor.putString("qq", qq);
        editor.commit();
    }

    public String getQQ() {
        return sp.getString("qq", "");
    }

    // Mail
    public void setMail(String mail) {
        editor.putString("mail", mail);
        editor.commit();
    }

    public String getMail() {
        return sp.getString("mail", "");
    }

    // 座机
    public void setLandlineNumber(String landlineNumber) {
        editor.putString("landlineNumber", landlineNumber);
        editor.commit();
    }

    public String getLandlineNumber() {
        return sp.getString("landlineNumber", "");
    }

    // BOSS账号
    public void setBossAccount(String bossAccount) {
        editor.putString("bossAccount", bossAccount);
        editor.commit();
    }

    public String getBossAccount() {
        return sp.getString("bossAccount", "");
    }

    // 2级支行
    public void setBranchLevel2(String branchLevel2) {
        editor.putString("branchLevel2", branchLevel2);
        editor.commit();
    }

    public String getBranchLevel2() {
        return sp.getString("branchLevel2", "");
    }

    // 4级支行
    public void setBranchLevel4(String branchLevel4) {
        editor.putString("branchLevel4", branchLevel4);
        editor.commit();
    }

    public String getBranchLevel4() {
        return sp.getString("branchLevel4", "");
    }

    // 头像URL
    public void setPhotoUrl(String url) {
        editor.putString("photo", url);
        editor.commit();
    }

    public String getPhotoUrl() {
        return sp.getString("photo", "");
    }

    public void setGroupHeadUrl(String url) {
        editor.putString("groupHead", url);
        editor.commit();
    }

    public String getGroupHeadUrl() {
        return sp.getString("groupHead", "");
    }

    // 是否第一次运行本应用
    public void setIsFirst(boolean isFirst) {
        editor.putBoolean("isFirst", isFirst);
        editor.commit();
    }

    public boolean getisFirst() {
        return sp.getBoolean("isFirst", true);
    }

    // 清空存储
    public void clear() {
        editor.clear();
        editor.commit();
    }

    //是否登录
    public void setIsLogin(boolean isLogin) {
        editor.putBoolean("isLogin", isLogin);
        editor.commit();
    }

    public boolean getisLogin() {
        return sp.getBoolean("isLogin", false);
    }

    public String getExtras() {
        return sp.getString("extras", "");
    }

    public void setExtras(String extras) {
        editor.putString("extras", extras);
        editor.commit();
    }
    //聊天参数
    public void setChatExtra(String chatExtra) {
        editor.putString("chatExtra", chatExtra);
        editor.commit();
    }
    public String getChatExtra() {
        return sp.getString("chatExtra", "");
    }

    //聊天参数
    public void setGroupChatExtra(String groupChatExtra) {
        editor.putString("groupChatExtra", groupChatExtra);
        editor.commit();
    }
    public String getGroupChatExtra() {
        return sp.getString("groupChatExtra", "");
    }

    //地图marker缓存
    public void setMarkerCache(String markerCache) {
        editor.putString("markerCache", markerCache);
        editor.commit();
    }
    public String getMarkerCache() {
        return sp.getString("markerCache", "");
    }

    //判断是否是可抢单客户经理
    public void setGrab(String grab) {
        editor.putString("grab", grab);
        editor.commit();
    }
    public String getGrab() { return sp.getString("grab", ""); }

}
