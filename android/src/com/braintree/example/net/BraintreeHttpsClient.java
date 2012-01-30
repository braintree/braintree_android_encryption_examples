package com.braintree.example.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

public class BraintreeHttpsClient {
    private String DEFAULT_RESPONSE = "Could not connect to merchant server";
    private String charset = "UTF-8";
    private String params = "";
    private HttpsURLConnection httpsURLConnection;
    private Integer responseCode;
    private String responseMessage;
    private String responseBody;
    private InputStream keyStore;
    private String keyStorePassword;

    public BraintreeHttpsClient(String merchantServerURL, InputStream keyStore, String keyStorePassword) {
        this.keyStore = keyStore;
        this.keyStorePassword = keyStorePassword;
        URL url;
        try {
            url = new URL(merchantServerURL);
            this.httpsURLConnection = (HttpsURLConnection) url.openConnection();
            this.httpsURLConnection.setConnectTimeout(5000);
            this.httpsURLConnection.setSSLSocketFactory(getSSLSocketFactory());
            this.httpsURLConnection.setHostnameVerifier(new AllowAllHostnameVerifier());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    public void addParam(String field, String value) {
        try {
            String param = String.format(field + "=%s", URLEncoder.encode(value, charset));
            if (!params.equals("")) {
                params = params + "&";
            }
            params = params + param;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void post() {
        if (httpsURLConnection == null) {
            return;
        }
        try {
            clearResponseFields();
            httpsURLConnection.setDoOutput(true);
            httpsURLConnection.setRequestProperty("Accept-Charset", charset);
            httpsURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
            OutputStream output = httpsURLConnection.getOutputStream();
            output.write(params.getBytes(charset));
            output.close();
            InputStream response = httpsURLConnection.getInputStream();
            buildResponse(response);
            this.responseCode = httpsURLConnection.getResponseCode();
            this.responseMessage = httpsURLConnection.getResponseMessage();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpsURLConnection.disconnect();
        }
    }

    public Integer getResponseCode() {
        return this.responseCode;
    }

    public String getResponseMessage() {
        return this.responseMessage;
    }

    public String getResponseText() {
        return this.responseBody;
    }

    public String prettyResponse() {
        if (responseCode == null) {
            return DEFAULT_RESPONSE;
        } else {
            return Integer.toString(responseCode) + " : " +
                    responseMessage + "\n" +
                    responseBody;
        }
    }

    private void clearResponseFields() {
        responseCode = null;
        responseMessage = "";
        responseBody = "";
    }

    private void buildResponse(InputStream response) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(response, charset));
            for (String line; (line = reader.readLine()) != null;) {
                responseBody = responseBody + line;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private SSLSocketFactory getSSLSocketFactory() {
        try {
            KeyStore trusted = KeyStore.getInstance("BKS");
            try {
                trusted.load(keyStore, keyStorePassword.toCharArray());
            } finally {
                keyStore.close();
            }
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("X509");
            trustManagerFactory.init(trusted);

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, trustManagerFactory.getTrustManagers(), null);
            return context.getSocketFactory();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}