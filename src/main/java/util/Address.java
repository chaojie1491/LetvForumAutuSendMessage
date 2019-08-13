package util;

public class Address {


    public static String getLoginUrl(String username, String password, String domain) {
        String loginUrl = "%s/member.php?mod=logging&action=login&loginsubmit=yes&handlekey=login&loginhash=Lkrb0&inajax=1&username=%s&password=%s";
        return String.format(loginUrl, domain, username, password);
    }

    public static String getLoginUrlBy17500() {
        return "https://passport.17500.cn/login/login.html";
    }

    public static String getFormHashBy17500() {
        return "https://bbs.17500.cn/home.php?mod=space&do=home";
    }

    public static String getMsgUrl(String touId, String msg) {
        String sendMsg = "https://8765138.com/home.php?mod=spacecp&ac=pm&op=send&touid=1&inajax=1";
        return String.format(sendMsg, touId, msg);
    }

    public static String Ping(Long uid) {
        String sendMsg = "https://8765138.com/home.php?mod=space&uid=%s";
        return String.format(sendMsg, uid);
    }

    public static String hello(String uid, String domain) {
        String helloUrl = "%s/home.php?mod=spacecp&ac=friend&op=add&uid=%s&inajax=1";

        return String.format(helloUrl, domain, uid);
    }

    public static String referer(String uid, String domain) {
        String referer = "%s/home.php?mod=space&uid=%s";
        return String.format(referer, domain, uid);
    }

    public static String getFormHash(String domain) {
        String hashUrl = "%s/home.php?mod=space&uid=1";
        return String.format(hashUrl, domain);
    }

    public static String getCount() {
        return "https://8765138.com/forum.php";
    }

    public static String getFollowAddress(String domain, String hash, String fid) {
        String url = "%s/home.php?mod=spacecp&ac=follow&op=add&hash=%s&fuid=%s&infloat=yes&handlekey=followmod&inajax=1&ajaxtarget=fwin_content_followmod";
        return String.format(url, domain, hash, fid);
    }

    public static String getAddFriend(String domian, String uid) {
        return String.format("%s/home.php?mod=spacecp&ac=friend&op=add&uid=%s&inajax=1", domian, uid);
    }

    public static String getLSTV(String uid) {
        String url = "https://bbs.le.com/home.php?mod=spacecp&ac=friend&op=add&uid=%s&inajax=1&handlekey=addfriendhk_%s&infloat=yes&handlekey=a_friend";
        return String.format(url,uid,uid);
    }
}
