package cn.neocross.libs.common.router.action;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 路由动作请求结果的封装
 * Created by shenhua on 2017-09-08-0008.
 * Email shenhuanet@126.com
 */
public class RouterActionResult {

    public static final int CODE_SUCCESS = 0x0000;// 成功
    public static final int CODE_ERROR = 0x0001;// 失败
    public static final int CODE_NOT_FOUND = 0X0002;// 未找到
    public static final int CODE_INVALID = 0X0003;// 无效的
    public static final int CODE_ROUTER_NOT_REGISTER = 0X0004;// 未注册路由
    public static final int CODE_CANNOT_BIND_LOCAL = 0X0005;// 不能绑定到本地
    public static final int CODE_REMOTE_EXCEPTION = 0X0006;// 远程错误
    public static final int CODE_CANNOT_BIND_WIDE = 0X0007;// 不能绑定到广域网络
    public static final int CODE_TARGET_IS_WIDE = 0X0008;// 目标网络为广域路由
    public static final int CODE_WIDE_STOPPING = 0X0009;// 广域路由关闭中
    public static final int CODE_NOT_IMPLEMENT = 0X000a; // 未实现的
    private int code;
    private String msg;
    private String data;
    private Object object;

    public RouterActionResult(Builder builder) {
        this.code = builder.code;
        this.msg = builder.msg;
        this.data = builder.data;
        this.object = builder.object;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getData() {
        return data;
    }

    public Object getObject() {
        return object;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code", code);
            jsonObject.put("msg", msg);
            jsonObject.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static class Builder {
        private int code;
        private String msg;
        private String data;
        private Object object;

        public Builder() {
            code = CODE_ERROR;
            msg = "";
            object = null;
            data = null;
        }

        public Builder resultString(String resultString) {
            try {
                JSONObject jsonObject = new JSONObject(resultString);
                this.code = jsonObject.getInt("code");
                this.msg = jsonObject.getString("msg");
                this.data = jsonObject.getString("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder msg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder data(String data) {
            this.data = data;
            return this;
        }

        public Builder object(Object object) {
            this.object = object;
            return this;
        }

        public RouterActionResult build() {
            return new RouterActionResult(this);
        }

    }

}
