package kr.tracom.smps.scenario.controller;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.tracom.smps.handler.DataHandler;
import kr.tracom.smps.handler.ExecuteHandler;
import kr.tracom.smps.scenario.mapper.ScenarioMapper;
import kr.tracom.smps.scenario.service.ScenarioService;

@RestController
@RequestMapping("/api/v1")
public class ScenarioController {
	
	//park
	public static LinkedHashMap srTemp;
	public static ArrayList<String> rsrvList = new ArrayList<String>();
	public static boolean executeFlag;//true면 실행, false면 미실행
	public static boolean overlapFlag;//true면 미실행, false면 실행
	@Autowired
	private ScenarioMapper mapper;
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
		//System.out.println("예약조회!!!!!!!!!!!");
		
//		System.out.println("키 값 확인"+selectDeletedReservedScenarioList().get("deletedReservedScenarioList"));
//		System.out.println("타입확인"+selectDeletedReservedScenarioList().get("deletedReservedScenarioList").getClass());
//		ArrayList<Map<String,Object>> temp = (ArrayList<Map<String, Object>>) selectDeletedReservedScenarioList().get("deletedReservedScenarioList");
//		System.out.println("임시저장값 확인 :"+temp);
//		System.out.println("임시저장값 크기확인 :"+temp.size());
//		System.out.println(temp.get(0));
		
		return service.selectReservedScenarioList(input);
	}
	
	// 삭제된 동작 시나리오 조회 - 박대원
	@GetMapping("/scenario/reserve/deleted")
	public Map<String, Object> selectDeletedReservedScenarioList() {
		Map<String, Object> input = new HashMap<String, Object>();
		return service.selectDeletedReservedScenarioList(input);
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
		service.executeScenario(input);
		System.out.println("Scenario List : "+input.get("scenarioList"));
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
		System.out.println("조회한 목록 :"+selectReservedScenarioList());
		System.out.println("삭제조회목록 : "+selectDeletedReservedScenarioList());
		return service.deleteReserve(input);
	}
	
	
