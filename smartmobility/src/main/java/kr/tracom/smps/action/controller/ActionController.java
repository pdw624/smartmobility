package kr.tracom.smps.action.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.tracom.smps.action.service.ActionService;
import kr.tracom.smps.handler.DataHandler;
import kr.tracom.smps.handler.ExecuteHandler;
import kr.tracom.smps.scenario.controller.ScenarioController;

@RestController
@RequestMapping("/api/v1")
public class ActionController {
	
	//park
	public static boolean exFlag;
	
	@Autowired
	private ActionService service;
	
	// 실행체크 - 박대원
	@PostMapping("/action/check")
	public Map<String,Object> checkIsRun(){
		Map<String, Object> tempInput = new HashMap<String, Object>();
		if (ScenarioController.gExecuteFlag == true || ActionController.exFlag == true) {
			tempInput.put("executeFlag", true);
			return tempInput;
		}
		return tempInput;
	}
	
	// 동작 리스트 조회
	@GetMapping("/action")
	public Map<String, Object> selectActionList(@RequestHeader Map<String, Object> header, HttpServletRequest request) {
		Map<String, Object> input = new HashMap<String, Object>();
		/*
		 * System.out.println(
		 * "======================================================================================================================================"
		 * ); System.out.println("header: " + header); System.out.println(
		 * "======================================================================================================================================"
		 * ); System.out.println("body: " + request.getQueryString());
		 * System.out.println(
		 * "======================================================================================================================================"
		 * );
		 */
		
//		//park
//		Map<String, Object> rsrvList = new HashMap<String, Object>();
//		rsrvList = ScenarioController.selectReservedScenarioList();
//		System.out.println("예약 리스트" + rsrvList);
		
		return service.selectActionList(input);
	}
	
	// 삭제된 동작 리스트 조회
	@GetMapping("/action/deleted")
	public Map<String, Object> selectDeletedActionList() {
		Map<String, Object> input = new HashMap<String, Object>();
		return service.selectDeletedActionList(input);
	}
	
	// 동작 삽입
	@PostMapping("/action")
	public boolean insertAction(@RequestBody Map<String, Object> input) {
		//System.out.println(input);
		return service.insertAction(input);
	}
	
	// 동작 수정
	@PutMapping("/action")
	public boolean updateAction(@RequestBody Map<String, Object> input) {
		//System.out.println(input);
		return service.updateAction(input);
	}
	
	// 동작 삭제
	@DeleteMapping("/action")
	public boolean deleteAction(@RequestBody Map<String, Object> input) {
		return service.deleteAction(input);
	}
	
	// 동작 복원
	@PostMapping("/action/recovery")
	public boolean recoveryAction(@RequestBody Map<String, Object> input) {
		return service.recoveryAction(input);
	}
	
	// 동작 실행
	@PostMapping("/action/execute")
	public void executeAction(@RequestBody Map<String, Object> input, HttpSession session) {
		input.put("sessionId", session.getId());
		System.out.println("sessionId : "+input.get("sessionId"));
		exFlag = true;//
		//System.out.println("시작!!"+exFlag);//
		service.executeAction(input);
		exFlag = false;//
		//System.out.println("종료!!"+exFlag);//
		
	}
	
	// 동작 중지
	@PostMapping("/action/pause")
	public void pauseAction(HttpSession session) {
		DataHandler handler = ExecuteHandler.sessionHandlerMap.get(session.getId());
		System.out.println(session.getId() + ", " + handler);
		handler.setFlag(true);
		handler.processInsert();
	}
}
