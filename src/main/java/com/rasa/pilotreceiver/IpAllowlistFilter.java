package com.rasa.pilotreceiver;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Пропускает запросы только с адресов из receiver.allowed-ips (через запятую).
 * Пустой список отключает проверку.
 */
@Component
public class IpAllowlistFilter extends OncePerRequestFilter {
	private static final Logger LOGGER = LoggerFactory.getLogger(IpAllowlistFilter.class);

	private final Set<String> allowedIps;

	public IpAllowlistFilter(@Value("${receiver.allowed-ips:}") String allowedIps) {
		this.allowedIps = Arrays.stream(allowedIps.split(","))
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.collect(Collectors.toUnmodifiableSet());
		if (this.allowedIps.isEmpty()) {
			LOGGER.warn("receiver.allowed-ips не задан: запросы принимаются с любых адресов");
		}
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String remote = request.getRemoteAddr();
		if (!allowedIps.isEmpty() && !allowedIps.contains(remote)) {
			LOGGER.warn("Отклонён запрос {} {} с адреса {}", request.getMethod(), request.getRequestURI(), remote);
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		chain.doFilter(request, response);
	}
}
