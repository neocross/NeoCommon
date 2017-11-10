package cn.neocross.libs.common.http;

import android.graphics.Bitmap;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.neocross.libs.common.utils.ImageUtils;

import static cn.neocross.libs.common.LogHelper.LOGE;
import static cn.neocross.libs.common.LogHelper.LOGW;

/**
 * * 基本网络请求类
 * <p>
 * 使用方法：
 * step 1 : 实例化类;
 * step 2 : 调用公共方法
 * Created by nodlee on 2016/4/28.
 */
public class HttpManager {

    private static final String TAG = "HttpManager";
    // 建立连接最大等待时间
    private static final int CONNECT_TIMEOUT = 15 * 1000;
    // 读取数据最大等待时间
    private static final int READ_TIMEOUT = 10 * 1000;
    // Http请求内容编码格式
    private static final String CHARSET = "UTF-8";

    private byte[] connect(String urlSpec, String query) {
        if (TextUtils.isEmpty(urlSpec)) return null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlSpec);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            // connection.setDoInput(true);
            // connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept-Charset", CHARSET);
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=" + CHARSET);
            if (query != null) {
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
                bos.write(query.getBytes(CHARSET));
                bos.flush();
                bos.close();
            }
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                LOGW(TAG, "Http请求失败，响应码code=" + connection.getResponseCode());
                return null;
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            int byteReaded = 0;
            byte[] buffer = new byte[1024];
            while ((byteReaded = in.read(buffer)) > 0) {
                bos.write(buffer, 0, byteReaded);
            }
            //in.close();
            bos.close();
            return bos.toByteArray();
        } catch (IOException e) {
            LOGE(TAG, "Http请求失败，错误信息：" + e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    /**
     * 上传多张图片
     *
     * @param requestUrl 请求URL
     * @param files      图片资源
     * @return string
     */
    public String uploadMultipleImage(String requestUrl, String[] files) {
        String LINEND = "\r\n";
        String BOUNDARY = "---------------------------7df2ad12508cc"; // 数据分隔线
        String PREFIX = "--";
        String MUTIPART_FORMDATA = "multipart/form-data";
        String CHARSET = "utf-8";
        String CONTENTTYPE = "application/octet-stream";
        HttpURLConnection urlConn = null;
        try {
            URL url = new URL(requestUrl);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoOutput(true);
            urlConn.setDoInput(true);
            urlConn.setUseCaches(false);
            urlConn.setConnectTimeout(CONNECT_TIMEOUT); // 设置连接超时时间
            urlConn.setReadTimeout(READ_TIMEOUT); // 读取超时
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Charset", CHARSET);
            urlConn.setRequestProperty("connection", "Keep-Alive");
            urlConn.setRequestProperty("Content-Type", MUTIPART_FORMDATA + ";boundary=" + BOUNDARY);
            DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());
            // 构建图片表单数据
            for (String filePath : files) {
                if (filePath == null) continue;
                File file = new File(filePath);
                if (!file.exists()) continue;
                String sb = "" + PREFIX + BOUNDARY + LINEND +
                        "Content-Disposition: form-data;"
                        + " name=\"" + file.getName() + "\";" + " filename=\""
                        + file.getName() + "\"" + LINEND +
                        "Content-Type:" + CONTENTTYPE + ";"
                        + "charset=" + CHARSET + LINEND +
                        LINEND;
                dos.write(sb.getBytes());
                // 输出方式一：无损
//              FileInputStream fis = new FileInputStream(new File(filePath));
//              byte[] buffer = new byte[10000];
//              int len = 0;
//              while ((len = fis.read(buffer)) != -1) {
//                  dos.write(buffer, 0, len);
//              }

                // 输出方式二：压缩图片
                Bitmap bitmap = ImageUtils.createBitmap(filePath, 600, 400);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, dos);
                dos.write(LINEND.getBytes());
//              fis.close();
                bitmap.recycle();
            }

            // 请求的结束标志
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
            dos.write(end_data);
            dos.flush();
            dos.close();
            // 发送请求数据结束

            // 接收返回信息
            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                LOGE(TAG, "上传失败，HTTP请求失败，响应吗:" + urlConn.getResponseCode());
                return null;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    urlConn.getInputStream()));

            StringBuilder result = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (Exception e) {
            LOGE(TAG, "上传失败，HTTP请求失败，错误信息" + e);
        } finally {
            if (urlConn != null) {
                urlConn.disconnect();
            }
        }
        return null;
    }

    /**
     * Post请求
     *
     * @param url   url
     * @param query query
     * @return string
     */
    public String doPost(String url, String query) {
        byte[] responseData = connect(url, query);

        if (responseData != null && responseData.length > 0) {
            return new String(responseData);
        } else {
            return null;
        }
    }

    /**
     * Get 请求
     *
     * @param url url
     * @return string
     */
    public String doGet(String url) {
        byte[] responseData = connect(url, null);

        if (responseData != null && responseData.length > 0) {
            return new String(responseData);
        } else {
            return null;
        }
    }
}
