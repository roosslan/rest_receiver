package com.rasa.pilotreceiver;


import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// This annotation instructs Spring to initialize its configuration - which is needed to start a new application
@SpringBootApplication // (exclude = org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration.class)
// Indicates that this class contains RESTful methods to handle incoming HTTP requests
@RestController
public class CPilotReceiver {
	static Logger LOGGER;
	static ConnectMSSQLServer connServer = new ConnectMSSQLServer();

	public static void main(String[] args) throws Throwable
    {
		LOGGER = LogManager.getLogger(CPilotReceiver.class.getName());

		Properties prop = new Properties();
		String file_name = "src/main/resources/app.config";
		try (FileInputStream fis = new FileInputStream(file_name)) {
			prop.load(fis);
		} catch (FileNotFoundException ex) {
			// FileNotFoundException catch is optional and can be collapsed
		} catch (IOException ex) {
    		// ...
		}
		System.out.println(prop.getProperty("app.name"));
		System.out.println(prop.getProperty("app.version"));

		String user_name = "some_user";
		String password = "some_passwd";

		// integratedSecurity=false;
		connServer.dbConnect("jdbc:sqlserver://sql_server_hostname;databaseName=pilot;encrypt=true;trustServerCertificate=true;", user_name, password);

		Tomcat tomcat = new Tomcat();
		// Add an empty context
		Context ctx = tomcat.addContext("", null);

		/* Get an instance of the servlet and add a servlet mapping
		 * Tomcat.addServlet(ctx, "PilotConnector", new PilotConnector());
		 * ctx.addServletMappingDecoded("/CHAR", "PilotConnector");
		 */
		LOGGER.info("Pilot receiver server ready");
		// Start the tomcat instance
		tomcat.start();
		// Wait for a control-C to stop the process to allow for testing
		// tomcat.getServer().await();
		SpringApplication.run(CPilotReceiver.class, args);
	}

	@PostMapping("/CREA")
	// We can pass the name of the url param we want as an argument to the RequestParam annotation.
	public void addRecord(@RequestBody String body, HttpServletRequest request) {
		String requesterIP = request.getRemoteAddr();
		// if (requesterIP != "10.10.14.145"){}
		JSONObject jsonObj = new JSONObject(body);
		String izm_code = jsonObj.getString("izm_code");
		String project = jsonObj.getString("project");
		String pilotLogin = jsonObj.getString("pilotLogin");
		connServer.insertRow(izm_code, project, pilotLogin);
	}

	@PostMapping("/CHAR")
	public void changeRequest(@RequestBody String body, HttpServletRequest request) {
		String requesterIP = request.getRemoteAddr();
		// if (requesterIP != "10.10.14.145"){}
		JSONObject jsonObj = new JSONObject(body);
		String izm_code = jsonObj.getString("izm_code");
		connServer.updateRow(izm_code);
	}

	@GetMapping("/error")
	public String sampleError(HttpServletResponse response) {
		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		return "error";
	}
}
