package cn.itcast.demo.publish;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;

@WebService
public class HelloService {
    public void publish(){
        System.out.println("陈彰坤");
    }

    public static void main(String[] args) {
        Endpoint.publish("http://192.168.1.4:8088/hello",new HelloService());
    }
}
