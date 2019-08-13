package service;

import cn.hutool.core.util.StrUtil;
import db.LetvConfigEntity;
import db.LetvCookieEntity;
import db.LetvUserEntity;
import entity.Status;
import javafx.concurrent.Task;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddFriendService extends Task<Void> {

    private String message;

    private Logger logger = LoggerFactory.getLogger(AddFriendService.class);

    private String formHash;


    @Override
    protected Void call() throws Exception {
        try {
            Session session = HibernateUtil.currentSession();
            List<LetvCookieEntity> cookieEntities = session.createQuery("from db.LetvCookieEntity").list();

            Query<LetvUserEntity> query = session.createQuery("from db.LetvUserEntity where letvUserStatus=:status");
            query.setParameter("status", Status.FALSE.getCode());
            List<LetvUserEntity> letvUserEntities = query.list();
            List<LetvConfigEntity> letvConfigEntities = session.createQuery("from db.LetvConfigEntity").list();
            message = letvConfigEntities.get(0).getConfigMessageText();

            Map<String, String> cookies = new HashMap<>();
            cookieEntities.forEach(cookie -> {
                if (cookie.getCookieKey().equals("formHash")) {
                    formHash = cookie.getCookieValue();
                } else {
                    cookies.put(cookie.getCookieKey(), cookie.getCookieValue());
                }
            });
            if (StrUtil.isEmpty(formHash)) {
                NotificationUtil.notification("錯誤", "請輸入formHash值", "error");
                return null;
            } else {
                letvUserEntities.forEach(user -> {
                    logger.info("用戶:" + user.getLetvLink());
                    try {
                        SendMessageService.hello(cookies, user.getLetvUserUid(), message, Global.DOMAIN_STRING, formHash);
                        Transaction transaction = session.beginTransaction();
                        Query updateLink = session.createQuery("update db.LetvUserEntity set letvUserStatus = :status where letvUserId=" + user.getLetvUserId());
                        updateLink.setParameter("status", Status.TRUE.getCode());
                        updateLink.executeUpdate();
                        transaction.commit();
                        session.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            updateProgress(0, 0);
            done();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