//	@GetMapping("/scenario/reserve")
//	public boolean executeFlag(boolean eF) {
//		return eF;
//	}
//	@GetMapping("/scenario/reserve")
//	public boolean overlapFlag(boolean oF) {
//		return oF;
//	}
	
	// 시나리오 예약 - 박대원 synchronized
	@PostMapping("/scenario/reserve")
	public void reserveScenario(@RequestBody Map<String, Object> input,HttpServletRequest request) {
		//service.updateScenario(input);
		overlapFlag = false;
		
		
		System.out.println("초기 input :"+input);
		service.insertReserve(input);//여기서 RSRV_ID 생성됨
		
		//KEY 확인용
		Set set = input.keySet();
		Iterator iterator = set.iterator();
		
		while(iterator.hasNext()) {
			String key = (String)iterator.next();
			//System.out.println("input key : "+ key);
		}
		//Key의 Value 확인용
		//System.out.println(input.get("scenario"));
		//System.out.println(input.get("actionIds"));
		///////////////////////////////////////////////////////////////
		//받아온 예약 내용 저장 
		srTemp = new LinkedHashMap();
		String reserveTime;
		Map<String, Object> scenarioList = new HashMap<String,Object>();
		
		srTemp = (LinkedHashMap) input.get("scenario");
		reserveTime = (String) srTemp.get("reserveTime");
		scenarioList.put("scenarioList", srTemp.get("scenarioList"));
		scenarioList.put("sessionId", request.getSession().getId());
		
		
		//예약시간 중복 확인

		ArrayList<Map<String,Object>> overlapCheck = (ArrayList<Map<String, Object>>) selectReservedScenarioList().get("reserveList");
		for(int k=0; k<overlapCheck.size()-1;k++){
			if(reserveTime.equals(overlapCheck.get(k).get("reserveTime"))){
				System.out.println("지금 예약한 시간에 이미 다른 예약이 있습니다. >> " +overlapCheck.get(k).get("reserveId")+" "+overlapCheck.get(k).get("reserveTime"));
				overlapFlag = true;
			}
		}
		/***********************************여기에서 overlapFlag 전송할 수 있도록??*********************************************/
		
		String reserveId = mapper.selectLastReserveId(input);//맨 마지막 예약 아이디
		((Map<String, Object>)input.get("scenario")).put("reserveId", reserveId);//이제 input{scenario={rsrvId='1'}} 추가됨
		
		//0: 대기중, 1: 실행중, 2: 실행완료
		service.updateReserveScenario(input,0,srTemp); // 대기중으로 설정
		
		
		//받은 String 년월일시분 split
		int ymd_hm[] = new int[5];
		
		String reserve[] = reserveTime.split("_");
		String reserveYMD[] = reserve[0].split("/");//3개
		String reserveHM[] = reserve[1].split(":");//2개
		
		
		ymd_hm[0] = Integer.parseInt(reserveYMD[0]);
		ymd_hm[1] = Integer.parseInt(reserveYMD[1]);
		ymd_hm[2] = Integer.parseInt(reserveYMD[2]);
		
		ymd_hm[3] = Integer.parseInt(reserveHM[0]);
		ymd_hm[4] = Integer.parseInt(reserveHM[1]);
		
		
		Calendar date = Calendar.getInstance();
		date.set(ymd_hm[0],ymd_hm[1]-1,ymd_hm[2],ymd_hm[3],ymd_hm[4],0);
		
		
		String rsrvIndicator = reserveId+","+date.getTimeInMillis();//현재 id, time 기억
		if(overlapFlag==true) {
			System.out.println("중복 예약이므로 예약목록에 담지 않겠습니다.");
		}else {
			rsrvList.add(rsrvIndicator); //예약 배열에 추가
		}
		
		//System.out.println("리스트!!! "+rsrvIndicator);
		//System.out.println("내가 설정한 시간 "+date.getTimeInMillis());
		//db 예약상태 업데이트
		
		
		
		//timer 작업
		Timer timer = new Timer();
		TimerTask timerTask = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				//삭제된 예약id와 예약시간 [{id,time}, {id,time}, {id,time}]
				ArrayList<Map<String,Object>> listTemp = (ArrayList<Map<String, Object>>) selectDeletedReservedScenarioList().get("deletedReservedScenarioList");
				//System.out.println("삭제조회목록222 : "+selectDeletedReservedScenarioList());
				//boolean executeFlag = true;
				executeFlag = true;
	
			
				String myTime;
				String timerTemp = String.valueOf(System.currentTimeMillis());//timer시간(밀리초포함)
				String timerTime = timerTemp.substring(0, timerTemp.length()-3);//timer시간 (밀리초제거)
				
				//비교할 시간 문자열 설정
				for(int i=0; i<rsrvList.size();i++) {
					String temp[]=rsrvList.get(i).split(",");//id, time 분리
					System.out.println(temp[0]+" "+temp[1]);
					myTime = temp[1].substring(0, temp[1].length()-3);//내 시간(밀리초제거)
							
					
					//[rsrvId -- rsrvTime]
					//내가 설정한 시간과 타이머의 시간이 같으면 내 rsrvId를 가져온 후 Map에 추가 - 이 작업을 하지 않으면 db의 마지막 행의 상태만 바뀌게 됨
					if(myTime.equals(timerTime)) {
						//id 값 가져왔음 -> 예약 실행되는 현재 인덱스(temp[0]) 파악함 , 검사로직, 추가됨
						for(int j=0; j<listTemp.size(); j++) {		
							//System.out.println("temp[0]형식 : "+temp[0].getClass()+", listTemp.get(j)형식 : "+listTemp.get(j).get("reserveId").getClass());
							//System.out.println("t/f판단"+temp[0].equals(listTemp.get(j).get("reserveId"))+",temp[0] : "+temp[0]+", listTemp.get(j): "+listTemp.get(j).get("reserveId"));
							if(temp[0].equals(String.valueOf(listTemp.get(j).get("reserveId")))) {
								System.out.println("여기 타니?? "+listTemp.get(j).get("reserveId"));
								//timer.cancel();
								executeFlag = false;
							}
						}
						
						if(executeFlag) {
							//원래 if(myTime.equals())안에 있던 함수들
							((Map<String, Object>)input.get("scenario")).put("reserveId", temp[0]);//update할 Map에 rsrvId 갱신
							srTemp.put("reserveId", temp[0]);//복사할 Map인 srTemp에도 rsrvId 갱신
							service.updateReserveScenario(input, 1, srTemp);//상태갱신 
							System.out.println("예약 시나리오 시작");
							service.executeScenario(scenarioList);//실행
							
						}
						
					}

				}
			}

			//input.put("sessionId", request.getSession().getId());

			//예약 기본키 삽입

			//service.executeScenario(scenarioList);
		
		};

		timer.schedule(timerTask, new Date(date.getTimeInMillis()), 24*60*60*1000);
		
	
		
