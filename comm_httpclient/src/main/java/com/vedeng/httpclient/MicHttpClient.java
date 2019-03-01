package com.vedeng.httpclient;

import com.vedeng.comm.base.utils.LogUtils;
import com.vedeng.comm.base.utils.Utils;
import com.vedeng.httpclient.cookie.CookiesManager;
import com.vedeng.httpclient.modle.BaseResponse;
import com.vedeng.httpclient.modle.HttpResponseCodeDefine;
import com.vedeng.httpclient.retrofit2.Call;
import com.vedeng.httpclient.retrofit2.Callback;
import com.vedeng.httpclient.retrofit2.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.ResponseBody;

/**********************************************************
 * @文件名称：MicHttpClient.java
 * @文件作者：聂中泽
 * @创建时间：2016年4月7日 下午4:17:45
 * @文件描述：连接池
 * @修改历史：2016年4月7日创建初始版本
 **********************************************************/
public class MicHttpClient {
    private static final String UTF8_BOM = "\uFEFF";
    private static final String TAG = "=====MicHttpClient=====";

    /**
     * 创建一个同步请求
     *
     * @param call
     * @param impl
     * @param <T>
     */
    public static <T> void execute(Call<T> call, DisposeDataListenerImpl impl) {
        execute(call, impl, null);
    }

    /**
     * 创建一个同步请求
     *
     * @param call
     * @param impl
     * @param targetClazz
     * @param <T>
     */
    public static <T> void execute(Call<T> call, DisposeDataListenerImpl impl, Class<?> targetClazz) {
        try {
            Response<T> response = call.execute();
            onEnqueueSuccess(response, impl, targetClazz);
        } catch (IOException e) {
            serverAnomaly(impl);
            LogUtils.e(TAG, "Sync execute response failed", e.fillInStackTrace());
        }
    }

    /**
     * 执行一个异步请求
     *
     * @param call
     * @param impl
     * @param <T>
     */
    private static <T> void enqueue(Call<T> call, DisposeDataListenerImpl impl) {
        enqueue(call, impl, null);
    }

