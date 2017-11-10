package cn.neocross.libs.common.router.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by shenhua on 2017/9/9.
 * Email shenhuanet@126.com
 */
public class RouterResponse {

    private static final int TIME_OUT = 30 * 1000;
    private long mTimeOut = 0;
    private boolean mHasGet = false;
    public boolean mIsAsync = true;
    private int mCode = -1;
    private String mMessage = "";
    private String mData;
    public Object mObject;
    public String mResultString;
    public Future<String> mAsyncResponse;

    public RouterResponse() {
        this(TIME_OUT);
    }

    public RouterResponse(long timeout) {
        if (timeout > TIME_OUT * 2 || timeout < 0) {
            timeout = TIME_OUT;
        }
        mTimeOut = timeout;
    }

    public boolean isAsync() {
        return mIsAsync;
    }

    public String get() throws Exception {
        if (mIsAsync) {
            mResultString = mAsyncResponse.get(mTimeOut, TimeUnit.MILLISECONDS);
        }
        parseResult();
        return mResultString;
    }

    private void parseResult() {
        if (!mHasGet) {
            try {
                JSONObject jsonObject = new JSONObject(mResultString);
                this.mCode = jsonObject.getInt("code");
                this.mMessage = jsonObject.getString("msg");
                this.mData = jsonObject.getString("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mHasGet = true;
        }
    }

    public int getCode() throws Exception {
        if (!mHasGet) {
            get();
        }
        return mCode;
    }

    public String getMessage() throws Exception {
        if (!mHasGet) {
            get();
        }
        return mMessage;
    }

    public String getData() throws Exception {
        if (!mHasGet) {
            get();
        }
        return mData;
    }

    public Object getObject() throws Exception {
        if (!mHasGet) {
            get();
        }
        return mObject;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" code:");
        sb.append(mCode);
        sb.append(" message:");
        sb.append(mMessage);
        sb.append(" data:");
        sb.append(mData);
        sb.append(" object:");
        sb.append(mObject);
        sb.append(" string:");
        sb.append(mResultString);
        return sb.toString();
    }
}
