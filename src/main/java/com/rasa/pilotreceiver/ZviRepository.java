package com.rasa.pilotreceiver;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** Доступ к таблице pilot_zvi. Все запросы параметризованы. */
@Repository
public class ZviRepository {
	private final JdbcTemplate jdbc;

	public ZviRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public void insertActive(String zviCode, String project, String userLogin) {
		jdbc.update("insert into pilot_zvi (zvi, project, username, is_active) values (?, ?, ?, 1)",
				zviCode, project, userLogin);
	}

	/** Возвращает число деактивированных записей. */
	public int deactivate(String zviCode) {
		return jdbc.update("update pilot_zvi set is_active = 0 where zvi = ?", zviCode);
	}
}
