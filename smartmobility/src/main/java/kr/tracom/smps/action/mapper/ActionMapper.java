package kr.tracom.smps.action.mapper;

import java.util.List;
import java.util.Map;

public interface ActionMapper {
	List<Map<String, Object>> selectActionList(Map<String, Object> input);
	int insertAction(Map<String, Object> input);
	int updateAction(Map<String, Object> input);
	int updateDeleteAction(Map<String, Object> input);
}
