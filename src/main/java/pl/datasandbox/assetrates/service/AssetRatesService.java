package pl.datasandbox.assetrates.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.jni.Local;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.datasandbox.assetrates.dao.AssetRatesRepository;
import pl.datasandbox.assetrates.model.AssetRate;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;

@Service
public class AssetRatesService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private AssetRatesRepository ratesRepository;
    private static final String baseUrl = "http://stooq.pl";



    public void processAssetRates(String symbol, String dtFrom, String dtTo) throws ParseException {

        //parse dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneId.systemDefault());
        LocalDate fromDate = LocalDate.parse(dtFrom,formatter);
        LocalDate toDate = LocalDate.parse(dtTo,formatter);
        ZoneId zone = ZoneId.of("Europe/Warsaw");
        ZoneOffset zoneOffset = zone.getRules().getOffset(now());

        Stream.iterate(fromDate, dt -> dt.isBefore(toDate.plusDays(1)), dt -> dt.plusDays(1))
                .forEach(x -> {
                    AssetRate aRate = new AssetRate();
                    aRate.setSymbol(symbol);
                    aRate.setDate(Date.from(x.atStartOfDay().toInstant(zoneOffset)));
                    aRate.setRate(getAssetRateByDate(symbol,x.format(formatter).toString()));
                    aRate.setLoadDate(Date.from(now().toInstant(zoneOffset)));
                    ratesRepository.save(aRate);
                //saveAssetRate
                });

        //call getAssetRateByDate method

        //save symbol, rate using repository

    }






    public double getAssetRateByDate(String symbol, String dt) {

        Document doc = new Document(baseUrl);
        String parseUrl;
        double rate = 0.0d;

        //validate date
      //  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneId.systemDefault());



        try {

            switch (symbol) {

                case "BTC":
                case "ETH":
                    try {
                        rate = parseAssetRateCoinGecko(symbol, dt);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case "USD":
                case "EUR":
                case "CHF":
                case "GOLD":
                case "SILVER":
                    symbol = symbol.replace("SILVER","xag");
                    symbol = symbol.replace("GOLD","xau");
                    parseUrl = baseUrl + "/q/?s=" + symbol.toLowerCase() + "pln&d=" + dt;
                    doc = Jsoup.connect(parseUrl).get();
                    rate = parseAssetRateStooq(doc);
                    break;
                default:
                    parseUrl = baseUrl + "/q/?s=" + symbol.toLowerCase() + "&d=" + dt;
                    doc = Jsoup.connect(parseUrl).get();
                    rate = parseAssetRateStooq(doc);
                    break;
            }
        }catch (IOException ex) {
            ex.printStackTrace();
        }

        return rate;
    }




    private double parseAssetRateStooq(Document doc) throws IOException {
        Elements elements = new Elements();
        double rate = 0.0;
        String rateString ="";

        Pattern pattern = Pattern.compile("([0-9]+\\.?[0-9]*|[0-9]*\\.[0-9]+)");


        //elements = doc.getElementsContainingOwnText("Kurs");
        elements = doc.getElementsMatchingOwnText("Kurs");
        if(elements != null && elements.size() > 0) {
            Element e = elements.get(0);
            List<Node> nodes = e.childNodes();
            if(nodes.size() > 0 && nodes.get(0).toString().equals("Kurs")){
                if(nodes.get(2)!=null) {
                    if(nodes.get(2).childNodeSize()>0) {
                        Node node = nodes.get(2).childNode(0);
                        Matcher matcher = pattern.matcher(node.toString());
                        System.out.println(node.toString());
                        matcher.matches();
                        rateString = matcher.group();
                        //rateString = node.toString();
                    }
                }

            }
        }

        return Double.parseDouble(rateString);
    }

    private double parseAssetRateCoinGecko(String symbol, String dt) throws ParseException{

        double rate = 0.0d;
        String baseUrl = "https://api.coingecko.com/api/v3";
        String finalUrl = "";
        JsonNode root;
        JsonNode price;

        SimpleDateFormat dfIn = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dfOut = new SimpleDateFormat("dd-MM-yyyy");

        Date dateIn = dfIn.parse(dt);


        switch(symbol){

            case "BTC":
                finalUrl = baseUrl + "/coins/bitcoin/history?date=" + dfOut.format(dateIn);
                break;
            case "ETH":
                finalUrl = baseUrl + "/coins/ethereum/history?date=" + dfOut.format(dateIn);
                break;
            default:
                break;
        }


        ResponseEntity<String> response = restTemplate.getForEntity(finalUrl, String.class);

        ObjectMapper mapper = new ObjectMapper();
        try {
            root = mapper.readTree(response.getBody());
            price = root.at("/market_data/current_price/pln");
            rate = Double.parseDouble(price.asText());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        return rate;

    }


}