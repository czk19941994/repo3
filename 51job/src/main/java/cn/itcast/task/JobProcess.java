package cn.itcast.task;

import cn.itcast.domain.JobInfo;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

@Component
public class JobProcess implements PageProcessor {
    @Override
    public void process(Page page) {
        List<Selectable> nodes = page.getHtml().css("div#resultList div.el").nodes();
        //System.out.println(123);
        String s1 = page.getHtml().toString();
        if(nodes.size()==0){
            //招聘详情页
            this.saveJobInfo(page);
        }else{
            //不为空，为招聘列表页，解析器url，将其放入了队列之中
            for (Selectable node : nodes) {
                //得到超链接
                String url = node.links().toString();
                //将其房屋任务队列中
                page.addTargetRequest(url);
            }
            //获得下一页的url
         String s=   page.getHtml().css("div.p_in li.bk").nodes().get(1).links().toString();
            page.addTargetRequest(s);
        }
    }

    private void saveJobInfo(Page page) {
        Html html = page.getHtml();
        JobInfo jobInfo=new JobInfo();
        jobInfo.setCompanyName(html.css("div.cn p.cname a","text").toString());
        jobInfo.setCompanyAddr(Jsoup.parse(html.css("div.bmsg").nodes().get(1).toString()).text());
        jobInfo.setCompanyInfo(Jsoup.parse(html.css("div.tmsg").toString()).text());
        jobInfo.setJobName(html.css("div.cn h1","text").toString());
        //jobInfo.setJobAddr(html.css("div.cn span.t3","text").toString());
        jobInfo.setJobInfo(Jsoup.parse(html.css("div.job_msg").toString()).text());
        jobInfo.setUrl(page.getUrl().toString());
       // jobInfo.setTime(Jsoup.parse(html.css("span.t5").regex(".*发布").toString()).text());
        //将结果存入内存
        page.putField("jobInfo",jobInfo);
    }


    private Site site=Site.me().setCharset("GBK").setTimeOut(10*10000).setRetrySleepTime(3);
    public Site getSite() {
        return site;
    }
    private String url="https://search.51job.com/list/000000,000000,0000,00,9,99,java,2,1.html?lang=c&stype=&postchannel=0000&workyear=99&cotype=99&degreefrom=99&jobterm=99&companysize=99&providesalary=99&lonlat=0%2C0&radius=-1&ord_field=0&confirmdate=9&fromType=&dibiaoid=0&address=&line=&specialarea=00&from=&welfare=";
    @Scheduled(initialDelay = 1000,fixedDelay = 100000)
    public void process(){
        Spider.create(new JobProcess()).addUrl(url).setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(100000))).thread(8).addPipeline(pipline).run();
    }
    @Autowired
    private SpringdataPipline pipline;
}
