package cn.itcast.demo.webservice;

import java.util.List;

public class App {
    public static void main(String[] args) {
        WeatherWS weatherWS=new WeatherWS();
        WeatherWSSoap weatherWSSoap = weatherWS.getWeatherWSSoap();
        ArrayOfString weather = weatherWSSoap.getWeather("成都", null);
        List<String> string = weather.getString();
        System.out.println(string);
    }
}
