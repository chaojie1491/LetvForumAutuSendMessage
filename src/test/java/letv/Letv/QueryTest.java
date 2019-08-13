//package letv.Letv;
//
//import com.inamik.text.tables.GridTable;
//import com.inamik.text.tables.SimpleTable;
//import com.inamik.text.tables.grid.Border;
//import com.inamik.text.tables.grid.Util;
//import db.LetvLinkEntity;
//import db.LetvUserEntity;
//import entity.Status;
//import org.hibernate.Session;
//import org.junit.Test;
//import util.HibernateUtil;
//
//import java.util.List;
//
//public class QueryTest {
//
//    @Test
//    public void queryTest() {
//        Session session = HibernateUtil.currentSession();
//        List<LetvUserEntity> letvUserEntities = session.createQuery("from db.LetvUserEntity").list();
//        long finish = letvUserEntities.stream().filter(u -> u.getLetvUserStatus().equals(Status.TRUE.getCode())).count();
//        long newUser = letvUserEntities.stream().filter(u -> u.getLetvUserStatus().equals(Status.FALSE.getCode())).count();
//        SimpleTable simpleTable = new SimpleTable();
//        simpleTable.nextRow().nextCell().addLine("总数").nextCell().addLine("已发").nextCell().addLine("未发");
//        simpleTable.nextRow()
//                .nextCell().addLine(String.valueOf(letvUserEntities.size()))
//                .nextCell().addLine(String.valueOf(finish)).nextCell().addLine(String.valueOf(newUser));
//        GridTable gridTable = simpleTable.toGrid();
//        gridTable = Border.of(Border.Chars.of('+', '-', '|')).apply(gridTable);
//        System.out.println(Util.asString(gridTable));
//    }
//
//    @Test
//    public void querytest2() {
//        Session session = HibernateUtil.currentSession();
//        List<LetvLinkEntity> letvLinkEntities = session.createQuery("from db.LetvLinkEntity group by letvModuleId").list();
//        SimpleTable simpleTable = new SimpleTable();
//        simpleTable.nextRow().nextCell().addLine("板块");
//        letvLinkEntities.forEach(g -> {
//            simpleTable.nextRow().nextCell().addLine(String.valueOf(g.getLetvModuleId()));
//        });
//        GridTable gridTable = simpleTable.toGrid();
//        gridTable = Border.of(Border.Chars.of('+', '-', '|')).apply(gridTable);
//        System.out.println(Util.asString(gridTable));
//    }
//
//
//}
