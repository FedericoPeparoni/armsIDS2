package ca.ids.oxr.client.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ca.ids.oxr.client.dto.Context;
import ca.ids.oxr.client.dto.LatestExchangeRates;

import javax.validation.Valid;
import java.util.Arrays;

@Service
public class OpenExchangeRatesService {

    private final RestTemplate restTemplate;

    @Value("${oxr.client.provider.latest:https://openexchangerates.org/api/latest.json}")
    private String serviceLatestRates;

    public OpenExchangeRatesService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
            .build();
    }

    public LatestExchangeRates getLatestExchangeRates (@Valid final Context context) {
        final StringBuilder queryParams = new StringBuilder(serviceLatestRates)
                .append("?app_id=").append(context.getAppId());
        if (StringUtils.isNotEmpty(context.getBase())) {
            queryParams.append(";base_id=").append(context.getBase());
        }
        if (StringUtils.isNotEmpty(context.getSymbols())) {
            queryParams.append(";symbols=").append(context.getSymbols());
        }
        final ResponseEntity<LatestExchangeRates> respEntity = restTemplate.exchange(queryParams.toString(),
                HttpMethod.GET, getDefaultHttpEntity(), LatestExchangeRates.class);
        return respEntity.getBody();
    }

    private HttpEntity<String> getDefaultHttpEntity() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>("parameters", headers);
    }
}
