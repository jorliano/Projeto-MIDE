package br.com.jortec.mide.internet;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Jorliano on 06/03/2016.
 */
public class HttpConnection {

    //public static final String URL = "http://192.168.0.50:8090/WebServiceMIDE/mobile/";
    public static final String URL = "http://192.168.0.10/WebServiceMIDE/mobile/";
    public static final String URL_CHAT = "http://192.168.0.10/WebServiceMIDE/chat/";
    public static final String RESPOSTA_SUCESSO = "sucesso";
    public static final String RESPOSTA_FALHOU = "falhou";
    public static final String ATUALIZACAO = "br.com.jortec.mide.internet.ATUALIZACAO";

    //POST
    public static String getPost(String url, String method, String data){

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        String answer = "";

        try{
            ArrayList<NameValuePair> valores = new ArrayList<NameValuePair>();
            valores.add(new BasicNameValuePair(method, data));
            UrlEncodedFormEntity ur = new UrlEncodedFormEntity(valores);
            Log.i("LOG ", "Metodo Post");
            //httpPost.setEntity(new UrlEncodedFormEntity(valores, "UTF-8"));
            httpPost.setEntity(new UrlEncodedFormEntity(valores));
            HttpResponse resposta = httpClient.execute(httpPost);
            answer = EntityUtils.toString(resposta.getEntity());
        }
        catch(NullPointerException e){ e.printStackTrace(); }
        catch(ClientProtocolException e){ e.printStackTrace(); }
        catch(IOException e){
          Log.i("Erro ", e.getMessage());
            e.printStackTrace();
        }

        return(answer);
    }

    // GET
    public static String getGet(String URL) {

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet get = new HttpGet(URL);
        String answer = "";

        try {

            HttpResponse resposta = httpClient.execute(get);
            answer = EntityUtils.toString(resposta.getEntity());
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (answer);



    }

    // GET
    public static InputStream getGetFile(String URL) {

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet get = new HttpGet(URL);
        InputStream inputStream = null;

        try {

            HttpResponse response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                long len = entity.getContentLength();
                inputStream = entity.getContent();

            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (inputStream);

    }
}