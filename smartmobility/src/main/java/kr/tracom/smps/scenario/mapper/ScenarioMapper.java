package kr.tracom.smps.scenario.mapper;

import java.util.List;
import java.util.Map;

public interface ScenarioMapper {
	List<Map<String, Object>> selectScenarioList(Map<String, Object> input);
	List<Map<String, Object>> selectDeletedScenarioList(Map<String, Object> input);
	String selectLastScenarioId(Map<String, Object> input);
	int insertScenario(Map<String, Object> input);
	int insertScenarioAction(Map<String, Object> input);
	int updateScenario(Map<String, Object> input);
	int updateDeleteScenario(Map<String, Object> input);
	int deleteScenarioAction(Map<String, Object> input);
	
	//park
	//List<Map<String, Object>> selectScenarioActionList(Map<String, Object> input);
	List<Map<String, Object>> selectReservedScenarioList(Map<String, Object> input);
	List<Map<String, Object>> selectDeletedReservedScenarioList(Map<String, Object> input);
	String selectLastReserveId(Map<String, Object> input);
	int insertReserve(Map<String, Object> input);
	int updateReserveScenario(Map<String, Object> input);
	int updateDeleteReserve(Map<String, Object> input);
	//int insertReserveScenarioAction(Map<String, Object> input);
}
