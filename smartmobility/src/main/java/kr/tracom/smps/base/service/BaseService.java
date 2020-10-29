package kr.tracom.smps.base.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.tracom.smps.base.mapper.BaseMapper;

@Service
public class BaseService {
	@Autowired
	private BaseMapper mapper;
	
	public String selectCodeValue(Map<String, Object> input) {
		return mapper.selectCodeValue(input);
	}
	
	public boolean updateCodeValue(Map<String, Object> input) {
		if(mapper.updateCodeValue(input) > 0)
			return true;
		else
			return false;
	}
}
