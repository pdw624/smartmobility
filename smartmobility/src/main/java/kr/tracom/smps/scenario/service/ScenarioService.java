package kr.tracom.smps.scenario.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.tracom.smps.action.mapper.ActionMapper;
import kr.tracom.smps.handler.ExecuteHandler;
import kr.tracom.smps.scenario.controller.ScenarioController;
import kr.tracom.smps.scenario.mapper.ScenarioMapper;

@Service
public class ScenarioService {
	
	@Autowired
	private ScenarioMapper mapper;
	
	@Autowired
	private ActionMapper actionMapper;
	
	@Autowired
	private ExecuteHandler handler;
	
	public Map<String, Object> selectScenarioList(Map<String, Object> input) {
		input.put("delYn", "N");
		input.put("actionList", actionMapper.selectActionList(input));
		input.put("scenarioList", mapper.selectScenarioList(input));
		
		return input;
	}
	
	
	public Map<String, Object> selectDeletedScenarioList(Map<String, Object> input) {
		input.put("deletedScenarioList", mapper.selectDeletedScenarioList(input));
		return input;
	}
	
	public boolean insertScenario(Map<String, Object> input) {
		boolean result = false;
		String scenarioId = mapper.selectLastScenarioId(input);
		((Map<String, Object>)input.get("scenario")).put("scenarioId", scenarioId);
		
		if(mapper.insertScenario(input) > 0)
			result = true;
		
		mapper.insertScenarioAction(input);
		
		return result;
	}
	
	///////////////park
	public Map<String, Object> selectReservedScenarioList(Map<String, Object> input) {
		//input.put("delYn", "N");
		//input.put("actionList", actionMapper.selectActionList(input));
		//input.put("scenarioList", mapper.selectScenarioList(input));
		input.put("reserveList", mapper.selectReservedScenarioList(input));
		
		//System.out.println(input);
		return input;
	}
	
	public boolean insertReserve(Map<String, Object> input) {
		boolean result = false;
		
		//sessionId도 넘어왔음 reservedb에 저장
		/**               시나리오 아이디 제대로 저장됨                   **/
		//System.out.println("시나리오: "+input.get("scenario"));
		LinkedHashMap temp = (LinkedHashMap) input.get("scenario");
		/////
		List<Map<String, Object>> scenarioList = (List<Map<String, Object>>) temp.get("scenarioList");
		//System.out.println("시나리오 리스트 : "+scenarioList);
		//System.out.println("시나리오 리스트의 시나리오 ID: "+scenarioList.get(0).get("scenarioId"));
		//System.out.println("session 있니 : "+input.get("sessionId"));
		String scenarioId = (String) scenarioList.get(0).get("scenarioId");//한 예약에 여러개의 시나리오 처리하려면 scenarioIds해서 여러개 만들어야함. 한 예약에 한 시나리오만
		
//		String scenarioId = mapper.selectLastScenarioId(input);
//		System.out.println("시나리오id : "+scenarioId);
		((Map<String, Object>)input.get("scenario")).put("scenarioId", scenarioId);
		((Map<String, Object>)input.get("scenario")).put("sessionId", input.get("sessionId")); //sessionId db 저장
		/***********************************************/
		//System.out.println("예약 시나리오에 저장된 액션들 : "+mapper.selectScenarioActionList(temp));
		
		
		if(mapper.insertReserve(input) > 0)
			result = true;
		
		//mapper.insertScenarioAction(input);
		
		return result;
	}
	
//	@Transactional
//	public boolean updateReserveScenario(Map<String, Object> input, int status, LinkedHashMap<String, Object> tempMap) {
//		boolean result = false;
//		
//		//db 예약상태 업데이트
//		if(status==0) {
//			System.out.println("srTemp 넣기 전 input : "+input);
//			tempMap.put("status","대기중");
//			input.put("scenario", tempMap);
//			System.out.println("srTemp 넣은 후 input : "+input);
//			
//			//System.out.println("새로운 INPUT!!!!! : "+input);
//		}else if(status==1) {
//			tempMap.put("status","실행중");
//			input.put("scenario", tempMap);
//			//System.out.println("새로운 INPUT!!!!! : "+input);
//		}else if(status==2) {
//			tempMap.put("status","실행완료");
//			input.put("scenario", tempMap);
//			//System.out.println("새로운 INPUT!!!!! : "+input);
//		}else {
//			System.out.println("잘못된 예약 상태값");
//		}
//		System.out.println("srTemp : "+ tempMap.get("status"));
//		System.out.println("확인!!!!"+input);
//		
//		if(mapper.updateReserveScenario(input) > 0)
//			result = true;
//
//		//mapper.deleteScenarioAction(input);
//		//mapper.insertScenarioAction(input);
//
//		return result;
//	}
	
	@Transactional
	public boolean updateReserveScenario(Map<String, Object> input, String status) {
		boolean result = false;
		
		//db 예약상태 업데이트
		if(status.equals("대기중")) {
			//System.out.println("srTemp 넣기 전 input : "+input);
			((Map<String, Object>) input.get("scenario")).put("status", status);
			//System.out.println("srTemp 넣은 후 input : "+input);
			//System.out.println("새로운 INPUT!!!!! : "+input);
		}else if(status.equals("실행중")) {
			((Map<String, Object>) input.get("scenario")).put("status", status);
			//System.out.println("새로운 INPUT!!!!! : "+input);
		}else if(status.equals("실행완료")) {
			((Map<String, Object>) input.get("scenario")).put("status", status);
			//System.out.println("새로운 INPUT!!!!! : "+input);
		}else {
			System.out.println("잘못된 예약 상태값");
		}

		//System.out.println("확인!!!!"+input);
		
		if(mapper.updateReserveScenario(input) > 0)
			result = true;

		//mapper.deleteScenarioAction(input);
		//mapper.insertScenarioAction(input);

		return result;
	}
	

	public boolean deleteReserve(Map<String, Object> input) {
		input.put("canYn", "Y");
		if(mapper.updateDeleteReserve(input) > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public Map<String, Object> selectDeletedReservedScenarioList(Map<String, Object> input) {
		input.put("deletedReservedScenarioList", mapper.selectDeletedReservedScenarioList(input));
		return input;
	}
	
	
	///////////////
	
	
	@Transactional
	public boolean updateScenario(Map<String, Object> input) {
		boolean result = false;
		
		if(mapper.updateScenario(input) > 0)
			result = true;
		
		mapper.deleteScenarioAction(input);
		mapper.insertScenarioAction(input);
		
		return result;
	}
	
	public boolean deleteScenario(Map<String, Object> input) {
		input.put("delYn", "Y");
		if(mapper.updateDeleteScenario(input) > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	
	public boolean recoveryScenario(Map<String, Object> input) {
		input.put("delYn", "N");
		if(mapper.updateDeleteScenario(input) > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public void executeScenario(Map<String, Object> input) {
		input.put("workType", "SR");
		handler.execute(input);
	}
}
