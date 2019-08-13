package service;

import cn.hutool.core.util.StrUtil;
import db.LetvLinkEntity;
import db.LetvUserEntity;
import entity.Status;
import entity.UserItem;
import javafx.concurrent.Task;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import util.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static service.PostsHandler.logger;


/**
 * 乐视TV 论坛 Service
 *
 * @author Administrator
 */
public class ScanUserService extends Task<Void> {

    private UserLinkFilter userLinkFilter;
    private List<LetvLinkEntity> urls;
    private Session session = HibernateUtil.currentSession();
    private List<LetvUserEntity> userEntities;
    private List<LetvUserEntity> newUsers;
    private SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();

    public ScanUserService(UserLinkFilter userLinkFilter, List<LetvLinkEntity> urls) {
        this.userLinkFilter = userLinkFilter;
        this.urls = urls;
        this.newUsers = new ArrayList<>();
        this.userEntities = session.createQuery("from db.LetvUserEntity").list();
    }

    @Override
    protected Void call() {
        try {
            for (LetvLinkEntity url : urls) {
                try {
                    updateMessage("启动");
                    Connection connection = Jsoup.connect(url.getLetvLink());
                    connection.headers(JsoupUtil.getDefaultHeader());
                    connection.timeout(2000);
                    logger.info(url.getLetvLink());
                    Document document = connection.get();
                    String pageCount = UserFilterUtil.getPageCount(document);
                    if (StrUtil.isEmpty(pageCount)) {
                        insertUser(document);
                    } else {
                        for (int j = 1; j < Integer.valueOf(pageCount); j++) {
                            if (j == 1) {
                                insertUser(document);
                            } else {
                                logger.info("多页面 :" + UserFilterUtil.getCurrentPage(String.valueOf(j), url.getLetvLink()));
                                Connection connectionPage = Jsoup.connect(UserFilterUtil.getCurrentPage(String.valueOf(j), url.getLetvLink()));
                                Document document1 = connectionPage.get();
                                insertUser(document1);
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            List<LetvUserEntity> filter = newUsers.stream().collect(
                    Collectors.collectingAndThen(
                            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(LetvUserEntity::getLetvUserUid))), ArrayList::new)
            );
            Transaction scanUserTransaction = session.getTransaction();
            for (int i = 0; i < filter.size(); i++) {
                int finalI = i;
                if (userEntities.stream().noneMatch(ou -> ou.getLetvUserUid().equals(filter.get(finalI).getLetvUserUid()))) {


                    if (!scanUserTransaction.isActive()) {
                        scanUserTransaction = session.beginTransaction();
                    }

                    filter.get(i).setLetvUserId(snowflakeIdWorker.nextId());
                    session.save(filter.get(i));


                    if (i % 20 == 0) {

                        session.flush();

                        session.clear();

                        scanUserTransaction.commit();

                        scanUserTransaction = session.beginTransaction();

                    }



                } else {
                    logger.info("用户存在:" + filter.get(i).getLetvUserLink());
                }
            }
            scanUserTransaction.commit();
            HibernateUtil.clossSession();
            logger.info("共扫描：" + newUsers.size() + "个用户");

            logger.info("过滤后：" + filter.size() + "个用户");


            updateProgress(0, 0);
            done();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void insertUser(Document document) {
        Elements elements = document.select(Global.userClass);
        elements.forEach(e -> {
            LetvUserEntity userEntity = new LetvUserEntity();
            String uid = userLinkFilter.filterLink(e.attr("href"));
            if (UserFilterUtil.isNumeric(uid)) {
                userEntity.setLetvUserStatus(Status.FALSE.getCode());
                userEntity.setLetvUserLink(e.text());
                userEntity.setLetvUserUid(userLinkFilter.filterLink(e.attr("href")));
                newUsers.add(userEntity);
            }
        });
    }
}


//    Transaction scanUserTransaction = session.beginTransaction();
//                logger.info("用户已扫描：" + u.getUserlink());
//                        if (userEntities.stream().noneMatch(ou -> ou.getLetvUserUid().equals(u.getUserId()))) {
//                        logger.info("用户入库:" + u.getUserlink());
//                        LetvUserEntity userEntity = new LetvUserEntity();
//                        userEntity.setLetvUserId(snowflakeIdWorker.nextId());
//                        userEntity.setLetvUserStatus(Status.FALSE.getCode());
//                        userEntity.setLetvUserLink(u.getUserlink());
//                        userEntity.setLetvUserUid(u.getUserId());
//                        session.save(userEntity);
//                        scanUserTransaction.commit();
//                        } else {
//                        logger.info("用户存在:" + u.getUserlink());
//                        }

//    ScanUserService scanUserService = new ScanUserService(new LetvFilterUserId(), item.getLinkStr());
//                                        new Thread(scanUserService).start();
//                                                scanUserService.setOnSucceeded(task -> {
//                                                logger.info("OK");
//                                                });
