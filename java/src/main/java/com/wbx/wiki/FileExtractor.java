package com.wbx.wiki;

import com.wbx.entity.FileProcessRecord;
import com.wbx.service.FileProcessRecordService;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static com.wbx.constant.Constants.*;

public class FileExtractor {

    public static void extractFiles(){
        List<FileProcessRecord> downloadedFile = FileProcessRecordService.selectList(1);
        for(FileProcessRecord file : downloadedFile){
            String path = extract(file.getPath());
            if(StringUtils.isNotBlank(path)){
                file.setPath(path);
                file.setStatus(2);
                FileProcessRecordService.update(file);
            }
        }
    }

    public static String extract(String gzFilePath){
        String[] nameParts = gzFilePath.split("-");
        //理论上在数组的第三部分
        String day = nameParts[2];
        if(StringUtils.isNotBlank(day)){
            return extractGzipFile(gzFilePath, FULL_PV_PATH + day);
        }
        return "";
    }

    public static String extractGzipFile(String compressedFilePath, String destinationDirectory){
        try {
            File compressedFile = new File(compressedFilePath);
            // 创建目标目录
            File destinationDir = new File(destinationDirectory);
            if (!destinationDir.exists()) {
                destinationDir.mkdirs();
            }
            // 创建输入流
            try (GZIPInputStream gzipInputStream = new GZIPInputStream(Files.newInputStream(compressedFile.toPath()))) {
                // 构建目标文件路径
                String destinationFilePath = destinationDirectory + "/" + getFileNameWithoutExtension(compressedFile.getName());
                System.out.println("开始解压文件：" + compressedFilePath);
                Files.copy(gzipInputStream, Paths.get(destinationFilePath), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("完成解压文件：" + compressedFilePath);
                return destinationFilePath;
            }
        }catch (Exception e){
            System.out.println("文件解压过程出现异常：" + compressedFilePath);
        }
        return "";
    }

    public static String getFileNameWithoutExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex != -1) {
            return fileName.substring(0, dotIndex);
        }
        return fileName;
    }

    public static void main(String[] args) {
        extractFiles();
    }

}
