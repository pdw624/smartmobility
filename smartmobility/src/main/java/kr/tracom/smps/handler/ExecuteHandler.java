package kr.tracom.smps.handler;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.asynchttpclient.AsyncHandler;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.HttpResponseBodyPart;
import org.asynchttpclient.HttpResponseStatus;
import org.asynchttpclient.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.handler.codec.http.HttpHeaders;
import kr.tracom.smps.base.mapper.BaseMapper;
import kr.tracom.smps.common.Util;
import kr.tracom.smps.scenario.controller.ScenarioController;
import kr.tracom.smps.scenario.service.ScenarioService;
import kr.tracom.smps.statistics.mapper.StatisticsMapper;

@Service
public class ExecuteHandler {
	//park
	@Autowired
	private ScenarioService service;
	//
	@Autowired
	private BaseMapper baseMapper;
	
	@Autowired
	private StatisticsMapper statisticsMapper;
	
	public static Map<String, DataHandler> sessionHandlerMap = new HashMap<>();
	
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
	private static final int TPS_TIME = 1000;
	
	@Transactional
	public void execute(Map<String, Object> input) {
		String workType = input.get("workType").toString();
		
		// api 요청시 필요한 header token 조회
		input.put("code", "AUTH_TOKEN");
		String token = baseMapper.selectCodeValue(input);
		
		// 통계조회 시퀀스 조회
		Map<String, Object> history = new HashMap<>();
		history.put("workType", workType);
		
		if(workType.equals("AT")) {
			List<Map<String, Object>> list = (List<Map<String, Object>>) input.get("actionList");
			
			// 총 삽입해야될 개수 계산
			int totalCount = 0;
			for(Map<String, Object> action : list) {
				totalCount += Integer.parseInt(action.get("loopCount").toString()) * Integer.parseInt(action.get("userCount").toString());
			}
			
			DataHandler handler = new DataHandler(totalCount, workType);
			handler.sendTotalCount(totalCount);
			
			String sessionId = input.get("sessionId").toString();
			sessionHandlerMap.put(sessionId, handler);
			
			long start = System.currentTimeMillis();
			for(Map<String, Object> action : list) {
				String workSeq = statisticsMapper.selectLastWorkSeq(input);
				history.put("workSeq", workSeq);
				history.put("workId", action.get("actionId"));
				action.put("token", token);
				action.put("workSeq", workSeq);
				
				statisticsMapper.insertHistory(history);
				process(action, handler);
			}
			long end = System.currentTimeMillis();
			System.out.println("동작 총 실행시간(ms): " + (end - start) / 1000.0);
		} else if(workType.equals("SR")) {
			List<Map<String, Object>> scenarioList = (List<Map<String, Object>>) input.get("scenarioList");
			
			// 총 삽입해야될 개수 계산
			int totalCount = 0;
			for(Map<String, Object> scenario : scenarioList) {
				int loopCount = Integer.parseInt(scenario.get("loopCount").toString());
				int actionCount = 0;
				List<Map<String, Object>> actionList = (List<Map<String,Object>>) scenario.get("actionList");
				
				for(Map<String, Object> action : actionList) {
					actionCount += Integer.parseInt(action.get("loopCount").toString()) * Integer.parseInt(action.get("userCount").toString()); 
				}
				
				totalCount += loopCount * actionCount;
			}
			
			DataHandler handler = new DataHandler(totalCount, workType);
			handler.sendTotalCount(totalCount);
			sessionHandlerMap.put(input.get("sessionId").toString(), handler);
			
			long start = System.currentTimeMillis();
			for(Map<String, Object> scenario : scenarioList) {
				// 시나리오를 2개 이상 실행시 시나리오 별로 workSeq 분리
				String workSeq = statisticsMapper.selectLastWorkSeq(input);
				List<Map<String, Object>> actionList = (List<Map<String,Object>>) scenario.get("actionList");
				int loopCount = Integer.parseInt(scenario.get("loopCount").toString());
			
				history.put("workSeq", workSeq);
				history.put("workId", scenario.get("scenarioId"));
				statisticsMapper.insertHistory(history);
				
				for(int i = 1; i < loopCount + 1; i++) {
					for(Map<String, Object> action : actionList) {
						// 수행명에 시나리오 루프 카운트 인덱스 번호 추가
						Map<String, Object> request = new HashMap<>(action);
						
						String actionName = request.get("actionName").toString();
						request.put("actionName", actionName + "-" + i);
						request.put("token", token);
						request.put("workSeq", workSeq);
						process(request, handler);
					}
				}
			}
			long end = System.currentTimeMillis();
			System.out.println("시나리오 총 실행시간(ms): " + (end - start) / 1000.0);
			
			//park
			service.updateReserveScenario(input, 2, ScenarioController.srTemp);
			
		}
	}
	
