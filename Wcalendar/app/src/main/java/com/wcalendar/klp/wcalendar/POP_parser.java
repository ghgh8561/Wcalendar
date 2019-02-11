package com.wcalendar.klp.wcalendar;

import android.os.AsyncTask;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class POP_parser extends AsyncTask<String, Void, Document> {
    Document document;

    String Temperatures; // 현재기온(3시간간격)
    String wkKor; // 날씨상태
    String POP; // 강수확률
    double lat;
    double lon;

    public POP_parser(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    protected void onPostExecute(Document document) {
        while(true) {
            try {
                NodeList nodeList = document.getElementsByTagName("data");
                Temperatures = nodeList.item(0).getChildNodes().item(5).getTextContent();
                wkKor = nodeList.item(0).getChildNodes().item(15).getTextContent();
                POP = nodeList.item(0).getChildNodes().item(19).getTextContent();
                break;
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        super.onPreExecute();
    }

    @Override
    protected Document doInBackground(String... strings) {
        while (true) {
            try {
                Calculation calculation = new Calculation();
                Calculation.LatXLngY latXLngY = calculation.convertGRID_GPS(Calculation.TO_GRID, lat, lon);
                StringBuilder urlBuilder = new StringBuilder("http://www.kma.go.kr/wid/queryDFS.jsp?"); /*URL*/
                urlBuilder.append(URLEncoder.encode("gridx", "UTF-8") + "=" + URLEncoder.encode(String.valueOf((int) latXLngY.x), "UTF-8"));
                urlBuilder.append("&" + URLEncoder.encode("gridy", "UTF-8") + "=" + URLEncoder.encode(String.valueOf((int) latXLngY.y), "UTF-8"));
                URL url = new URL(urlBuilder.toString());
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                document = documentBuilder.parse(new InputSource(url.openStream()));
                document.getDocumentElement().normalize();
                break;
            } catch (Exception e) {
                doInBackground();
                e.printStackTrace();
            }
        }
        return document;
    }
}