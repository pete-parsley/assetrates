package pl.datasandbox.assetrates.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.datasandbox.assetrates.dao.AssetRatesRepository;
import pl.datasandbox.assetrates.model.AssetRate;
import pl.datasandbox.assetrates.service.AssetRatesService;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Controller
public class AssetRatesController {

    @Autowired
    AssetRatesRepository assetRepository;
    @Autowired
    AssetRatesService ratesService;

    @GetMapping("/get_rates")
    public ResponseEntity getRates(@RequestParam(name="symbol") String symbol, @RequestParam(name="from") String fromDate, @RequestParam(name="to") String toDate) {

        try {
            ratesService.processAssetRates(symbol,fromDate,toDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //call to AssetRates Service

        //double rate = ratesService.getAssetRateByDate(symbol, fromDate);

        //save rates in database
        //List<AssetRate> rates = assetRepository.findAll();
        //return status
        return new ResponseEntity(HttpStatus.OK);
    }

}
