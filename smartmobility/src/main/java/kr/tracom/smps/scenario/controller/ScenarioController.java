package kr.tracom.smps.scenario.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Null;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import kr.tracom.smps.action.controller.ActionController;
import kr.tracom.smps.handler.DataHandler;
import kr.tracom.smps.handler.ExecuteHandler;
import kr.tracom.smps.handler.ReserveHandler;
import kr.tracom.smps.scenario.mapper.ScenarioMapper;
import kr.tracom.smps.scenario.service.ScenarioService;

@RestController
@RequestMapping("/api/v1")
public class ScenarioController {

	//park
	public static boolean gExecuteFlag;//true면 실행중, false면 미실행

	@Autowired
	private ReserveHandler handler; 
	//
	
	@Autowired
	private ScenarioService service; 
	
	// 시나리오 리스트 조회
	@GetMapping("/scenario")
	public Map<String, Object> selectScenarioList() {
		Map<String, Object> input = new HashMap<String, Object>();
		return service.selectScenarioList(input);
	}
		
	// 예약 시나리오 리스트 조회 - 박대원
	@GetMapping("/scenario/reserve")
	public Map<String, Object> selectReservedScenarioList() {
		Map<String, Object> input = new HashMap<String, Object>();
		return service.selectReservedScenarioList(input);
	}
	
	// 삭제된 예약 시나리오 조회 - 박대원
	@GetMapping("/scenario/reserve/deleted")
	public Map<String, Object> selectDeletedReservedScenarioList() {
		Map<String, Object> input = new HashMap<String, Object>();
		return service.selectDeletedReservedScenarioList(input);
	}	
	
	// 실행체크 - 박대원
	@PostMapping("/scenario/check")
	public Map<String,Object> checkIsRun(){
		Map<String, Object> tempInput = new HashMap<String, Object>();
		if (gExecuteFlag == true || ActionController.exFlag == true) {
			tempInput.put("executeFlag", true);
			return tempInput;
		}
		return tempInput;
	}
	
	// 삭제된 동작 시나리오 조회
	@GetMapping("/scenario/deleted")
	public Map<String, Object> selectDeletedScenarioList() {
		Map<String, Object> input = new HashMap<String, Object>();
		return service.selectDeletedScenarioList(input);
	}
	
	// 시나리오 삽입
	@PostMapping("/scenario")
	public boolean insertScenario(@RequestBody Map<String, Object> input) {
		return service.insertScenario(input);
	}
	
	// 시나리오 수정
	@PutMapping("/scenario")
	public boolean updateScenario(@RequestBody Map<String, Object> input) {
		return service.updateScenario(input);
	}
	
	// 시나리오 삭제
	@DeleteMapping("/scenario")
	public boolean deleteScenario(@RequestBody Map<String, Object> input) {
		return service.deleteScenario(input);
	}
	
	// 시나리오 복원
	@PostMapping("/scenario/recovery")
	public boolean recoveryScenario(@RequestBody Map<String, Object> input) {
		return service.recoveryScenario(input);
	}
	
	
	// 시나리오 실행
	@PostMapping("/scenario/execute")
	public void executeScenario(@RequestBody Map<String, Object> input, HttpServletRequest request) {
		input.put("sessionId", request.getSession().getId());
		ActionController.exFlag=true;
		service.executeScenario(input);
		ActionController.exFlag=false;

		//System.out.println("Scenario List : "+input.get("scenarioList"));
	}
	
	// 시나리오 중지
	@PostMapping("/scenario/pause")
	public void pauseScenario(HttpServletRequest request) {
		DataHandler handler = ExecuteHandler.sessionHandlerMap.get(request.getSession().getId());
		handler.setFlag(true);
		handler.processInsert();
	}
	
	
	// 예약취소 - 박대원
	@DeleteMapping("/scenario/reserve")
	public boolean cancelReserve(@RequestBody Map<String, Object> input) {		
		return service.deleteReserve(input);
	}
	
	// 시나리오 실행 확인 - 박대원 
	@PostMapping("/scenario/checkScenario")
	public Map<String, Object> checkScenario(@RequestBody Map<String, Object> input) {
		return handler.checkScenario(input);
	}

	// 시나리오 예약 - 박대원
	@PostMapping("/scenario/reserve")
	public Map<String, Object> reserveScenario(@RequestBody Map<String, Object> input,HttpServletRequest request) {
		//예약실행중인지 검사 (예약 실행중이면 종료)
		if (gExecuteFlag == true || ActionController.exFlag == true) {
			Map<String, Object> tempInput = new HashMap<String, Object>();//클라이언트에 플래그 값들 보낼 맵
			tempInput = new HashMap<String, Object>();
			tempInput.put("executeFlag", true);
			
			return tempInput;
		}

		return handler.reserveScenario(input, request);
	}
	
}
