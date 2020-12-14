package kr.tracom.smps.handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import kr.tracom.smps.action.controller.ActionController;
import kr.tracom.smps.scenario.controller.ScenarioController;
import kr.tracom.smps.scenario.mapper.ScenarioMapper;
import kr.tracom.smps.scenario.service.ScenarioService;

@Service
public class ReserveHandler {
	//park
	//public static boolean gExecuteFlag;//true면 실행중, false면 미실행
	//public static LinkedHashMap srTemp;
	public static ArrayList<String> rsrvList = new ArrayList<String>();
	public static ArrayList<Long> rsrvTimeArr = new ArrayList<>(); 
	int index;
	
	@Autowired
	private ScenarioController controller;
	//
	
	@Autowired
	private ScenarioMapper mapper;
	//
	
	@Autowired
	private ScenarioService service; 
	
	
	
	
	// create 쿼리 실행 - 박대원
	//@PostMapping("/scenario/query")
	public void createQuery() {
		System.out.println("Start Creating Query...");
		Connection con = null;
		
		Statement stmt=null;
		//Statement stmt2=null;
		
		ResultSet rs = null;
		//ResultSet rs2 = null;
		
		String dbFileUrl = "jdbc:sqlite:smartmobility.db";
		
		//rsrv테이블 생성
		try {
			Class.forName("org.sqlite.JDBC");
			con=DriverManager.getConnection(dbFileUrl);
			System.out.println("SQLite DB connected");
			
			stmt = con.createStatement();
			rs = stmt.executeQuery("CREATE TABLE IF NOT EXISTS \"SMPS_COMM_RSRV\" (\r\n" + 
					"	\"RSRV_ID\"	INTEGER NOT NULL,\r\n" + 
					"	\"SNR_ID\"	TEXT NOT NULL,\r\n" + 
					"	\"SNR_NAME\"	TEXT NOT NULL,\r\n" + 
					"	\"RSRV_TIME\"	TEXT NOT NULL,\r\n" + 
					"	\"RSRV_END\"	TEXT NOT NULL,\r\n" + 
					"	\"STATUS\"	TEXT,\r\n" + 
					"	\"CAN_YN\"	TEXT NOT NULL DEFAULT 'N',\r\n" + 
					"	\"SESSION_ID\"	TEXT,\r\n" + 
					"	PRIMARY KEY(\"RSRV_ID\" AUTOINCREMENT)\r\n" + 
					");");
			
		}catch(Exception e) {
			System.out.println(e);
		}
		
		try {
			Class.forName("org.sqlite.JDBC");
			con=DriverManager.getConnection(dbFileUrl);
			System.out.println("SQLite DB connected");

			stmt = con.createStatement();
			rs = stmt.executeQuery("ALTER TABLE \"SMPS_COMM_ACT\" ADD \"TIMESET\" NUMERIC NOT NULL DEFAULT 1;");
			
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	

	//Initialize할 때 예약 목록 있는지 확인
	@EventListener(ApplicationReadyEvent.class)
	public ArrayList<Map<String, Object>> reserveScanAfterStartup() {
		
		//힙메모리 확인
		long heapSize = Runtime.getRuntime().totalMemory();
        //System.out.println("Heap Size : " + heapSize);
        System.out.println("Heap Size(M) : " + heapSize / (1024 * 1024) + " MB");
		
	    //System.out.println("hello world, I have just started up");
	    //예약 테이블이 없다면 자동 생성해줌
	    createQuery();

	    //예약 리스트 받아오기
	    Map<String, Object> reserveInput = new HashMap<String, Object>();
	    reserveInput = service.selectReservedScenarioList(reserveInput);
	    
	    //예약 객체별로 확인
	    ArrayList<Map<String,Object>> rsrvList = new ArrayList<>();
	    ArrayList<Map<String,Object>> rsrvYetList = new ArrayList<>();
	    rsrvList = (ArrayList<Map<String, Object>>) reserveInput.get("reserveList");
	    
	    //대기중인 예약이 있다면 모두 가져옴
	    for(int i=0; i<rsrvList.size(); i++) {
	    	//System.out.println((i+1)+"번 예약 : "+rsrvList.get(i));
	    	if(rsrvList.get(i).get("status").equals("대기중")) {
	    		rsrvYetList.add(rsrvList.get(i));
	    	}
	    }
	    
	    //미실행 예약 실행 함수
	    reserveScenario(rsrvYetList);
	    
	    //System.out.println("아직 실행 안된 예약들 "+rsrvYetList);
	    return rsrvYetList;
	}
	
	

	
	
	//시나리오 예약
	public Map<String, Object> reserveScenario(Map<String, Object> input, HttpServletRequest request) {
//			//예약실행중인지 검사 (예약 실행중이면 종료)
//			if (gExecuteFlag == true || ActionController.exFlag == true) {
//				Map<String, Object> tempInput = new HashMap<String, Object>();//클라이언트에 플래그 값들 보낼 맵
//				tempInput = new HashMap<String, Object>();
//				tempInput.put("executeFlag", true);
//				
//				return tempInput;
//			}
		
		boolean isOverlapFlag = false; //중복 확인 Flag

		String reserveTime; //받아온 예약 내용 저장 

		Map<String, Object> scenarioList = new HashMap<String,Object>(); //실행할 때 넘겨줄 시나리오 리스트
		
		//input->scenario-> reserveTime, scenarioList...등을 꺼내기 위해 만든 임시 맵
		Map<String, Object> inputScenario = (Map<String, Object>) input.get("scenario");
			
		reserveTime = (String) inputScenario.get("reserveTime"); //예약시간 저장
		
		Calendar date = Calendar.getInstance();
		date = timeSplit(reserveTime); //예약시간 String 값을 밀리초로 변경 후 date에 저장
		
		//예약종료시간 계산 후 Map 저장 >> db에 넣기 위한 용도 
		long executeTime = executeTimeCalculator(inputScenario, date);
		((HashMap) input.get("scenario")).put("reserveEnd", executeTime);
		
		
		//최초 예약 시 세션 아이디 설정해서 맵에 넣어줌 >> reserve Table에도 저장해야함
		input.put("sessionId", request.getSession().getId());
		
		//여기서 RSRV_ID 생성됨 - Auto Increment
		service.insertReserve(input);
			
		scenarioList.put("scenarioList", inputScenario.get("scenarioList"));
		scenarioList.put("sessionId", request.getSession().getId());
		
		//예약시간 중복 확인
		ArrayList<Map<String,Object>> overlapCheckMap = (ArrayList<Map<String, Object>>) controller.selectReservedScenarioList().get("reserveList");
		
		//System.out.println("예약시나리오리스트 : "+ selectReservedScenarioList());
		//System.out.println("overlapCheckMap : "+ overlapCheckMap);
		
		for(int k=0; k<overlapCheckMap.size()-1;k++){
			if(reserveTime.equals(overlapCheckMap.get(k).get("reserveTime"))){
				//System.out.println("지금 예약한 시간에 이미 다른 예약이 있습니다. >> " +overlapCheck.get(k).get("reserveId")+" "+overlapCheck.get(k).get("reserveTime"));
				isOverlapFlag = true;
			}
		}
		/***********************************여기에서 overlapFlag 전송할 수 있도록*********************************************/
		String reserveId = mapper.selectLastReserveId(input);//맨 마지막 예약 아이디
		ArrayList<Integer> reserveIds = new ArrayList<>();
		reserveIds.add(Integer.parseInt(reserveId));
		
		//예약클릭 시  중복이라면 여기서 종료
		if (isOverlapFlag == true) {
			Map<String, Object> tempInput = new HashMap<String, Object>();
			tempInput.put("overlaps", true);
			
			input.put("reserveIds", reserveIds);
			service.deleteReserve(input);//중복 db 삭제 되는지 확인
			System.out.println("reserve overlap!"+input);
			return tempInput;
		}
		/***********************************여기에서 overlapFlag 전송할 수 있도록*********************************************/
		
		((Map<String, Object>)input.get("scenario")).put("reserveId", reserveId);//이제 input{scenario={rsrvId='1'}} 추가됨

		String rsrvIndicator = reserveId+","+date.getTimeInMillis();//현재 예약 id, time 기억
		
		//예약끼리 겹치는지 판단 후 플래그 생성
		isOverlapFlag = reserveOverlap(overlapCheckMap, isOverlapFlag);
		
		//예약클릭 시  중복이라면 여기서 종료
		if (isOverlapFlag == true) {
			Map<String, Object> tempInput = new HashMap<String, Object>();
			tempInput.put("overlaps", true);
			
			input.put("reserveIds", reserveIds);
			service.deleteReserve(input);//중복 db 삭제 되는지 확인
			System.out.println("reserve overlap!"+input);
			return tempInput;
		}
		
		
		service.updateReserveScenario(input,"대기중"); // 대기중으로 설정

		//예약날짜들 전역 예약날짜배열에 삽입
		rsrvTimeArr.add(date.getTimeInMillis());
		
		if(isOverlapFlag==true) {
			System.out.println("중복 예약이므로 예약목록에 담지 않습니다.");
		}else {
			rsrvList.add(rsrvIndicator); //예약 배열에 추가
		}
		
		try {
			//timer 작업
			Timer timer = new Timer();
			TimerTask timerTask = new TimerTask() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					//예약시간과 실행시간이 같으면 실행완료예약이니 예약 배열에서 삭제
					for(int i=0; i<rsrvTimeArr.size();i++) {
						if(rsrvTimeArr.get(i)-100 < System.currentTimeMillis() && rsrvTimeArr.get(i)+100 > System.currentTimeMillis()) {
							rsrvTimeArr.remove(i);
						}
					}
					
					//삭제된 예약id와 예약시간 [{id,time}, {id,time}, {id,time}]
					ArrayList<Map<String,Object>> deletedReserveList = (ArrayList<Map<String, Object>>) controller.selectDeletedReservedScenarioList().get("deletedReservedScenarioList");

					boolean executeFlag = true;//중복시 하나만 실행하기 위한 플래그
					//
					ActionController.exFlag = true;
					ScenarioController.gExecuteFlag=true;//예약 실행될 때 예약실행플래그 on
					
					String myTime;
					String timerTemp = String.valueOf(System.currentTimeMillis());//timer시간(밀리초포함)
					String timerTime = timerTemp.substring(0, timerTemp.length()-3);//timer시간 (밀리초제거)
					
					//비교할 시간 문자열 설정
					for(int i=0; i<rsrvList.size();i++) {
						String temp[]=rsrvList.get(i).split(",");//id, time 분리
						//System.out.println(temp[0]+" "+temp[1]);
						myTime = temp[1].substring(0, temp[1].length()-3);//내 시간(밀리초제거)
								
						
						//[rsrvId -- rsrvTime]
						//내가 설정한 시간과 타이머의 시간이 같으면 내 rsrvId를 가져온 후 Map에 추가 - 이 작업을 하지 않으면 db의 마지막 행의 상태만 바뀌게 됨
						if(myTime.equals(timerTime)) {
							//id 값 가져왔음 -> 예약 실행되는 현재 인덱스(temp[0]) 파악함 , 검사로직, 추가됨
							for(int j=0; j<deletedReserveList.size(); j++) {		

								
								//삭제된 예약들이 실행되지 않도록 구분
								if(temp[0].equals(String.valueOf(deletedReserveList.get(j).get("reserveId")))) {
									System.out.println("삭제된 예약과 중복됩니다. "+deletedReserveList.get(j).get("reserveId"));
									executeFlag = false;
								}
							}
							
							if(executeFlag) {
								//원래 if(myTime.equals())안에 있던 함수들
								((Map<String, Object>)input.get("scenario")).put("reserveId", temp[0]);//update할 Map에 rsrvId 갱신
								service.updateReserveScenario(input,"실행중");//상태갱신 test
								System.out.println("예약 시나리오 시작");
								service.executeScenario(scenarioList);//실행 -> 실행 플래그 true
								service.updateReserveScenario(input,"실행완료");//상태갱신 test
							}
							
							//실행완료 후 예약배열에서 삭제
							rsrvList.remove(i);

						}

					}
					//타이머 끝나면 실행 플래그 false
					ScenarioController.gExecuteFlag=false;
					ActionController.exFlag = false;
				}
			};

			timer.schedule(timerTask, new Date(date.getTimeInMillis()));
		}catch(Exception e) {
			System.out.println(e);
			throw(e);
		}
		
		
		return input;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	//첫 실행 시 미실행된 예약들 다시 구동
	public void reserveScenario(ArrayList<Map<String, Object>> rsrvYetList) {
		
		boolean isOverlapFlag = false;

		String reserveTime;
		String reserveId;

		Map<String, Object> mScenario = new HashMap<String,Object>();//모든 미완료 예약정보
		List<Map<String, Object>> lScenarioList = new ArrayList<Map<String, Object>>(); 
		
		Map<String, Object> tempScenarioList = new HashMap<String,Object>();//실행시킬 미완료 예약정보(1개씩만 들어감) 

		
		//미완료 예약들 한개씩 시작
		for(index=0; index<rsrvYetList.size(); index++) {
			
			rsrvYetList.get(index).put("loopCount", 1);
			
			lScenarioList.add(rsrvYetList.get(index));//예약 객체 1개씩만 넣기 위해
			
			//update할때는 scenario(map)매개변수, execute할때는 scenarioList(map<arraylist>) 매개변수
			mScenario.put("scenarioList", lScenarioList);//scenarioList={...} 꼴로 변경
			mScenario.put("sessionId", rsrvYetList.get(index).get("sessionId"));
			tempScenarioList.put("scenarioList", lScenarioList);
			tempScenarioList.put("sessionId", rsrvYetList.get(index).get("sessionId"));
			
			
			reserveTime = (String) rsrvYetList.get(index).get("reserveTime"); //미완료 예약시간
			
			
//				ArrayList<Map<String,Object>> overlapCheck = (ArrayList<Map<String, Object>>) selectReservedScenarioList().get("reserveList");
//				System.out.println("overlap check : "+overlapCheck);
//				for(int k=0; k<overlapCheck.size()-1;k++){
//					if(reserveTime.equals(overlapCheck.get(k).get("reserveTime"))){
//						System.out.println("지금 예약한 시간에 이미 다른 예약이 있습니다. >> " +overlapCheck.get(k).get("reserveId")+" "+overlapCheck.get(k).get("reserveTime"));
//						isOverlapFlag = true;
//					}
//				}
			
			reserveId =  String.valueOf(rsrvYetList.get(index).get("reserveId"));//미완료 예약 아이디
			ArrayList<Integer> reserveIds = new ArrayList<>();
			reserveIds.add(Integer.parseInt(reserveId));
			
			//예약클릭 시  중복이라면 여기서 종료
			if (isOverlapFlag == true) {
				Map<String, Object> tempInput = new HashMap<String, Object>();
				tempInput.put("overlaps", true);
				
				rsrvYetList.get(index).put("reserveIds", reserveIds);
				service.deleteReserve(rsrvYetList.get(index));//중복 db 삭제 되는지 확인
				System.out.println("reserve overlap!"+rsrvYetList.get(index));
				return;
			}
			
			//받은 String 년월일시분 split
			Calendar date = Calendar.getInstance();
			date = timeSplit(reserveTime);
			long executeTime = executeTimeCalculator(mScenario, date);
			
			
			String rsrvIndicator = reserveId+","+date.getTimeInMillis();//현재 예약 id, time 기억
			/**************************************************************************/
			//예약날짜들 전역 예약날짜배열에 삽입
			rsrvTimeArr.add(date.getTimeInMillis());
			/**************************************************************************/
			if(isOverlapFlag==true) {
				System.out.println("중복 예약이므로 예약목록에 담지 않습니다.");
			}else {
				rsrvList.add(rsrvIndicator); //예약 배열에 추가
			}
			
			
			
			try {
				//timer 작업
				Timer timer = new Timer();
				//TimerTask timerTask = new TimerTask()
				TimerTask timerTask = new TimerTask() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						//예약시간과 실행시간이 같으면 실행완료예약이니 예약 배열에서 삭제
						for(int i=0; i<rsrvTimeArr.size();i++) {
							if(rsrvTimeArr.get(i)-100 < System.currentTimeMillis() && rsrvTimeArr.get(i)+100 > System.currentTimeMillis()) {
								rsrvTimeArr.remove(i);
							}
						}
						
						//삭제된 예약id와 예약시간 [{id,time}, {id,time}, {id,time}]
						ArrayList<Map<String,Object>> listTemp = (ArrayList<Map<String, Object>>) controller.selectDeletedReservedScenarioList().get("deletedReservedScenarioList");

						boolean executeFlag = true;//중복시 하나만 실행하기 위한 플래그
						
						ScenarioController.gExecuteFlag=true;//예약 실행될 때 예약실행플래그 on
						ActionController.exFlag = true;
					
						String myTime;
						String timerTemp = String.valueOf(System.currentTimeMillis());//timer시간(밀리초포함)
						String timerTime = timerTemp.substring(0, timerTemp.length()-3);//timer시간 (밀리초제거)
						
						//비교할 시간 문자열 설정
						for(int i=0; i<rsrvList.size();i++) {
							String temp[]=rsrvList.get(i).split(",");//id, time 분리
							//System.out.println(temp[0]+" "+temp[1]);
							myTime = temp[1].substring(0, temp[1].length()-3);//내 시간(밀리초제거)
									
							
							//[rsrvId -- rsrvTime]
							//내가 설정한 시간과 타이머의 시간이 같으면 내 rsrvId를 가져온 후 Map에 추가 - 이 작업을 하지 않으면 db의 마지막 행의 상태만 바뀌게 됨
							if(myTime.equals(timerTime)) {
								//id 값 가져왔음 -> 예약 실행되는 현재 인덱스(temp[0]) 파악함 , 검사로직, 추가됨
								for(int j=0; j<listTemp.size(); j++) {		
									//System.out.println("temp[0]형식 : "+temp[0].getClass()+", listTemp.get(j)형식 : "+listTemp.get(j).get("reserveId").getClass());
									//System.out.println("t/f판단"+temp[0].equals(listTemp.get(j).get("reserveId"))+",temp[0] : "+temp[0]+", listTemp.get(j): "+listTemp.get(j).get("reserveId"));
									
									//삭제된 예약들이 실행되지 않도록 구분
									if(temp[0].equals(String.valueOf(listTemp.get(j).get("reserveId")))) {
										//System.out.println("삭제된 예약과 중복됩니다. "+listTemp.get(j).get("reserveId"));
										executeFlag = false;
									}
								}
								
								if(executeFlag) {
									//원래 if(myTime.equals())안에 있던 함수들
									//((Map<String, Object>)input.get("scenario")).put("reserveId", temp[0]);//update할 Map에 rsrvId 갱신
									//srTemp.put("reserveId", temp[0]);//복사할 Map인 srTemp에도 rsrvId 갱신
									
									//맨처음에 넘어온 모든 미완료 값[{},{},{},{}]에서 하나씩만 골라낼 수 있을까 >> run() 시작될 때의 reserveId 값과 List값의 reserveId를 비교하여 같은 것을 보내자 >> 골라낸 리스트만 execute에 추가해서 실행하면 된다!
									ArrayList<Map<String, Object>> tt = new ArrayList<Map<String, Object>>();//reserveId를 가져오기 위한 List
									tt = (ArrayList<Map<String, Object>>) mScenario.get("scenarioList");
									int lIndex = 0;
									ArrayList<Map<String, Object>> ttExecute = new ArrayList<Map<String, Object>>();//실행될 1가지 시나리오만 담는 List
									
									//시나리오={시나리오리스트=[{},{},{},{}], sessionId=""} 형식을 맞추기 위해 생성
									Map<String, Object> scenario = new HashMap<String,Object>();
									Map<String, Object> tempScenario = new HashMap<String,Object>();
									
									//모든 예약 시나리오 중 현재 시간에 맞는 시나리오리스트를 찾아 단 한개의 시나리오만 실행되도록 한다. [{},{},{}]
									for(int k=0; k<lScenarioList.size(); k++) {
										//System.out.println("예약아이디....."+tt.get(k).get("reserveId")+" ,"+temp[0]);
										if(temp[0].equals(String.valueOf(tt.get(k).get("reserveId")))) {
											
											ttExecute.add(tt.get(k));
											tempScenarioList.put("scenarioList", ttExecute);
											
											//db작업하기 위한 맵
											tempScenario.put("scenarioList", ttExecute);
											scenario.put("scenario", tempScenario);
											
											//예약종료시간 계산 후 Map 저장 >> db에 넣기 위한 용도 
											((HashMap) scenario.get("scenario")).put("reserveEnd", executeTime);
											
											lIndex = k;//실행시키고 지워줄 예약의 인덱스
										}
									}
									/**************************status변경위해 여기 추가함********************************/
									//System.out.println("시나리오에 들어갈 아이디"+tt.get(lIndex).get("reserveId"));
									((HashMap) scenario.get("scenario")).put("reserveId", tt.get(lIndex).get("reserveId"));
									/**************************status변경위해 여기 추가함********************************/
									
									//System.out.println("뭐 어떤데? "+tempScenarioList);
									
									service.updateReserveScenario(scenario,"실행중");//상태갱신
									System.out.println("예약 시나리오 시작");
									service.executeScenario(tempScenarioList);
									service.updateReserveScenario(scenario, "실행완료");//상태갱신 
									//////////
									lScenarioList.remove(lIndex);
								}
								//실행완료 후 예약배열에서 삭제
								rsrvList.remove(i);
							}
						}
						//타이머 끝나면 실행 플래그 false
						ScenarioController.gExecuteFlag=false;
						ActionController.exFlag = false;
					}
					//input.put("sessionId", request.getSession().getId());
				};
				//timer.schedule(timerTask, new Date(date.getTimeInMillis()), 24*60*60*1000);
				timer.schedule(timerTask, new Date(date.getTimeInMillis()));
			}catch(Exception e) {
				System.out.println(e);
				throw(e);
				//ScenarioController.gExecuteFlag=false;
			}
			
			
			
		}

	}
		
		
		
	//예약 시간 겹치는지 체크	
	public Map<String, Object> checkScenario(Map<String, Object> input) {
		
		Map<String, Object> tempInput = new HashMap<String, Object>();//클라이언트에 플래그 값들 보낼 맵
		int wholeTime = 0; //현재 시간에서 더해질 시간(초단위)
		//더해질 시간 구하기
		if (input.containsKey("scenarioList") == true) {
			List<Map<String, Object>> scenarioList = (List<Map<String, Object>>) input.get("scenarioList");
			// 총 삽입해야될 개수 계산
			int totalCount = 0;
			
			for(Map<String, Object> scenario : scenarioList) {
				int loopCount = Integer.parseInt(scenario.get("loopCount").toString());
				int actTime=0;
				int actionCount = 0;
				List<Map<String, Object>> actionList = (List<Map<String,Object>>) scenario.get("actionList");
				
				for(Map<String, Object> action : actionList) {
					actionCount += Integer.parseInt(action.get("loopCount").toString()) * Integer.parseInt(action.get("userCount").toString()); 
					//
					//System.out.println("반복횟수..? "+Integer.parseInt(action.get("loopCount").toString()));
					//System.out.println("기준시간..? "+ Integer.parseInt(action.get("timeSet").toString()));
					//System.out.println("타임아웃..? "+ Integer.parseInt(action.get("timeout").toString().substring(0,action.get("timeout").toString().length()-3)));
					actTime += (Integer.parseInt(action.get("loopCount").toString()) * Integer.parseInt(action.get("timeSet").toString())) + Integer.parseInt(action.get("timeout").toString().substring(0,action.get("timeout").toString().length()-3));
				}
				totalCount += loopCount * actionCount;
				wholeTime += actTime;
				//System.out.println("루프 "+loopCount);// 시나리오 자체의 루프
				//System.out.println("액션 "+actionCount);
				//System.out.println("총시간 "+wholeTime);
	
			}
			//System.out.println("시나리오 추가될 시간! "+wholeTime);
		}
		else if(input.containsKey("actionList") == true) {
			//System.out.println("액션리스트!!!" +input.get("actionList"));
			List<Map<String, Object>> list = (List<Map<String, Object>>) input.get("actionList");
			
			// 총 삽입해야될 개수 계산
			int totalCount = 0;
			int actTime=0;
			for(Map<String, Object> action : list) {
				totalCount += Integer.parseInt(action.get("loopCount").toString()) * Integer.parseInt(action.get("userCount").toString());
				
				actTime += (Integer.parseInt(action.get("loopCount").toString()) * Integer.parseInt(action.get("timeSet").toString())) + Integer.parseInt(action.get("timeout").toString().substring(0,action.get("timeout").toString().length()-3));
				wholeTime += actTime;
			}
			
			//System.out.println("액션 추가될 시간! "+wholeTime);
		}
		
		//wholeTime*1000 = 더해질 밀리초
		
		//현재 실제 시간 가져오기 , 현재시간 + 더해질 시간
		SimpleDateFormat format1 = new SimpleDateFormat ("yyMMddHHmm");
		String sCurrentTime = format1.format(System.currentTimeMillis()+(wholeTime*1000));
		//System.out.println("현재 실행하려는 시간+실행시간 str) >>> "+sCurrentTime);
		int iCurrentTime = Integer.parseInt(sCurrentTime);//현재시간 + 수행시간
		//System.out.println("현재 실행하려는 시간+실행시간 int) >>> "+iCurrentTime);
		
		
		//예약시작시간들과 현재시간 비교하기
		for(int i=0; i<rsrvTimeArr.size(); i++) {
			//예약시작시간 <= 현재시작시간+총동작시간 이면 예약이 돌때 실행
			if(rsrvTimeArr.get(i) <= System.currentTimeMillis()+(wholeTime*1000)) {
				
				//System.out.println("예상 종료시간: "+(System.currentTimeMillis()+((long)wholeTime*1000))+ ", 예약 시작 시간: "+rsrvTimeArr);
				
				tempInput = new HashMap<String, Object>();
				tempInput.put("executeFlag", true);

				//System.out.println("실행중? : "+gExecuteFlag);

				return tempInput;
			}
		}
		
		//예약 실행중이니 안됨
		if (ScenarioController.gExecuteFlag == true) {
			tempInput = new HashMap<String, Object>();
			tempInput.put("executeFlag", true);

			return tempInput;
		}
		
		return tempInput;
	}
		
		
		
		
		
		
		
		
		
	//사용 함수들
	
	
	public Calendar timeSplit(String reserveTime) {
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
		
		return date;
	}
	
	public long executeTimeCalculator(Map<String, Object> inputScenario, Calendar date) {
		
		int wholeTime = 0;//예약의 총 실행시간

		List<Map<String, Object>> inputScenarioList = (List<Map<String, Object>>) inputScenario.get("scenarioList");
		// 총 삽입해야될 개수 계산
		int totalCount = 0;
		
		for(Map<String, Object> scenario : inputScenarioList) {
			int loopCount = Integer.parseInt(scenario.get("loopCount").toString());
			int actTime=0;
			int actionCount = 0;
			List<Map<String, Object>> actionList = (List<Map<String,Object>>) scenario.get("actionList");
			
			for(Map<String, Object> action : actionList) {
				actionCount += Integer.parseInt(action.get("loopCount").toString()) * Integer.parseInt(action.get("userCount").toString()); 
				//
				//System.out.println("반복횟수..? "+Integer.parseInt(action.get("loopCount").toString()));
				//System.out.println("기준시간..? "+ Integer.parseInt(action.get("timeSet").toString()));
				//System.out.println("타임아웃..? "+ Integer.parseInt(action.get("timeout").toString().substring(0,action.get("timeout").toString().length()-3)));
				actTime += (Integer.parseInt(action.get("loopCount").toString()) * Integer.parseInt(action.get("timeSet").toString())) + Integer.parseInt(action.get("timeout").toString().substring(0,action.get("timeout").toString().length()-3));
			}
			totalCount += loopCount * actionCount;
			wholeTime += actTime;
			//System.out.println("루프 "+loopCount);// 시나리오 자체의 루프
			//System.out.println("액션 "+actionCount);
			//System.out.println("총시간 "+wholeTime);

		}
		//System.out.println("시나리오 추가될 시간! "+wholeTime);
	
		
		//종료시간계산 (예약시작시간 + 실행시간)
		long reserveEnd = date.getTimeInMillis()+(long)wholeTime*1000;
		
		return reserveEnd;
	}
	
	public boolean reserveOverlap(ArrayList<Map<String,Object>> overlapCheckMap, boolean isOverlapFlag) {
		long rStartArr[] = new long[overlapCheckMap.size()];
		long rEndArr[] = new long[overlapCheckMap.size()];
		long iStartReserve = 0;
		long iEndReserve = 0;
		
		
		//모든 예약 시작,종료 시간들을 배열에 담는다.
		for(int i=0; i<overlapCheckMap.size(); i++) {
			iStartReserve = timeSplit((String) overlapCheckMap.get(i).get("reserveTime")).getTimeInMillis();//시작 밀리초
			iEndReserve = Long.parseLong((String) overlapCheckMap.get(i).get("reserveEnd")); 
		
			rStartArr[i] = iStartReserve;//처음부터 끝까지의 시작시간을 담는 배열
			rEndArr[i] = iEndReserve;//처음부터 끝까지의 종료시간을 담는 배열
		}
		

		//모든 예약 객체를 스캔
		for(int i=0; i<overlapCheckMap.size(); i++) {
			iStartReserve = timeSplit((String) overlapCheckMap.get(i).get("reserveTime")).getTimeInMillis();//시작 밀리초
			iEndReserve = Long.parseLong((String) overlapCheckMap.get(i).get("reserveEnd"));
			
			// [예약시작 ~ 예약종료]에 다른 예약시작시간, 예약종료시간이 하나라도 있다면 예약시간이 겹치므로 overlap을 true로 변경해줌 
			for(int j=0; j<overlapCheckMap.size(); j++) {
				if(rStartArr[j] >= iStartReserve && rStartArr[j] <= iEndReserve) {
					if(i!=j) {
						//System.out.println("start겹침!! "+ i+","+j);
						//System.out.println("id = "+overlapCheckMap.get(j).get("reserveId")+", "+overlapCheckMap.get(j).get("reserveTime"));
						isOverlapFlag = true;
					}
					
				}
				if(rEndArr[j] >= iStartReserve && rEndArr[j] <= iEndReserve) {
					if(i!=j) {
						//System.out.println("end겹침!! "+i+","+j);
						//System.out.println("id = "+overlapCheckMap.get(j).get("reserveId")+", "+overlapCheckMap.get(j).get("reserveTime"));
						isOverlapFlag = true;
					}
				}
			}
		}
		
		
		return isOverlapFlag;
	}
}
