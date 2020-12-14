package kr.tracom.smps.statistics.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
	
	public static LinkedHashMap<String, Object> sortMapByValue(Map<String, Object> map) {
	    List<Map.Entry<String, Object>> entries = new LinkedList<>(map.entrySet());
	    Collections.sort(entries, (o1, o2) -> ((String) o1.getValue()).compareTo((String) o2.getValue()));

	    LinkedHashMap<String, Object> result = new LinkedHashMap<>();
	    for (Map.Entry<String, Object> entry : entries) {
	        result.put(entry.getKey(), entry.getValue());
	    }
	    return result;
	}
}