    /**
     * 执行一个异步请求
     *
     * @param call
     * @param impl
     * @param targetClazz
     * @param <T>
     */
    public static <T> void enqueue(Call<T> call, final DisposeDataListenerImpl impl, final Class<?> targetClazz) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> arg0, Response<T> arg1) {
                onEnqueueSuccess(arg1, impl, targetClazz);
            }

            @Override
            public void onFailure(Call<T> arg0, Throwable throwable) {
                onEnqueueFailure(throwable, impl);
            }
        });
    }

    /**
     * 异步请求成功
     *
     * @param arg1
     * @param impl
     * @param targetClazz
     */
    private static <T> void onEnqueueSuccess(Response<T> arg1, DisposeDataListenerImpl impl, Class<?> targetClazz) {
        if (arg1.isSuccessful()) {
            if (arg1.body() != null) {
                convertResponseData(arg1.body(), impl, targetClazz);
            } else {
                serverAnomaly(impl);
            }
            if (arg1.headers() != null) {
                impl.onGetHeaders(arg1.headers());
            }
        } else {
            serverAnomaly(impl);
        }
    }

    private static <T> void convertResponseData(final T t, final DisposeDataListenerImpl impl, final Class<?> targetClazz) {
        Observable.create(new ObservableOnSubscribe<MicRootResponse>() {

            @Override
            public void subscribe(ObservableEmitter<MicRootResponse> emitter) throws Exception {
                if ((t instanceof ResponseBody) && targetClazz != null) {
                    ResponseBody response = (ResponseBody) t;
                    Object jsonResponse = parseResponse(response.bytes());
                    if (jsonResponse == null)
                        throw new JSONException("JSONResponse is null,check it");
                    if (jsonResponse instanceof JSONObject) {
                        JSONObject jsonObj = (JSONObject) jsonResponse;
                        if (!jsonObj.isNull(MicResponseParams.CODE.toString())) {
                            Object module = ResponseEntityToModule.parseJsonObjectToModule(jsonObj, targetClazz);
                            MicRootResponse rootResponse = new MicRootResponse();
                            rootResponse.code = jsonObj.getString(MicResponseParams.CODE.toString());
                            rootResponse.message = !jsonObj.isNull(MicResponseParams.MESSAGE.toString()) ? jsonObj.getString(MicResponseParams.MESSAGE.toString()) : getString(R.string.request_data_exception);
                            rootResponse.status = jsonObj.getString(MicResponseParams.STATUS.toString());
                            rootResponse.targetObj = module;
                            emitter.onNext(rootResponse);
                        } else {
                            throw new IllegalArgumentException("Response Code is null,check it");
                        }
                    } else {
                        throw new ClassCastException("Response is not JSONObject,check it");
                    }
                } else if (t instanceof BaseResponse) {
                    BaseResponse response = (BaseResponse) t;
                    if (!Utils.isEmpty(response.code)) {
                        MicRootResponse rootResponse = new MicRootResponse();
                        rootResponse.code = response.code;
                        rootResponse.message = !Utils.isEmpty(response.message) ? response.message : getString(R.string.request_data_exception);
                        rootResponse.status = response.status;
                        rootResponse.targetObj = t;
                        emitter.onNext(rootResponse);
                    } else {
                        throw new IllegalArgumentException("Response Code is null,check it");
                    }
                } else {
                    throw new ClassCastException("Response is not JSONObject,check it");
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MicRootResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MicRootResponse rootResponse) {
                        onEnqueueSuccess(rootResponse.code, rootResponse.message, rootResponse.status, rootResponse.targetObj, impl);
                    }

                    @Override
                    public void onError(Throwable e) {
                        serverAnomaly(impl);
                        LogUtils.e(TAG, "Custom transition response failed", e.fillInStackTrace());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 异步请求成功
     *
     * @param code
     * @param err
     * @param t
     * @param impl
     */
    protected static <T> void onEnqueueSuccess(String code, String err, String status, T t, DisposeDataListenerImpl impl) {
        if (isNeedDispose(status, HttpResponseCodeDefine.COOKIE_OVERTIME)) {
            impl.onAccountError(code, status, err);
        } else if (isNeedDispose(code, HttpResponseCodeDefine.SC_OK)) {
            if (t != null) {
                impl.onGetSuccess(t);
            } else {
                impl.onServerError(code, status, err);
            }
        } else {
            impl.onDataError(code, status, err);
        }
    }

    /**
     * 未知错误
     *
     * @param impl
     */
    private static void serverAnomaly(DisposeDataListenerImpl impl) {
        impl.onServerError(HttpResponseCodeDefine.UNKNOWN.toString(),
                HttpResponseCodeDefine.UNKNOWN.toString(),
                getString(R.string.request_data_exception));
    }

    /**
     * 异步请求失败
     *
     * @param throwable
     * @param impl
     */
    protected static void onEnqueueFailure(Throwable throwable, DisposeDataListenerImpl impl) {
        LogUtils.e("------------onEnqueueFailure------------------", throwable.toString());
        switch (HttpException.getValueByTag(throwable.getClass().getSimpleName())) {
            case SocketException:
            case SocketTimeoutException:
                impl.onNetworkError(getString(R.string.request_timeout));
                break;
            case ConnectException:
            case UnknownError:
            case UnknownHostException:
                impl.onNetworkError(getString(R.string.request_no_internet));
                break;
            case InterruptedException:
                impl.onNetworkError(getString(R.string.request_service_interrupted));
                break;
            default:
                impl.onServerError(HttpResponseCodeDefine.UNKNOWN.toString(),
                        HttpResponseCodeDefine.UNKNOWN.toString(),
                        getString(R.string.request_data_exception));
                break;
        }
    }

    private static String getResponseString(byte[] stringBytes, String charset) {
        try {
            String toReturn = (stringBytes == null) ? null : new String(stringBytes, charset);
            if (toReturn != null && toReturn.startsWith(UTF8_BOM)) {
                return toReturn.substring(1);
            }
            return toReturn;
        } catch (UnsupportedEncodingException e) {
            LogUtils.e(TAG, "Encoding response into string failed", e);
            return null;
        }
    }

    protected static Object parseResponse(byte[] responseBody) throws JSONException {
        if (null == responseBody)
            return null;
        Object result = null;
        // trim the string to prevent start with blank, and test if the string is valid JSON, because the parser don't
        // do this :(. If JSON is not valid this will return null
        String jsonString = getResponseString(responseBody, "UTF-8");
        if (jsonString != null) {
            jsonString = jsonString.trim();
            // Check if the string is an JSONObject style {} or JSONArray style []
            // If not we consider this as a string
            if ((jsonString.startsWith("{") && jsonString.endsWith("}")) || jsonString.startsWith("[")
                    && jsonString.endsWith("]")) {
                result = new JSONTokener(jsonString).nextValue();
            }
            // Check if this is a String "my String value" and remove quote
            // Other value type (numerical, boolean) should be without quote
            else if (jsonString.startsWith("\"") && jsonString.endsWith("\"")) {
                result = jsonString.substring(1, jsonString.length() - 1);
            }
        }
        if (result == null) {
            result = jsonString;
        }
        return result;
    }

    /**
     * 解析校验
     *
     * @param codeContent
     * @param targets
     * @return
     */
    private static boolean isNeedDispose(String codeContent, HttpResponseCodeDefine... targets) {
        boolean checkTag = false;
        for (HttpResponseCodeDefine target : targets) {
            checkTag = checkTag || codeCheck(target, codeContent);
        }
        return checkTag;
    }

    /**
     * code值校验
     *
     * @param target
     * @param codeContent
     * @return
     */
    private static boolean codeCheck(HttpResponseCodeDefine target, String codeContent) {
        return HttpResponseCodeDefine.getValue(target).equals(codeContent);
    }

    protected static String getString(int resId) {
        if (MicHttpClientConfigHelper.getInstance().getContext() == null)
            return "";
        return MicHttpClientConfigHelper.getInstance().getContext().getString(resId);
    }

    public static List<Cookie> getCookies() {
        return CookiesManager.getInstance().getCookies();
    }

    public static String getCookiesToString() {
        List<Cookie> cookies = getCookies();
        String cookiesStr = "";
        for (int i = 0; i < cookies.size(); i++) {
            cookiesStr = cookiesStr + cookies.get(i) + (i == cookies.size() - 1 ? "" : ";");
        }
        return cookiesStr;
    }

    public static List<Cookie> getCookies(HttpUrl httpUrl) {
        return CookiesManager.getInstance().getCookies(httpUrl);
    }

    public static void clearCookies() {
        CookiesManager.getInstance().clearCookies();
    }
}
