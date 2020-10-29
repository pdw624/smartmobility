package kr.tracom.smps.base.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.tracom.smps.base.service.BaseService;

@Controller
public class BaseController implements ErrorController {
	@Autowired
	private BaseService service;
	
	@GetMapping("/")
	public String home() {
		return "index";
	}
	
	@GetMapping("/error")
	public String redirectRoot() {
		return "index.html";
	}
	
	@Override
	public String getErrorPath() {
		return "/error";
	}
	
	@GetMapping("/api/v1/token")
	@ResponseBody
	public String token() {
		Map<String, Object> input = new HashMap<String, Object>();
		input.put("code", "AUTH_TOKEN");
		return service.selectCodeValue(input);
	}
	
	@PutMapping("/api/v1/token")
	@ResponseBody
	public boolean updateToken(@RequestBody Map<String, Object> input) {
		input.put("code", "AUTH_TOKEN");
		return service.updateCodeValue(input);
	}
}