//		//timer 작업
//		Timer timer = new Timer();
//		TimerTask timerTask = new TimerTask() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				
//				//삭제된 예약id와 예약시간 [{id,time}, {id,time}, {id,time}]
//				ArrayList<Map<String,Object>> listTemp = (ArrayList<Map<String, Object>>) selectDeletedReservedScenarioList().get("deletedReservedScenarioList");
//				System.out.println("삭제조회목록222 : "+selectDeletedReservedScenarioList());
//				//boolean executeFlag = true;
//				executeFlag = true;
//				
//				
//				String myTime;
//				String timerTemp = String.valueOf(System.currentTimeMillis());//timer시간(밀리초포함)
//				String timerTime = timerTemp.substring(0, timerTemp.length()-3);//timer시간 (밀리초제거)
//				
//				//비교할 시간 문자열 설정
//				for(int i=0; i<rsrvList.size();i++) {
//					String temp[]=rsrvList.get(i).split(",");//id, time 분리
//					System.out.println(temp[0]+" "+temp[1]);
//					myTime = temp[1].substring(0, temp[1].length()-3);//내 시간(밀리초제거)
//					
//					//[rsrvId -- rsrvTime]
//					//내가 설정한 시간과 타이머의 시간이 같으면 내 rsrvId를 가져온 후 Map에 추가 - 이 작업을 하지 않으면 db의 마지막 행의 상태만 바뀌게 됨
//					if(myTime.equals(timerTime)) {
//						//id 값 가져왔음 -> 예약 실행되는 현재 인덱스(temp[0]) 파악함 , 검사로직, 추가됨
//						for(int j=0; j<listTemp.size(); j++) {		
//							//System.out.println("temp[0]형식 : "+temp[0].getClass()+", listTemp.get(j)형식 : "+listTemp.get(j).get("reserveId").getClass());
//							System.out.println("t/f판단"+temp[0].equals(listTemp.get(j).get("reserveId"))+",temp[0] : "+temp[0]+", listTemp.get(j): "+listTemp.get(j).get("reserveId"));
//							if(temp[0].equals(String.valueOf(listTemp.get(j).get("reserveId")))) {
//								System.out.println("여기 타니?? "+listTemp.get(j).get("reserveId"));
//								//timer.cancel();
//								executeFlag = false;
//							}
//						}
//						
//						if(executeFlag) {
//							//원래 if(myTime.equals())안에 있던 함수들
//							((Map<String, Object>)input.get("scenario")).put("reserveId", temp[0]);//update할 Map에 rsrvId 갱신
//							srTemp.put("reserveId", temp[0]);//복사할 Map인 srTemp에도 rsrvId 갱신
//							service.updateReserveScenario(input, 1, srTemp);//상태갱신 
//							System.out.println("예약 시나리오 시작");
//							service.executeScenario(scenarioList);//실행
//						}
//						
//					}
//
//				}
//
//				
//				//input.put("sessionId", request.getSession().getId());
//
//				//예약 기본키 삽입
//
//				//service.executeScenario(scenarioList);
//			}
//		};
//		
//		
//		timer.schedule(timerTask, new Date(date.getTimeInMillis()), 24*60*60*1000);
		
		
	}
}
