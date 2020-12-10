package pl.datasandbox.assetrates.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Controller
public class AssetRatesController {


    @GetMapping("/get_rates")
    public String currencyRemove(@RequestParam(name="symbol") String symbol, @RequestParam(name="from") String fromDate, @RequestParam(name="to") String toDate) {

        return "asset_rates";
    }

}
