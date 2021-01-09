package org.stock.track.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.stock.track.pojo.SearchResult;

@Service
public class SearchService {
    @Autowired
    private String searchPath;

    public SearchResult search(String query) {
        RestTemplate template = new RestTemplate();
        String url = searchPath + "&q=" + query;
        return template.getForObject(url, SearchResult.class);
    }
}
