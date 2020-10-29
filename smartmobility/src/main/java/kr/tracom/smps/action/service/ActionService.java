package kr.tracom.smps.action.service;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.tracom.smps.action.mapper.ActionMapper;
import kr.tracom.smps.handler.ExecuteHandler;

@Service
public class ActionService {
	private static final Log LOGGER = LogFactory.getLog( ActionService.class );
	
	@Autowired
	private ActionMapper mapper;
	
	@Autowired
	private ExecuteHandler handler;
	
	public Map<String, Object> selectActionList(Map<String, Object> input) {
		input.put("delYn", "N");
		input.put("actionList", mapper.selectActionList(input));
		return input;
	}
	
	public Map<String, Object> selectDeletedActionList(Map<String, Object> input) {
		input.put("delYn", "Y");
		input.put("deletedActionList", mapper.selectActionList(input));
		return input;
	}
	
	public boolean insertAction(Map<String, Object> input) {
		if(mapper.insertAction(input) > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean updateAction(Map<String, Object> input) {
		if(mapper.updateAction(input) > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean deleteAction(Map<String, Object> input) {
		input.put("delYn", "Y");
		if(mapper.updateDeleteAction(input) > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean recoveryAction(Map<String, Object> input) {
		input.put("delYn", "N");
		if(mapper.updateDeleteAction(input) > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	@Transactional
	public void executeAction(Map<String, Object> input) {
		input.put("workType", "AT");
		handler.execute(input);
	}
}
