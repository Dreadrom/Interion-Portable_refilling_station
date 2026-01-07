package com.technotrade.pts2.network.http;

import static com.technotrade.pts2.enumeration.ProtocolSecurityType.HTTPS;
import static com.technotrade.pts2.enumeration.Result.INIT_ERROR;

import android.annotation.SuppressLint;
import android.content.Context;

import com.burgstaller.okhttp.AuthenticationCacheInterceptor;
import com.burgstaller.okhttp.CachingAuthenticatorDecorator;
import com.burgstaller.okhttp.basic.BasicAuthenticator;
import com.burgstaller.okhttp.digest.CachingAuthenticator;
import com.burgstaller.okhttp.digest.Credentials;
import com.burgstaller.okhttp.digest.DigestAuthenticator;
import com.technotrade.pts2.BuildConfig;
import com.technotrade.pts2.RequestCallback;
import com.technotrade.pts2.enumeration.AuthenticationType;
import com.technotrade.pts2.enumeration.ProtocolSecurityType;
import com.technotrade.pts2.enumeration.Result;
import com.technotrade.pts2.exception.TTException;
import com.technotrade.pts2.network.BaseClientManager;
import com.technotrade.pts2.network.IRequest;
import com.technotrade.pts2.util.Logger;
import com.technotrade.pts2.util.UnicodeReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;

/// <summary>
/// Http client manager.
/// Contains methods for requests executing
/// </summary>
public class HttpClientManager extends BaseClientManager {
    protected OkHttpClient mHttpClient;
    protected HttpUrl mHttpUrl;
    protected Map<String, String> mRequestHeaders;
    protected Map<String, Object> mRequestParams;
    protected ExecutorService mExecutor;

