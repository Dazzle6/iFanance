package cn.kidjoker.htmlData.service.impl;

import cn.kidjoker.htmlData.service.ParseHtmlService;
import cn.kidjoker.htmlData.service.bo.CoinColaBo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParseHtmlServiceImpl implements ParseHtmlService {
    @Override
    public Document getHtmlData() throws IOException {

        Document doc = Jsoup.connect("https://www.coincola.com/buy/BCH?country_code=CN").get();
        String body = doc.body().toString();
        System.out.println(body);
        return doc;
    }

    @Override
    public Object parseDataFromCoinCola(Document document) throws Exception {
        List<CoinColaBo> coinColaBoList = new ArrayList<>();

        Element div = document.body().child(5);
        Element table = div.getElementsByTag("table").first();
        Element tbody = table.getElementsByTag("tbody").first();
        List<Element> trList = tbody.getElementsByTag("tr");
        Integer size = trList.size();

        if (size <= 0) {
            throw new Exception("爬取数据为空！");
        }

        for (Element tr : trList) {
            Element nameTd = tr.getElementsByIndexEquals(0).first();
            Element normalTd = tr.getElementsByClass("td-normal").first();
            Element payChannelTd = tr.child(2);
            Element tradeRegionTd = tr.child(3);
            Element priceTd = tr.getElementsByClass("td-price").first();

//            获取用户地址和用户名
            Element user = nameTd.select("a").get(1);
            String userUrl = user.attr("abs:href");
            String userName = user.text();

//            获取交易次数、评价、信任数
            String nomal = normalTd.text();
            String[] subs = nomal.split("\\|");
            Integer tradeTimes = Integer.valueOf(subs[0].replace("Trades", "").trim());
            Double grade = Integer.valueOf(subs[1].replace("%", "").replace("Rating", "").trim()) * 0.01;
            Integer credit = Integer.valueOf(subs[2].replace("Trusted", "").trim());

//            支付渠道
            String payChannel = payChannelTd.text();

//            交易区间
            String[] tradeRegion = tradeRegionTd.text().trim().split("-");
            Integer low = Integer.valueOf(tradeRegion[0]);
            Integer top = Integer.valueOf(tradeRegion[1].replace("CNY", "").trim());

//            交易价格
            Double price = Double.valueOf(priceTd.text().replace("CNY", "").trim());

            CoinColaBo coinColaBo = new CoinColaBo();
            coinColaBo.setUserName(userName);
            coinColaBo.setUserUrl(userUrl);
            coinColaBo.setPrice(price);
            coinColaBo.setGrade(grade);
            coinColaBo.setCredit(credit);
            coinColaBo.setTradeTimes(tradeTimes);
            coinColaBo.setPayChannel(payChannel);
            coinColaBo.setTradeReginlow(low);
            coinColaBo.setTradeRegintop(top);
            coinColaBoList.add(coinColaBo);
        }

        return coinColaBoList;
    }

    @Override
    public void persistentDictionary(List<CoinColaBo> coinColaBoList) throws Exception {
        for (CoinColaBo c : coinColaBoList) {
            System.out.println(c.toString());
        }
    }

}
