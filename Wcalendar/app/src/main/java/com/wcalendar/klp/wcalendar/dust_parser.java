package com.wcalendar.klp.wcalendar;

import android.content.Context;
import android.os.AsyncTask;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class dust_parser extends AsyncTask<String , Void, Document> {
    Document document;
    String time_;
    String location_;
    String PM10_;
    String PM25_;

    double lat;
    double lon;
    Context context;


    public dust_parser(Context context, double lat, double lon){
        this.context = context;
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    protected void onPostExecute(Document document) {
        while(true) {
            try {
                City city= new City(context, lat ,lon);
                NodeList nodeList = document.getElementsByTagName("item");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    Element element = (Element) node;
                    if (city.City_name().equals(element.getElementsByTagName("cityName").item(0).getChildNodes().item(0).getNodeValue())) {
                        NodeList time = element.getElementsByTagName("dataTime");
                        time_ = time.item(0).getChildNodes().item(0).getNodeValue();

                        location_ = city.City_name();

                        NodeList PM10 = element.getElementsByTagName("pm10Value");
                        PM10_ = PM10.item(0).getChildNodes().item(0).getNodeValue();

                        NodeList PM25 = element.getElementsByTagName("pm25Value");
                        PM25_ = PM25.item(0).getChildNodes().item(0).getNodeValue();
                    }
                }
                break;
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        super.onPostExecute(document);
    }

    @Override
    protected Document doInBackground(String... strings) {
        while(true) {
            try {
                Area_name area_name = new Area_name(context, lat, lon);
                StringBuilder urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnMesureSidoLIst");
                urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=avRyiVbF2TLqxBhIM2k5I%2B1ftisPzEeqdoqkmchNU0eZh48XElEJPsmtqp8oT2%2BPycIvIoMXeEehXtxwJVL1ow%3D%3D"); /*Service Key*/
                urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
                urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/
                urlBuilder.append("&" + URLEncoder.encode("sidoName", "UTF-8") + "=" + URLEncoder.encode(area_name.Location_name(), "UTF-8")); /*시도 이름 (서울, 부산, 대구, 인천, 광주, 대전, 울산, 경기, 강원, 충북, 충남, 전북, 전남, 경북, 경남, 제주, 세종)*/
                urlBuilder.append("&" + URLEncoder.encode("searchCondition", "UTF-8") + "=" + URLEncoder.encode("DAILY", "UTF-8")); /*요청 데이터기간 (시간 : HOUR, 하루 : DAILY)*/
                URL url = new URL(urlBuilder.toString());
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                document = documentBuilder.parse(new InputSource(url.openStream()));
                document.getDocumentElement().normalize();
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return document;
    }
}
