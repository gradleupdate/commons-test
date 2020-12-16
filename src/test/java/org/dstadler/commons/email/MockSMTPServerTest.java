package org.dstadler.commons.email;

import static org.junit.Assert.*;

import java.util.Arrays;

import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.HtmlEmail;
import org.dstadler.commons.testing.MemoryLeakVerifier;
import org.junit.After;
import org.junit.Test;

public class MockSMTPServerTest {
	private static final MemoryLeakVerifier verifier = new MemoryLeakVerifier();

	@After
	public void tearDown() {
		verifier.assertGarbageCollected();
	}

	@Test
	public void testStart() throws Exception {
		try (MockSMTPServer server = new MockSMTPServer()) {
			verifier.addObject(server);

			assertFalse(server.isRunning());
			server.start();
			assertTrue(server.isRunning());
			int port = server.getPort();
			assertTrue(port > 0);

			assertEquals(0, server.getMessageCount());
			assertNotNull(server.getMessages());

			server.stop();
			assertFalse(server.isRunning());
		}
	}

	@Test
	public void testSendEmail() throws Exception {
		try (MockSMTPServer server = new MockSMTPServer()) {
			verifier.addObject(server);

			assertFalse(server.isRunning());
			server.start();
			assertTrue(server.isRunning());
			int port = server.getPort();
			assertTrue(port > 0);

			HtmlEmail email = new HtmlEmail();
			email.setHostName("localhost");
			email.setSmtpPort(port);

			email.setTextMsg("somemessage");
			email.setSubject("somesubj");
			email.setTo(Arrays.asList(InternetAddress.parse("somebody@example.com")));
			email.setFrom("from@example.com");

			email.send();

			assertEquals(1, server.getMessageCount());
			assertNotNull(server.getMessages());
			assertNotNull(server.getMessages().next());
		}
	}
}
