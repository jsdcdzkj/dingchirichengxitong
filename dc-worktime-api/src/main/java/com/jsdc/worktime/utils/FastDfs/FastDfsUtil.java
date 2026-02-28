package com.jsdc.worktime.utils.FastDfs;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.resource.InputStreamResource;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.jsdc.worktime.sys_.SysFile;
import com.jsdc.worktime.utils.AjaxJson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * FastDfs文件服务器
 */
@Component
public class FastDfsUtil {
    @Value("${fastDfs_url}")
    private String upload_url;


    /**
     * 像fastdfs发送请求
     *
     * @param url
     * @param params
     * @return
     */
    private JSONObject sendPost(String url, HashMap params) {
        String result = HttpUtil.post(upload_url + url, params);
        return JSONObject.parseObject(result);
    }

    //上传文件不直接入file库 返回服务器路径
    public Map<String, String> uploadFileOther(MultipartFile multipartFile, String uploadPath) {
        //文件名
        String fileName = multipartFile.getOriginalFilename();
        //后缀名
        String fileSuffix = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
        HashMap<String, Object> paramMap = new HashMap<>();
        //文件
        InputStreamResource file = null;
        HashMap<String, String> map = new HashMap<>();
        try {
            file = new InputStreamResource(multipartFile.getInputStream(),
                    multipartFile.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
        }
        paramMap.put("file", file);
        //输出
        paramMap.put("output", "json");
        if (isImageByData(multipartFile)) {
            //自定义路径
            paramMap.put("path", "picture" + uploadPath);
            map.put("fileType", "1");
        } else {
            //自定义路径
            paramMap.put("path", "file" + uploadPath);
            map.put("fileType", "2");
        }
        //场景
        paramMap.put("scene", "picture");
        //文件名
        paramMap.put("filename", IdUtil.simpleUUID() + fileSuffix);
        //上传
        JSONObject result = sendPost(FastDfsUrl.UPLOAD_URL, paramMap);
        if ("fail".equals(result.getString("status"))) {
            return new HashMap<>();
        }
        map.put("contentType", multipartFile.getContentType());
        map.put("fileUrl", result.getString("path").substring(1));
        map.put("fileName", fileName);
        map.put("fileSize", multipartFile.getSize() + "");
        map.put("fileMd5", result.getString("md5"));
        return map;
    }

    public static boolean isImageByData(MultipartFile file) {
        try {
            // 尝试使用 ImageIO 读取文件输入流
            BufferedImage image = ImageIO.read(file.getInputStream());
            return image != null;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 上传图片
     *
     * @param params
     * @return
     */
    public AjaxJson uploadPicFile(FastDfsParams params) {
        MultipartFile multipartFile = params.getFile();
        //文件名
        String fileName = multipartFile.getOriginalFilename();
        //后缀名
        String fileSuffix = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
        HashMap<String, Object> paramMap = new HashMap<>();
        //文件
        InputStreamResource file = null;
        try {
            file = new InputStreamResource(multipartFile.getInputStream(),
                    multipartFile.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
        }
        paramMap.put("file", file);
        //输出
        paramMap.put("output", "json");
        //自定义路径
        paramMap.put("path", "picture/" + params.getUploadPath());
        //场景
        paramMap.put("scene", "picture");
        //文件名
        paramMap.put("filename", IdUtil.simpleUUID() + fileSuffix);
        //上传
        JSONObject result = sendPost(FastDfsUrl.UPLOAD_URL, paramMap);
        SysFile sysFile = new SysFile();
        if ("fail".equals(result.getString("status"))) {
            return AjaxJson.getError(result.getString("message"));
        } else {
            sysFile.setId(IdUtil.simpleUUID());
            sysFile.setFileName(fileName);
            sysFile.setContentType(multipartFile.getContentType());
            sysFile.setFileType("1");
            sysFile.setFileSize(multipartFile.getSize() + "");
            sysFile.setFileUrl(result.getString("path"));
            sysFile.setUploadTime(DateUtil.formatDateTime(new Date()));
            sysFile.setBizType(params.getBizType());
            sysFile.setBizId(params.getBizId());
            sysFile.setFileMd5(result.getString("md5"));
            sysFile.insert();
        }
        return AjaxJson.getSuccessData(sysFile);
    }

    /**
     * 上传文件
     *
     * @param params
     * @return
     */
    public AjaxJson uploadFile(FastDfsParams params) {
        MultipartFile multipartFile = params.getFile();
        //文件名
        String fileName = "";
        if (StrUtil.isNotEmpty(params.getFileName())) {
            fileName = params.getFileName();
        } else {
            fileName = multipartFile.getOriginalFilename();
        }

        //后缀名
        String fileSuffix = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
        HashMap<String, Object> paramMap = new HashMap<>();
        //文件
        InputStreamResource file = null;
        try {
            file = new InputStreamResource(multipartFile.getInputStream(),
                    multipartFile.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
        }
        paramMap.put("file", file);
        //输出
        paramMap.put("output", "json");
        //自定义路径
        paramMap.put("path", "file/" + params.getUploadPath());
        //场景
        paramMap.put("scene", "file");
        //文件名
        paramMap.put("filename", IdUtil.simpleUUID() + fileSuffix);
        //上传
        JSONObject result = sendPost(FastDfsUrl.UPLOAD_URL, paramMap);
        SysFile sysFile = new SysFile();
        if ("fail".equals(result.getString("status"))) {
            return AjaxJson.getError(result.getString("message"));
        } else {
            sysFile.setId(IdUtil.simpleUUID());
            sysFile.setFileName(fileName);
            sysFile.setContentType(multipartFile.getContentType());
            sysFile.setFileType("2");
            sysFile.setFileSize(multipartFile.getSize() + "");
            sysFile.setFileUrl(result.getString("path"));
            sysFile.setUploadTime(DateUtil.formatDateTime(new Date()));
            sysFile.setBizType(params.getBizType());
            sysFile.setBizId(params.getBizId());
            sysFile.setFileMd5(result.getString("md5"));
            sysFile.insert();
        }
        return AjaxJson.getSuccessData(sysFile);
    }


    /**
     * 删除文件
     *
     * @param sysFile
     * @return
     */
    public AjaxJson deleteFile(SysFile sysFile) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("path", sysFile.getFileUrl());
        JSONObject result = sendPost(FastDfsUrl.DELETE_URL, paramMap);
        if ("fail".equals(result.getString("status"))) {
            return AjaxJson.getError(result.getString("message"));
        } else {
            sysFile.deleteById();
            return AjaxJson.getSuccessData(sysFile);
        }
    }

    //将MultipartFile转化为File类的方法
    public static File MultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File toFile = null;
        if (!multipartFile.equals("") && !(multipartFile.getSize() <= 0)) {
            InputStream ins = multipartFile.getInputStream();
            toFile = new File(multipartFile.getOriginalFilename());
            inputStreamToFile(ins, toFile);
            ins.close();
        }
        return toFile;
    }

    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
