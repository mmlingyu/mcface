package com.sxz.ai.face;

import com.zhy.http.okhttp.https.HttpsUtils;

import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class BaseApi {
    public final static int CONNECT_TIMEOUT = 3;
    public final static int READ_TIMEOUT = 3;
    public final static int WRITE_TIMEOUT = 3;
    private static HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);

    public static final OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
            .hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            }).sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
           //.sslSocketFactory(getDefaultSSLSocketFactory(), sslParams.trustManager)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS).build();//设置连接超时时间



    private static synchronized SSLSocketFactory getDefaultSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                        }

                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                        }

                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            }, null);
            return sslContext.getSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new AssertionError();
        }

    }
}