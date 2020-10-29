package kr.tracom.smps.base.mapper;

import java.util.Map;

public interface BaseMapper {
	public String selectCodeValue(Map<String, Object> input);
	public int updateCodeValue(Map<String, Object> input);
}
