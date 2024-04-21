package com.in28minutes.microservices.currencyexchangeservice.repositories;

import com.in28minutes.microservices.currencyexchangeservice.domain.CurrencyExchange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyExchangeRepository
    extends JpaRepository<CurrencyExchange, Long> {
    CurrencyExchange findByFromAndTo(String from, String to);
}
