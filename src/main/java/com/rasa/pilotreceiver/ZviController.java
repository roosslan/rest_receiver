package com.rasa.pilotreceiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ZviController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZviController.class);

	private final ZviRepository repository;

	public ZviController(ZviRepository repository) {
		this.repository = repository;
	}

	/** 'C' из CRUD: ЗВИ согласовано, создаётся активная запись. */
	@PostMapping("/CREA")
	public ResponseEntity<String> addRecord(@RequestBody ZviRequest request) {
		if (isBlank(request.izmCode())) {
			return ResponseEntity.badRequest().body("izm_code не задан");
		}
		repository.insertActive(request.izmCode(), request.project(), request.pilotLogin());
		LOGGER.info("CREA: {} (проект {}, исполнитель {})", request.izmCode(), request.project(), request.pilotLogin());
		return ResponseEntity.ok().build();
	}

	/** CHAnge Request: ЗВИ отклонено, выполнено или удалено, запись деактивируется. */
	@PostMapping("/CHAR")
	public ResponseEntity<String> changeRequest(@RequestBody ZviRequest request) {
		if (isBlank(request.izmCode())) {
			return ResponseEntity.badRequest().body("izm_code не задан");
		}
		int rows = repository.deactivate(request.izmCode());
		if (rows == 0) {
			LOGGER.warn("CHAR: запись {} не найдена", request.izmCode());
			return ResponseEntity.notFound().build();
		}
		LOGGER.info("CHAR: {} деактивировано", request.izmCode());
		return ResponseEntity.ok().build();
	}

	private static boolean isBlank(String s) {
		return s == null || s.isBlank();
	}
}
