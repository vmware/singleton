package com.vmware.vip.test.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.vmware.vipclient.i18n.util.ConstantsKeys;

public class HttpRequester {
	public static String post(String url, String param){
        String result="";
        try {
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection)urlObj.openConnection();
            connection.setRequestMethod(ConstantsKeys.POST);
            connection.setRequestProperty("accept", "application/json");
            connection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            PrintWriter printWriter = new PrintWriter(connection.getOutputStream());
            printWriter.print(param);
            printWriter.flush();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            String line;
            while((line = bufferedReader.readLine())!=null){
                result += "\r\n" + line;
            }
            bufferedReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
