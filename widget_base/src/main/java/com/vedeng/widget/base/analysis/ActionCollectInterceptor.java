package com.vedeng.widget.base.analysis;

import android.os.Build;
import android.text.TextUtils;

import com.vedeng.comm.base.utils.LogUtils;
import com.vedeng.comm.base.utils.MobileUtils;
import com.vedeng.comm.base.utils.TimeUtils;
import com.vedeng.widget.base.MicBusinessConfigHelper;
import com.vedeng.widget.base.module.ActionData;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**********************************************************
 * @文件名称：
 * @文件作者：聂中泽
 * @创建时间：2017/1/6 11:41
 * @文件描述：接口请求信息数据interceptor
 * @修改历史：2017/1/6 创建初始版本
 **********************************************************/
public class ActionCollectInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final String TAG = "OkHttp->ActionCollectInterceptor";

    private static final List<String> requestUrlExcluded = new ArrayList<>();
    private static final List<String> loginOrRegisterUrlPath = new ArrayList<>();

    static {
        requestUrlExcluded.add("/base/userActInfo/collect");
        requestUrlExcluded.add("/base/clientReqInfo/collect");
        requestUrlExcluded.add("/common/server/getServerTime");
        requestUrlExcluded.add("/base/crashManager/insertCrashLog");
        //买家获取港口，由于数据量大导致内存溢出，所以暂时不做拦截
        requestUrlExcluded.add("/base/logistic/listDestinatePorts");

        loginOrRegisterUrlPath.add("/openapi_mic/login");
        loginOrRegisterUrlPath.add("/ssl/openapi_mic/login");
        loginOrRegisterUrlPath.add("/supplier/loginManager/login");
        loginOrRegisterUrlPath.add("/ssl/supplier/loginManager/login");
        loginOrRegisterUrlPath.add("/openapi_mic/register");
        loginOrRegisterUrlPath.add("/ssl/openapi_mic/register");
    }

    private String loginId;

    private static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "";
        }
    }

    private static String bodyToString(final ResponseBody body) {
        BufferedSource bufferedSource = body.source();
        try {
            bufferedSource.request(Long.MAX_VALUE);
            Buffer buffer = bufferedSource.buffer();
            return buffer.clone().readUtf8();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private boolean canInjectIntoBody(Request request) {
        if (request == null) {
            return false;
        }
        if (!TextUtils.equals(request.method(), "POST")) {
            return false;
        }
        RequestBody body = request.body();
        if (body == null) {
            return false;
        }
        MediaType mediaType = body.contentType();
        if (mediaType == null) {
            return false;
        }
        if (!TextUtils.equals(mediaType.subtype(), "x-www-form-urlencoded")) {
            return false;
        }
        return true;
    }

    private boolean isUIProcess() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.O ||
                MobileUtils.getProcessName(MicBusinessConfigHelper.getInstance().getContext()).equals(MicBusinessConfigHelper.getInstance().getContext().getPackageName());
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        Request request = builder.build();
        String urlPath = request.url().encodedPath();
        //8.0以上不允许后台服务再启动服务，故这里为了避免启动接口数据收集，直接放过子进程的所有使用公共网络库的请求，不做统计
        if (!"POST".equals(request.method()) || !isUIProcess()) {
            return chain.proceed(request);
        }
        LogUtils.e(TAG, MobileUtils.getProcessName(MicBusinessConfigHelper.getInstance().getContext()) + "<=>" + urlPath);
        if (requestUrlExcluded.indexOf(urlPath) > -1) {
            if (shouldInjectLoginIdToBody(request)) {
                request = injectParamIntoBody(builder, bodyToString(request.body()), "userName", loginId);
            }
            return chain.proceed(request);
        }

        ActionData data = new ActionData();
        data.url = request.url().toString();
        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        if (hasRequestBody) {
            boolean fileUploadRequest = false;
            MediaType mediaType = requestBody.contentType();
            if (mediaType != null) {
                fileUploadRequest = "multipart".equals(mediaType.type()) && "form-data".equals(mediaType.subtype());
            }
            data.param = !fileUploadRequest ? bodyToString(requestBody) : "";
        }
        data.sendTime = TimeUtils.toGMTStringWithMillis(new Date());
//        LogUtils.i(TAG, "SEND " + data.sendTime);
        Response response = chain.proceed(request);
        data.code = "0";
        data.receiveTime = TimeUtils.toGMTStringWithMillis(new Date());
//        LogUtils.i(TAG, "RECEIVE " + data.receiveTime);
        if (!response.isSuccessful()) {
            data.code = String.valueOf(response.code());
            data.err = response.message();
        } else {
            ResponseBody responseBody = response.body();
            boolean requestSuccess = handleResponse(data, bodyToString(responseBody));
            if (isLoginUrl(urlPath)) {
                loginId = requestSuccess ? getLoginId(data.param) : "";
            }
        }
        AnalyticsTracker.getInstances().trackRequestAction(data);
        return response;
    }

    private boolean shouldInjectLoginIdToBody(Request request) {
        return "/base/clientReqInfo/collect".equals(request.url().encodedPath()) && !TextUtils.isEmpty(loginId) && canInjectIntoBody(request);
    }

    private boolean isLoginUrl(String urlPath) {
        return loginOrRegisterUrlPath.indexOf(urlPath) > -1;
    }

    private Request injectParamIntoBody(Request.Builder builder, String body, String paramKey, String paramsValue) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        formBodyBuilder.add(paramKey, paramsValue);
        RequestBody formBody = formBodyBuilder.build();
        body += ((body.length() > 0) ? "&" : "") + bodyToString(formBody);
        return builder.post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), body)).build();
    }

    private String getLoginId(String paramStr) {
        if (TextUtils.isEmpty(paramStr))
            return "";
        String[] items = paramStr.split("&");
        if (items != null) {
            for (String item : items) {
                String[] pair = item.split("=");
                if (pair != null && pair.length == 2 && pair[0].equals("loginId")) {
                    return pair[1];
                }
            }
        }
        return "";
    }

    private boolean handleResponse(ActionData action, String response) {
        try {
            JSONObject responseJSON = new JSONObject(response);
            if (responseJSON.has("code")) {
                action.code = responseJSON.getString("code");
            }
            if (responseJSON.has("err")) {
                action.err = responseJSON.getString("err");
            }
            return "0".equals(action.code);
        } catch (Exception e) {
        }
        return false;
    }

}
