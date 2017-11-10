package cn.neocross.libs.common.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 自定义HTTP请求参数键值对容器
 * 如果传入值value为空，自动处理为空字符串
 * Created by nodlee on 2016/4/28.
 */
public class QueryString {

    private StringBuilder query = new StringBuilder();

    /**
     * 添加键值对
     *
     * @param name  键
     * @param value 值
     */
    public synchronized void add(String name, String value) {
        query.append("&");
        encode(name, value);
    }

    /**
     * url URLEncoder 编码
     *
     * @param name  键
     * @param value 值
     */
    private synchronized void encode(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("键name不能为空");
        }
        // 容错处理
        if (value == null) {
            value = "";
        }
        try {
            query.append(URLEncoder.encode(name, "UTF-8"));
            query.append("=");
            query.append(value.length() == 0 ? value : URLEncoder.encode(value, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private synchronized String getQuery() {
        if (query.length() > 0) {
            query.replace(0, 1, "?");
        }
        return query.toString();
    }

    /**
     * 获取query结果
     *
     * @return query
     */
    @Override
    public String toString() {
        return getQuery();
    }
}
