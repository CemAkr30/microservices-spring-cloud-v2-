package com.in28minutes.microservices.currencyconversionservice.controllers;


import com.in28minutes.microservices.currencyconversionservice.domains.CurrencyConversion;
import com.in28minutes.microservices.currencyconversionservice.feignClient.CurrencyExchangeProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/currency-conversion")
public class CurrencyConversionController {


    private CurrencyExchangeProxy currencyExchangeProxy;

    private RestTemplate restTemplate;

    public CurrencyConversionController(CurrencyExchangeProxy currencyExchangeProxy, RestTemplate restTemplate) {
        this.currencyExchangeProxy = currencyExchangeProxy;
        this.restTemplate = restTemplate;
    }


    @GetMapping("/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion retrieveExchangeValue(@PathVariable String from,
                                                    @PathVariable String to,
                                                    @PathVariable BigDecimal quantity) {
        Map<String,String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to",to);
        uriVariables.put("quantity", quantity.toString());

        ResponseEntity<CurrencyConversion> currencyConversionResponseEntity = restTemplate.getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}",
                CurrencyConversion.class, uriVariables);

        CurrencyConversion currencyConversion = currencyConversionResponseEntity.getBody();

        return new CurrencyConversion(1000L, from, to,quantity,
                currencyConversion.getConversionMultiple(),
                quantity.multiply(currencyConversion.getConversionMultiple()),
                currencyConversion.getEnvironment() + " " + "rest template");
    }


    @GetMapping("/feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion retrieveExchangeValueFeign(@PathVariable String from,
                                                    @PathVariable String to,
                                                    @PathVariable BigDecimal quantity) {

        CurrencyConversion currencyConversion = currencyExchangeProxy.retrieveExchangeValue(from, to);
        return new CurrencyConversion(1000L, from, to,quantity,
                currencyConversion.getConversionMultiple(),
                quantity.multiply(currencyConversion.getConversionMultiple()),
                currencyConversion.getEnvironment() + " " + "feign client");
    }
}
