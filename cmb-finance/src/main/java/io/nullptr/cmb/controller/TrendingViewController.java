package io.nullptr.cmb.controller;


import io.nullptr.cmb.controller.dto.TrendViewRequestData;
import io.nullptr.cmb.controller.dto.GrafanaDataFrame;
import io.nullptr.cmb.service.TrendingViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/trending-view")
@RequiredArgsConstructor
public class TrendingViewController {

    private final TrendingViewService trendingViewService;

    @PostMapping
    public List<GrafanaDataFrame> trendingView(@RequestBody TrendViewRequestData requestData) {
        return trendingViewService.generate(requestData);
    }
}
