package com.wbx.wiki;

import com.wbx.entity.FileProcessRecord;
import com.wbx.service.FileProcessRecordService;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wbx.constant.Constants.*;

public class WikiPageViewFileDownloader {

    public static void checkNewFiles(){
        Set<String> fileURLs = hrefExtractor(getPageInfo());
        if(fileURLs.isEmpty()){
            throw new RuntimeException("该页面没有文件");
        }
        Set<String> fileNames = FileProcessRecordService.selectGzFileNameSet(null);
        for (String url : fileURLs){
            if(!fileNames.contains(url)){
                if(USE_PROXY){
                    downloadFileWithProxy(FULL_URL + url, FULL_DOWNLOAD_PATH + url);
                }else {
                    downloadFile(FULL_URL + url, FULL_DOWNLOAD_PATH + url);
                }
                FileProcessRecord fileProcessRecord = new FileProcessRecord();
                fileProcessRecord.setFileName(FileExtractor.getFileNameWithoutExtension(url));
                fileProcessRecord.setPath(FULL_DOWNLOAD_PATH + url);
                fileProcessRecord.setStatus(1);
                FileProcessRecordService.insert(fileProcessRecord);
            }
        }
    }

    public static String getPageInfo(){
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()){
            // 创建HttpGet请求对象
            final HttpGet httpGet = new HttpGet(FULL_URL);
            // 发送请求并获取响应
            CloseableHttpResponse response = httpClient.execute(httpGet);
            // 从响应中获取响应体
            HttpEntity entity = response.getEntity();
            // 读取响应内容
            String responseText = EntityUtils.toString(entity, "UTF-8");
            // 关闭响应体
            EntityUtils.consume(entity);
            // 读取响应内容
            return responseText;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Set<String> hrefExtractor(String response){
        Set<String> hrefSet = new HashSet<>();
        // 正则表达式匹配<a href>标签
        String regex = "<a\\s+href=\"(pageviews[^\"]+)\">";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(response);

        // 遍历匹配结果
        while (matcher.find()) {
            String href = matcher.group(1);
            hrefSet.add(href);
        }
        return hrefSet;
    }

    public static void downloadFile(String fileUrl, String filePath){
        try {
            System.out.println("开始下载文件：" + fileUrl);
            URL url = new URL(fileUrl);
            URLConnection connection = url.openConnection();
            try (InputStream in = new BufferedInputStream(connection.getInputStream())) {
                Path path = Paths.get(filePath);
                Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("文件下载完毕：" + fileUrl);
        }catch (Exception e){
            throw new RuntimeException("下载文件时发生异常！fileUrl=" + fileUrl);
        }
    }

    public static void downloadFileWithProxy(String fileUrl, String filePath) {
        try {
            System.out.println("开始下载文件（代理）：" + fileUrl);
            URL url = new URL(fileUrl);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_HOST, PROXY_PORT));
            URLConnection connection = url.openConnection(proxy);
            try (InputStream in = new BufferedInputStream(connection.getInputStream())) {
                Path path = Paths.get(filePath);
                Files.createDirectories(path);
                Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("文件下载完毕（代理）：" + fileUrl);
        }catch (Exception e){
            throw new RuntimeException("下载（代理）文件时发生异常！fileUrl=" + fileUrl);
        }
    }


    public static void main(String[] args) {
        checkNewFiles();
        FileExtractor.extractFiles();
    }

}
