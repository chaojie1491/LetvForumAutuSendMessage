//package letv.Letv;
//
//import java.io.IOException;
//import java.sql.SQLException;
//
//import db.LetvCookieEntity;
//import org.hibernate.Session;
//import org.hibernate.Transaction;
//import org.hibernate.query.Query;
//import org.junit.Test;
//import util.HibernateUtil;
//import util.SnowflakeIdWorker;
//import util.SqliteUtil;
//
//public class DBTest {
//
//
//
//	@Test
//	public void dbCreateTest() {
//		try {
//			SqliteUtil.createDatabases();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	@Test
//	public void hibernateQueryTest(){
//		Session session = HibernateUtil.currentSession();
//
//		Query<LetvCookieEntity> cookieEntities = session.createQuery("from db.LetvCookieEntity");
//		System.out.println(cookieEntities.list().size());
//	}
//
//	@Test public void hibernateQueryAddTest(){
//		SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();
//		Session session = HibernateUtil.currentSession();
//		Transaction transaction  = session.beginTransaction();
//		LetvCookieEntity cookieEntity = new LetvCookieEntity();
//		cookieEntity.setCookieId(snowflakeIdWorker.nextId());
//		cookieEntity.setCookieKey("key");
//		cookieEntity.setCookieValue("value");
//		session.save(cookieEntity);
//		transaction.commit();
//	}
//}
