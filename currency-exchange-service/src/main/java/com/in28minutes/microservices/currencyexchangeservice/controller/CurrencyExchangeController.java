package com.in28minutes.microservices.currencyexchangeservice.controller;


import com.in28minutes.microservices.currencyexchangeservice.domain.CurrencyExchange;
import com.in28minutes.microservices.currencyexchangeservice.repositories.CurrencyExchangeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/currency-exchange")
public class CurrencyExchangeController {

    private Logger logger = LoggerFactory.getLogger(CurrencyExchangeController.class);
    
    private Environment environment;
    private CurrencyExchangeRepository currencyExchangeRepository;

    public CurrencyExchangeController(Environment environment, CurrencyExchangeRepository currencyExchangeRepository) {
        this.environment = environment;
        this.currencyExchangeRepository = currencyExchangeRepository;
    }

    @GetMapping("/from/{from}/to/{to}")
    public CurrencyExchange retrieveExchangeValue(@PathVariable String from,
                                                  @PathVariable String to) {
        //CurrencyExchange exchangeValue = new CurrencyExchange(1000L, from, to, BigDecimal.valueOf(75));
        CurrencyExchange exchangeValue = currencyExchangeRepository.findByFromAndTo(from, to);
        logger.info("retrieveExchangeValue called with {} to {}", from, to);
        if (exchangeValue == null) {
            throw new RuntimeException("Unable to find data for " + from + " to " + to);
        }
        exchangeValue.setEnvironment(environment.getProperty("local.server.port"));
        return exchangeValue;
    }
}
