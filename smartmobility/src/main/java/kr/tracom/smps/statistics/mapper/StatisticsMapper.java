package kr.tracom.smps.statistics.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.Cacheable;

public interface StatisticsMapper {
	public List<Map<String, Object>> selectHistoryList(Map<String, Object> input);
	@Cacheable(value = "historyResult", key = "#input?.get('workSeq')")
	public List<Map<String, Object>> selectResultList(Map<String, Object> input);
	public List<Map<String, Object>> selectGraphList(Map<String, Object> input);
	public String selectLastWorkSeq(Map<String, Object> input);
	public int insertHistory(Map<String, Object> input);
	public int insertResult(Map<String, Object> input);
}
