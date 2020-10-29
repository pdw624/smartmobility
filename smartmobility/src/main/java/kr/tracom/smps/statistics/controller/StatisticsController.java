package kr.tracom.smps.statistics.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.tracom.smps.statistics.service.StatisticsService;

@RestController
@RequestMapping("/api/v1")
public class StatisticsController {
	
	@Autowired
	private StatisticsService service;
	
	@GetMapping("/statistics")
	public Map<String, Object> selectHistoryList() {
		Map<String, Object> input = new HashMap<>();
		service.selectHistoryList(input);
		return input;
	}
	
	@GetMapping("/statistics/result")
	public Map<String, Object> selectResultList(@RequestParam String workSeq) {
		Map<String, Object> input = new HashMap<>();
		input.put("workSeq", workSeq);
		
		service.selectResultList(input);
		return input;
	}
}
