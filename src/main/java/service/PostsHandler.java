package service;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.concurrent.ExecutionException;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import db.LetvLinkEntity;
import entity.LinkItem;
import entity.Status;
import javafx.application.Platform;
import org.controlsfx.control.StatusBar;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Task;
import util.*;

import javax.xml.crypto.Data;

public class PostsHandler extends Task<List<LinkItem>> {

    static Logger logger = LoggerFactory.getLogger(PostsHandler.class);

    private List<LinkItem> linkList;
    private String rule;
    private boolean isAsc;
    private int mudule;
    private PageCountAdapter adapter;
    private Session session;
    private SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

    @Override
    protected List<LinkItem> call() {
        try {
            session = HibernateUtil.currentSession();
            updateMessage("启动");
            logger.info("===============================扫描链接线程启动==================================");
            String PageCountDoc = String.format(Global.FORUM, String.valueOf(mudule), 1);
            logger.info("鏈接：" + PageCountDoc);
            Connection connectionFirst = Jsoup.connect(PageCountDoc);
            connectionFirst.timeout(10000);
            connectionFirst.headers(JsoupUtil.getDefaultHeader());
            int pageCount = adapter.getPageCount(connectionFirst.get());
            linkList = new ArrayList<>();

            if (isAsc) {
                for (int i = 1; i < pageCount; i++) {
                    String cuttentUrlString = String.format(Global.FORUM, String.valueOf(mudule), i);
                    Connection connection = Jsoup.connect(cuttentUrlString);
                    connection.headers(JsoupUtil.getDefaultHeader());
                    try {
                        Document document = connection.get();
                        List<LinkItem> urlsList = getPageLinkList(document, rule);
                        linkList.addAll(urlsList);
                    } catch (Exception e) {
                        logger.info(e.getMessage());
                    }

                }
            } else {
                for (int i = pageCount; i >= 1; i--) {
                    String cuttentUrlString = String.format(Global.FORUM, String.valueOf(mudule), i);
                    Connection connection = Jsoup.connect(cuttentUrlString);
                    connection.headers(JsoupUtil.getDefaultHeader());
                    try {
                        Document document = connection.get();
                        List<LinkItem> urlsList = getPageLinkList(document, rule);
                        linkList.addAll(urlsList);
                    } catch (Exception e) {
                        logger.info(e.getMessage());
                    }

                }
            }

            List<LetvLinkEntity> letvLinkEntities = session.createQuery("from db.LetvLinkEntity").list();

            Transaction transaction = session.beginTransaction();

            for (int i = 0; i < linkList.size(); i++) {
                int finalI = i;
                if (letvLinkEntities.stream().noneMatch(l -> l.getLetvLink().equals(linkList.get(finalI).getLinkStr()))) {
                    logger.info("入库：postName：" + linkList.get(i).getLinkName() + "|" + linkList.get(i).getLinkStr());
                    LetvLinkEntity letvLinkEntity = new LetvLinkEntity();
                    letvLinkEntity.setLetvLinkId(snowflakeIdWorker.nextId());
                    letvLinkEntity.setLetvLink(linkList.get(i).getLinkStr());
                    letvLinkEntity.setLetvLinkName(linkList.get(i).getLinkName());
                    letvLinkEntity.setLetvLastTime(dateFormat.format(new Date()));
                    letvLinkEntity.setLetvModuleId(mudule);
                    letvLinkEntity.setLetvStatus(Status.FALSE.getCode());
                    session.save(letvLinkEntity);
                } else {
                    logger.info("已存在：postName：" + linkList.get(i).getLinkName() + "|" + linkList.get(i).getLinkStr());
                }
                if (i % 20 == 0) {

                    session.flush();

                    session.clear();

                    transaction.commit();

                    transaction = session.beginTransaction();

                }
            }

            NotificationUtil.notification("信息","链接扫描完毕","info");
            transaction.commit();
            HibernateUtil.clossSession();

        } catch (Exception io) {
            io.printStackTrace();
        }

        updateProgress(0, 0);
        done();
        return linkList;
    }

    public static class Builder {

        private String rule;
        private boolean isAsc;
        private int mudule;
        private PageCountAdapter adapter;

        public Builder() {
        }


        public Builder Rule(String rule) {
            this.rule = rule;
            return this;
        }

        public Builder AscOrDesc(boolean ascOrDesc) {
            isAsc = ascOrDesc;
            return this;
        }

        public Builder PageCount(PageCountAdapter adapter) {
            this.adapter = adapter;
            return this;
        }

        public Builder Mudule(int mudule) {
            this.mudule = mudule;
            return this;
        }

        public PostsHandler builder() {
            return new PostsHandler(this);
        }

    }

    private PostsHandler(Builder builder) {
        setAdapter(builder.adapter);
        setAsc(builder.isAsc);
        setMudule(builder.mudule);
        setRule(builder.rule);
    }

    private static List<LinkItem> getPageLinkList(Document document, String rule) {
        List<Element> elements = document.select(rule);
        List<LinkItem> postsList = new ArrayList<LinkItem>();
        elements.forEach(element -> {
            String linkString = Global.DOMAIN_STRING + "/" + element.attr("href");
            logger.info("已掃描：" + linkString);
            LinkItem item = new LinkItem();
            item.setLinkName(element.text());
            item.setLinkStr(linkString);
            postsList.add(item);
        });
        return postsList;
    }

    public List<LinkItem> getLinkList() {
        return linkList;
    }


    private void setRule(String rule) {
        this.rule = rule;
    }

    private void setAsc(boolean asc) {
        isAsc = asc;
    }

    private void setMudule(int mudule) {
        this.mudule = mudule;
    }

    private void setAdapter(PageCountAdapter adapter) {
        this.adapter = adapter;
    }


}
