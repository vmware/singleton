package com.vmware.vip.test.javaclient.mock;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Delay;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.Parameter;

import com.vmware.g11n.log.GLogger;
import com.vmware.vip.test.common.Config;
import com.vmware.vip.test.common.RequestType;
import com.vmware.vip.test.javaclient.Constants;

public class MockAgent {
	public static MockAgent mockAgent = null;
	public static final String RESPONSE_FLAG_PASS = "MOCK_TEST_PASS";
	public static final String RESPONSE_FLAG_FAIL = "MOCK_TEST_FAIL";
	private ClientAndServer mockServer;
	private MockServerClient mockServerClient;
	private static final int DEFAULT_PORT = 1080;
	private int port;

	MockAgent() {
		Config cfg = Config.getInstance();
		setPort(cfg.get(Constants.CONF_KEY_MOCK_PORT));
		mockServer = ClientAndServer.startClientAndServer(getPort());
		mockServerClient = new MockServerClient("localhost", DEFAULT_PORT);
	}

	public static synchronized MockAgent getInstance() {
		if (mockAgent == null) {
			mockAgent = new MockAgent();
		}
		return mockAgent;
	}

	public int getPort() {
		return this.port;
	}

	public void setPort(String port) {
		try {
			this.port = Integer.parseInt(port);
		} catch (NumberFormatException e) {
			GLogger.getInstance(this.getClass().getName()).info(
					String.format("mock port '%s' from properties file is not correct, "
							+ "using default port '%s'", port, DEFAULT_PORT));
			this.port = DEFAULT_PORT;
		}
	}

	public void stopMockServer() {
		mockServer.stop();
	}

	public void resetMock() {
		mockServerClient.reset();
	}

	public boolean isMockServerRuning() {
		return mockServerClient.isRunning();
	}

	public void addExpectation(String requestMethod, String requestPath,
			List<Parameter> requestParam, String requestBody, int responseCode,
			HashMap<String, String> responseHeaders, String expectedResponseBody) {
		Header[] headers = new Header[responseHeaders.size()];
		int i = 0;
		for ( String key : responseHeaders.keySet()) {
			headers[i] = new Header(key, responseHeaders.get(key));
			i++;
		}

		HttpRequest request = HttpRequest.request()
		.withMethod(requestMethod)
		.withPath(requestPath);
		if (requestParam!=null && !requestParam.isEmpty()) {
			request = request.withQueryStringParameters(requestParam);
		}
		if (!requestMethod.equalsIgnoreCase(RequestType.GET) && requestBody!=null && !requestBody.isEmpty()) {
			request = request.withBody(requestBody);
		}
		mockServerClient
			.when(request)
			.respond(
					HttpResponse.response()
					.withStatusCode(responseCode)
                    	.withHeaders(headers)
                    .withBody(expectedResponseBody)
                    .withDelay(new Delay(TimeUnit.SECONDS, 1))
			);
	}
}
