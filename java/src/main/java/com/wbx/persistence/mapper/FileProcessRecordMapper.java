package com.wbx.persistence.mapper;

import com.wbx.entity.FileProcessRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FileProcessRecordMapper {

    List<FileProcessRecord> selectFileRecordList(Integer status);

    int insertRecord(FileProcessRecord fileProcessRecord);

    int updateStatus(@Param("fileName") String fileName,@Param("status") int status);

    int updateRecord(FileProcessRecord fileProcessRecord);

}