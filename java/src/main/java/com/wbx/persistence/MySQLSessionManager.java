package com.wbx.persistence;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.*;

import java.io.InputStream;

public class MySQLSessionManager {

    public static final String RESOURCE = "mybatis-config.xml";

    private MySQLSessionManager() {
    }

    public static SqlSession getSession() {

        try {
            InputStream inputStream = Resources.getResourceAsStream(RESOURCE);
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            return sqlSessionFactory.openSession(true);
        }catch (Exception e){
            throw new RuntimeException("获取MySQL配置文件异常");
        }
    }

    public static SqlSession getBatchSession() {
        try {
            InputStream inputStream = Resources.getResourceAsStream(RESOURCE);
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            return sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        }catch (Exception e){
            throw new RuntimeException("获取MySQL配置文件异常");
        }
    }

    public static void returnSession(SqlSession session) {
        if(session != null){
            session.close();
        }
    }

}