	public void process(Map<String, Object> request, DataHandler handler) {
		String actionName = request.get("actionName").toString();
		String token = request.get("token").toString();
		int loopCount = Integer.parseInt(request.get("loopCount").toString());
		int timeSet = Integer.parseInt(request.get("timeSet").toString());//park
		int userCount = Integer.parseInt(request.get("userCount").toString());
		String type = request.get("type").toString().toUpperCase();
		String url = request.get("url").toString();
		String encoding = request.get("encoding").toString();
		int timeout = Integer.parseInt(request.get("timeout").toString());
		String params = request.get("parameter") == null ? null : request.get("parameter").toString();
		
		// 요청 간격 - park
		//int speed = (int)((TPS_TIME / userCount) * 0.9);
		int speed = (int)((timeSet*1000 / userCount) * 0.9);
		
		// http 요청 시작
		DefaultAsyncHttpClientConfig.Builder clientBuilder = Dsl.config()
				.setConnectTimeout(timeout)
				.setReadTimeout(timeout)
				.setRequestTimeout(timeout)
				.setUserAgent("Mozilla/5.0")
				.setFollowRedirect(true);
		BoundRequestBuilder builder;
		
		try {
			builder = Dsl.asyncHttpClient(clientBuilder)
					.prepare(type, url)
					.addHeader("apiKey", token)
					.addHeader("Content-Type", MediaType.APPLICATION_JSON)
					.setCharset(Charset.forName(encoding));
			
			if(type.equals("GET")) {
				List<Param> paramsMapList = params != null ? Util.splitQueryToList(params) : null;
				builder.setQueryParams(paramsMapList);
			}
			else if(type.equals("POST")) {
				Map<String, String> paramsMap = params != null ? Util.splitQueryToMap(params) : null;
				builder.setBody(new ObjectMapper().writeValueAsString(paramsMap));
			}
					
			for(int i = 1; i < loopCount + 1; i++) {
				final int loopIndex = i;
				
				long repeatStart = System.currentTimeMillis();
				for(int j = 1; j < userCount + 1; j++) {
					if(handler.getFlag())
						return;
					
					final int executeIndex = j;
					
					builder.execute(new AsyncHandler<Integer>() {
						long start = System.currentTimeMillis();
						private Integer status;
						private String workSeq = request.get("workSeq").toString();
						private String rstType = "SUCCESS";
						private String rstName = actionName + "-" + loopIndex + "-" + String.format("%0" + (userCount + "").length() + "d", executeIndex);
						private String startDatetime = LocalDateTime.now().format(formatter);
						private StringBuilder rstContent = new StringBuilder();
						
						@Override
						public State onStatusReceived(HttpResponseStatus responseStatus) throws Exception {
							return State.CONTINUE;
						}

						@Override
						public State onHeadersReceived(HttpHeaders headers) throws Exception {
							return State.CONTINUE;
						}

						@Override
						public State onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
							rstContent.append(new String(bodyPart.getBodyPartBytes()));
							//System.out.println("response body: " + rstContent);
							return State.CONTINUE;
						}
						
						@Override
						public void onThrowable(Throwable t) {
							String endDatetime = LocalDateTime.now().format(formatter);
							long end = System.currentTimeMillis();
							long resTime = end - start;
							rstType = "FAIL";
							
							System.out.println("에러발생: " + t.getMessage());
							
							Map<String, Object> result = new HashMap<>();
							result.put("workSeq", workSeq);
							result.put("rstName", rstName);
							
							result.put("rstType", rstType);
							result.put("startDatetime", startDatetime);
							result.put("endDatetime", endDatetime);
							result.put("resTime", resTime);
							result.put("rstContent", t.getMessage());
							
							handler.putData(result);
						}

						@Override
						public Integer onCompleted() throws Exception {
							String endDatetime = LocalDateTime.now().format(formatter);
							long end = System.currentTimeMillis();
							long resTime = end - start;
							
							if(resTime > timeout)
								rstType = "FAIL";
							
							Map<String, Object> result = new HashMap<>();
							result.put("workSeq", workSeq);
							result.put("rstName", rstName);
							result.put("rstType", rstType);
							result.put("startDatetime", startDatetime);
							result.put("endDatetime", endDatetime);
							result.put("resTime", resTime);
							result.put("rstContent", rstContent.toString());
							
							handler.putData(result);
							
							return status;
						}
						
					});
					Util.delay(speed);
				}
				
				long repeatEnd = System.currentTimeMillis();
				double loopTime = repeatEnd - repeatStart;
				
				System.out.println("하나의 루프 실행 시간(ms): " + loopTime / 1000.0);
				
				if(loopTime < TPS_TIME) {
					Util.delay(TPS_TIME - loopTime);
				}
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		//*/
	}
}
