package com.wbx.utils;

import com.wbx.entity.FileProcessRecord;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    private FileUtils() {}

    public static List<FileProcessRecord> getAllFilePaths(String folderPath) {
        List<FileProcessRecord> recordList = new ArrayList<>();
        File folder = new File(folderPath);

        if (folder.exists() && folder.isDirectory()) {
            getAllFilePathsRecursive(folder, recordList);
        }

        return recordList;
    }

    private static void getAllFilePathsRecursive(File folder, List<FileProcessRecord> recordList) {
        File[] files = folder.listFiles();

        if (files != null) {
            for (File f : files) {
                if (f.isFile()) {
                    FileProcessRecord record = new FileProcessRecord();
                    record.setFileName(f.getName());
                    record.setPath(f.getAbsolutePath());
                    record.setStatus(2);
                    recordList.add(record);
                } else if (f.isDirectory()) {
                    getAllFilePathsRecursive(f, recordList);
                }
            }
        }
    }


}
