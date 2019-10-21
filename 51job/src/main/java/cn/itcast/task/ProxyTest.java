package cn.itcast.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
//@Component
public class ProxyTest implements PageProcessor {

    public void process(Page page) {

    }
    @Scheduled(initialDelay = 1000)
    public void Process(){
        //创建下载器
        HttpClientDownloader httpClientDownloader=new HttpClientDownloader();
        //创建代理服务器信息
        
    }
    private Site site=Site.me();
    public Site getSite() {
        return site;
    }
}
