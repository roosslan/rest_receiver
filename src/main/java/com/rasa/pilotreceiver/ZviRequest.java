package com.rasa.pilotreceiver;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Тело запроса от серверного расширения Pilot (см. activity.cs). */
public record ZviRequest(
		@JsonProperty("izm_code") String izmCode,
		@JsonProperty("project") String project,
		@JsonProperty("pilotLogin") String pilotLogin) {
}