    HttpLoggingInterceptor mHTTPLogger = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
        @Override public void log(String message) {
            Logger.debug("HTTPLogger: " + message);
        }
    });
    public HttpClientManager(WeakReference<Context> contextRef) {
        super(contextRef);

        mHttpClient = null;
        mRequestHeaders = new HashMap<>();
        mRequestParams = new HashMap<>();
        mRequests = new ArrayList<>();
        mExecutor = null;

        //Keep-alive can't be used
        //appendHeader("Connection", "keep-alive");
        appendHeader("Connection", "close");
        appendHeader("Accept-Encoding", "identity");
        System.setProperty("http.keepAlive", "false");
    }
    /// <summary>
    /// Opens the connection
    /// </summary>
    @Override
    public synchronized Result open() {
        close();

        try {
            String url = getURLPath();
            mHttpUrl = HttpUrl.parse(url);
            mHttpClient = createHttpClient();
            mExecutor = Executors.newSingleThreadExecutor();
        } catch(TTException e) {
            return e.getInnerResult();
        }

        if (mHttpClient == null) {
            return Result.NETWORK_ERROR;
        }

        mIsOpened = true;

        return Result.SUCCESS;
    }
    /// <summary>
    /// Closes the connection
    /// </summary>
    public synchronized void close() {
        clearRequestsQueue();

        if (mExecutor != null) {
            mExecutor.shutdown();

            try {
                if (!mExecutor.awaitTermination(1500, TimeUnit.MILLISECONDS)) {
                    mExecutor.shutdownNow();
                }
            } catch(InterruptedException e) {
                mExecutor.shutdownNow();
            }
        }

        mIsOpened = false;
    }
    /// <summary>
    /// Cleans internal requests queue
    /// </summary>
    /// <returns>Result</returns>
    public synchronized Result clearRequestsQueue() {
        if (!mIsOpened) { return INIT_ERROR; }

        mRequests.clear();

        return Result.SUCCESS;
    }
    /// <summary>
    /// Executes the request queue
    /// </summary>
    /// <returns>Result</returns>
    public synchronized Result executeRequestsQueue() {
        if (!mIsOpened) { return Result.INIT_ERROR; }

        try
        {
            if (mHttpUrl == null) {
                throw new TTException("wrong URL path", Result.NETWORK_ERROR);
            }

            final HttpUrl.Builder httpBuilder = mHttpUrl.newBuilder();
            HttpUrl httpUrl = httpBuilder.build();

            if (mRequestParams != null) {
                for (Map.Entry<String, Object> param : mRequestParams.entrySet()) {
                    httpBuilder.addQueryParameter(param.getKey(), param.getValue().toString());
                }
            }

            final Request.Builder builder = new Request.Builder();

            if (mRequestHeaders != null) {
                appendHeaders(builder, mRequestHeaders);
            }

            builder.tag(String.valueOf(++mId));
            builder.url(httpUrl);
            final MediaType contentTypeJSON = MediaType.parse("application/json; charset=utf-8");
            final String requestJSON = String.valueOf(requestJSON());
            final RequestBody requestBody = RequestBody.create(requestJSON, contentTypeJSON);
            builder.post(requestBody);

            final Request httpRequest = builder.build();

            if (BuildConfig.DEBUG) {
                Logger.debug("pts2", "urlPath: " + mHttpUrl);
                Logger.debug("pts2", "request: " + requestJSON);
            }

            final Call httpCall = mHttpClient.newCall(httpRequest);

            class RequestCall implements Callable<Result> {
                @Override
                public Result call () throws TTException, JSONException, IOException {
                    try (Response response = httpCall.execute()) {
                        int statusCode = response.code();
                        if (statusCode != 200 || !response.isSuccessful()) {
                            if (statusCode == 401) {
                                Logger.error("call TTException: Authorization error");
                                throw new TTException("Authorization error", Result.UNAUTHORIZED_ERROR);
                            }

                            Logger.error("call TTException: Response code error");
                            throw new TTException("Response code error", Result.RESPONSE_CODE_ERROR);
                        }

                        final ResponseBody responseBody = response.body();

                        if (responseBody == null) {
                            Logger.error("call TTException: response is null");
                            throw new TTException("response is null", Result.PROTOCOL_ERROR);
                        }

                        final MediaType contentType = responseBody.contentType();

                        if (contentType == null) {
                            responseBody.close();

                            Logger.error("call TTException: wrong content type");
                            throw new TTException("wrong content type", Result.PROTOCOL_ERROR);
                        }

                        BufferedSource bufferedSource = responseBody.source();

                        StringBuilder readBytes = new StringBuilder();

                        while (!bufferedSource.exhausted ()) {
                            readBytes.append(bufferedSource.readUtf8());
                        }

                        final Result result = parseResponse(readBytes.toString());
                        responseBody.close();

                        return result;
                    }
                    catch(TTException e) {
                        Logger.error("RequestCall exception: " + e.getMessage());
                        return e.getInnerResult();
                    }
                }
            }

            final RequestCall requestCall = new RequestCall();
            final Future<Result> future = mExecutor.submit(requestCall);
            final Result result = future.get();
            future.cancel(true);

            return result;
        }
        catch(TTException e) {
            Logger.error("executeRequestsQueue TTException: " + e.getMessage());
            return e.getInnerResult();
        }
        catch(JSONException e) {
            Logger.error("executeRequestsQueue JSONException: " + e.getMessage());
            return Result.JSON_PARSE_ERROR;
        }  catch(InterruptedException e) {
            Logger.error("executeRequestsQueue InterruptedException: " + e.getMessage());
            return Result.NETWORK_ERROR;
        }
        catch(ExecutionException e) {
            Throwable t = e.getCause();

            if (t instanceof SocketTimeoutException) {
                Logger.error("executeRequestsQueue SocketTimeoutException: " + e.getMessage());
                return Result.NETWORK_ERROR;
            }

            Logger.error("executeRequestsQueue ExecutionException: " + e.getMessage());
            return Result.NETWORK_ERROR;
        }
    }
    /// <summary>
    /// Returns full request JSON based on each request in internal requests list
    /// </summary>
    /// <returns>JSONObject</returns>
    protected synchronized JSONObject requestJSON() throws JSONException, TTException {
        JSONObject requestJObject = new JSONObject();
        requestJObject.put("Protocol", "jsonPTS");

        JSONArray packetsJArray = new JSONArray();

        for (int i = 0; i < mRequests.size(); ++i) {
            IRequest<?> request = mRequests.get(i);
            request.setId(i);

            packetsJArray.put(request.requestJSON());
        }

        requestJObject.put("Packets", packetsJArray);

        return requestJObject;
    }
    /// <summary>
    /// Parses response JSON
    /// </summary>
    /// <param name="inputStream">input stream</param>
    /// <returns>Result</returns>
    protected Result parseResponse(String input) throws JSONException, TTException {
        if (BuildConfig.DEBUG) {
            Logger.debug("pts2", "response text: " + input);
        }

        final JSONObject jsonAnswer = new JSONObject(input);

        return parseJSON(jsonAnswer);
    }
    /// <summary>
    /// Parses response JSON
    /// </summary>
    /// <param name="inputStream">input stream</param>
    /// <returns>Result</returns>
    protected Result parseResponse(InputStream inputStream) throws JSONException, IOException, TTException {
        UnicodeReader inputStreamReader = new UnicodeReader(inputStream, "UTF-8");
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String responseText = "";

        try {
            java.util.stream.Stream<String> lines = reader.lines();
            Collector<CharSequence, ?, String> collectorsJoined = Collectors.joining();
            responseText = lines.collect(collectorsJoined);
        } finally {
            reader.close();
        }

        if (BuildConfig.DEBUG) {
            Logger.debug("pts2", "response text: " + responseText);
        }

        final JSONObject jsonAnswer = new JSONObject(responseText);

        return parseJSON(jsonAnswer);
    }
    /// <summary>
    /// Parse JSON and fill results for each request in internal requests list
    /// </summary>
    /// <param name="jObject">JSON object</param>
    /// <returns>Result</returns>
    protected Result parseJSON(JSONObject jObject) throws JSONException, TTException {
        Result result = Result.SUCCESS;

        if (!jObject.has("Protocol")) {
            throw new TTException("Error: response doesn't contain Protocol property", Result.PROTOCOL_ERROR);
        }

        if (!jObject.getString("Protocol").equals("jsonPTS")){
            throw new TTException("Error: response contains Protocol property that is not jsonPTS", Result.PROTOCOL_ERROR);
        }

        if (!jObject.has("Packets")) {
            throw new TTException("Error: response doesn't contain Packets property", Result.PROTOCOL_ERROR);
        }

        JSONArray packetsJArray = jObject.getJSONArray("Packets");

        if (packetsJArray.length() == 0) {
            throw new TTException("Error: Packets elements count is 0", Result.PROTOCOL_ERROR);
        }

        for (int i = 0; i < packetsJArray.length(); ++i) {
            JSONObject packetJObject = packetsJArray.getJSONObject(i);

            IRequest<?> request = mRequests.get(i);

            try {
                request.parseJSON(packetJObject);
            }
            catch(Exception exception) {
                request.setError(true);
                request.setErrorMessage(exception.getMessage());
                //at last one request was failed
                result = Result.AT_LAST_ONE_REQUEST_IN_SEQUENCE_FAILED_ERROR;
                continue;
            }

            String key = request.getKey();

            if (mCallbacks.containsKey(key)) {
                if (request.isError()) {
                    result = Result.AT_LAST_ONE_REQUEST_IN_SEQUENCE_FAILED_ERROR;
                }

                callCallback(key, i);
            }
        }

        return result;
    }
    /// <summary>
    /// Calls the required callback
    /// </summary>
    /// <param name="key">callback key</param>
    /// <param name="requestIndex">request index in mRequests list</param>
    private <T> void callCallback(String key, int requestIndex) {
        @SuppressWarnings("unchecked")
        RequestCallback<T> callback = (RequestCallback<T>) mCallbacks.get(key);
        assert callback != null;
        @SuppressWarnings("unchecked")
        IRequest<T> request = (IRequest<T>) mRequests.get(requestIndex);
        assert request != null;

        callback.execute(request, request.getResult());
    }
    /// <summary>
    /// Creates the HTTP client
    /// </summary>
    /// <returns>OkHttpClient</returns>
    private OkHttpClient createHttpClient() throws TTException {

        Authenticator authenticator;
        AuthenticationType authenticationType = mSettings.getAuthenticationType();

        if (authenticationType == AuthenticationType.DIGEST) {
            authenticator = new DigestAuthenticator(new Credentials(mSettings.getLogin(), mSettings.getPassword()));
        }
        else {
            authenticator = new BasicAuthenticator(new Credentials(mSettings.getLogin(), mSettings.getPassword()));
        }

        final Map<String, CachingAuthenticator> authCache = new ConcurrentHashMap<>();

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();

        okHttpClientBuilder.authenticator(new CachingAuthenticatorDecorator(authenticator, authCache));
        okHttpClientBuilder.addInterceptor(new AuthenticationCacheInterceptor(authCache));

        if (BuildConfig.DEBUG) {
            okHttpClientBuilder.addInterceptor(mHTTPLogger);
        }

        okHttpClientBuilder.connectTimeout(10, TimeUnit.SECONDS);
        okHttpClientBuilder.readTimeout(10, TimeUnit.SECONDS);
        okHttpClientBuilder.writeTimeout(10, TimeUnit.SECONDS);
        okHttpClientBuilder.callTimeout(10, TimeUnit.SECONDS);
        okHttpClientBuilder.retryOnConnectionFailure(true);

        ProtocolSecurityType protocolSecurityType = mSettings.getProtocolSecurityType();
        if (protocolSecurityType == HTTPS) {
            SSLContext sslContext;

            final TrustManager[] trustManagers = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
            };

            try {
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null, null);

                Context context = mContextRef.get();

                if (context == null ) {
                    throw new TTException(Result.NULL_POINTER_ERROR);
                }

                @SuppressLint("DiscouragedApi")
                int certResId = context.getResources().getIdentifier("ttdispenser", "raw", context.getPackageName());
                InputStream certInputStream = context.getResources().openRawResource(certResId);
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

                Certificate cert;
                try {
                    cert = certificateFactory.generateCertificate(certInputStream);
                } finally { certInputStream.close(); }

                keyStore.setCertificateEntry("ua.technotrade.ttdispenser", cert);

                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();

                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm);
                trustManagerFactory.init(keyStore);

                sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null, trustManagers, new java.security.SecureRandom());

                okHttpClientBuilder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagers[0])
                        .build();
                okHttpClientBuilder.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });

            }
            catch(TTException e) {
                Logger.error("TTException: " + e.getMessage());
                throw e;
            }
            catch(Exception e) {
                Logger.error("Exception: " + e.getMessage());
                throw new TTException(e.getMessage());
            }
        }

        mHttpClient = okHttpClientBuilder.build();

        return mHttpClient;
    }
    /// <summary>
    /// Returns the URL of PTS2 device
    /// </summary>
    /// <returns>URL string</returns>
    protected synchronized String getURLPath() {
        String url = "";
        String port = "";
        switch (mSettings.getProtocolSecurityType()) {
            case HTTP:
                url = "http://";
                port = String.valueOf(mSettings.getHttpPort());
                break;
            case HTTPS:
                url = "https://";
                port = String.valueOf(mSettings.getHttpsPort());
                break;
        }

        return url + mSettings.getHost() + ":" + port + "/jsonPTS";
    }
    /// <summary>
    /// Appends header to request
    /// </summary>
    /// <param name="key">Header key</param>
    /// <param name="value">Header value</param>
    public void appendHeader(String key, String value) {
        mRequestHeaders.put(key, value);
    }
    /// <summary>
    /// Appends headers to request
    /// </summary>
    protected void appendHeaders(Map<String, String> headers) {
        if (headers == null || headers.isEmpty()) {
            return;
        }

        mRequestHeaders.putAll(headers);
    }
    /// <summary>
    /// Appends headers to request builder
    /// </summary>
    /// <param name="builder">Request builder</param>
    /// <param name="headers">Headers map</param>
    protected void appendHeaders(Request.Builder builder, Map<String, String> headers) {
        if (builder == null) {
            return;
        }

        if (headers == null || headers.isEmpty()) {
            return;
        }

        Headers.Builder headerBuilder = new Headers.Builder();

        for (String key : headers.keySet()) {
            headerBuilder.add(key, Objects.requireNonNull(headers.get(key)));
        }

        builder.headers(headerBuilder.build());
    }
}
