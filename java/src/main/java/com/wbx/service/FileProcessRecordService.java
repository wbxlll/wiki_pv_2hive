package com.wbx.service;

import com.wbx.entity.FileProcessRecord;
import com.wbx.persistence.MySQLSessionManager;
import com.wbx.persistence.mapper.FileProcessRecordMapper;
import org.apache.ibatis.session.SqlSession;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FileProcessRecordService {

    private FileProcessRecordService() {
    }

    public static List<FileProcessRecord> selectList(Integer status){
        SqlSession session = MySQLSessionManager.getSession();
        FileProcessRecordMapper mapper = session.getMapper(FileProcessRecordMapper.class);
        List<FileProcessRecord> recordList = mapper.selectFileRecordList(status);
        MySQLSessionManager.returnSession(session);
        return recordList;
    }

    public static Set<String> selectGzFileNameSet(Integer status){
        SqlSession session = MySQLSessionManager.getSession();
        FileProcessRecordMapper mapper = session.getMapper(FileProcessRecordMapper.class);
        List<FileProcessRecord> recordList = mapper.selectFileRecordList(status);
        MySQLSessionManager.returnSession(session);
        return recordList.stream().map(file -> file.getFileName() + ".gz").collect(Collectors.toSet());
    }


    public static void insert(FileProcessRecord fileRecord){
        SqlSession session = MySQLSessionManager.getSession();
        FileProcessRecordMapper mapper = session.getMapper(FileProcessRecordMapper.class);
        mapper.insertRecord(fileRecord);
        MySQLSessionManager.returnSession(session);
    }

    public static void batchInsert(List<FileProcessRecord> recordList){
        SqlSession session = MySQLSessionManager.getBatchSession();
        FileProcessRecordMapper mapper = session.getMapper(FileProcessRecordMapper.class);
        recordList.forEach(mapper::insertRecord);
        session.commit();
        session.clearCache();
        MySQLSessionManager.returnSession(session);
    }

    public static void update(String fileName, int status){
        SqlSession session = MySQLSessionManager.getSession();
        FileProcessRecordMapper mapper = session.getMapper(FileProcessRecordMapper.class);
        mapper.updateStatus(fileName, status);
        MySQLSessionManager.returnSession(session);
    }

    public static void update(FileProcessRecord fileProcessRecord){
        SqlSession session = MySQLSessionManager.getSession();
        FileProcessRecordMapper mapper = session.getMapper(FileProcessRecordMapper.class);
        mapper.updateRecord(fileProcessRecord);
        MySQLSessionManager.returnSession(session);
    }

}
