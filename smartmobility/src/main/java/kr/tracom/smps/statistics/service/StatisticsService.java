package kr.tracom.smps.statistics.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.tracom.smps.statistics.mapper.StatisticsMapper;

@Service
public class StatisticsService {
	
	@Autowired
	private StatisticsMapper mapper;
	
	public void selectHistoryList(Map<String, Object> input) {
		input.put("historyList", mapper.selectHistoryList(input));
	}
	
	public void selectResultList(Map<String, Object> input) {
		input.put("resultList", mapper.selectResultList(input));
		input.put("graphList", mapper.selectGraphList(input));
	}
}
