package decibel.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import decibel.core.ServerController;

public class ServerTests
{
	@Before
	public void setUp() throws Exception
	{
	}

	@Test
	public void testRunningState() throws InterruptedException
	{
		ServerController server = new ServerController( 9000 );
		server.startServer();
		assertTrue( server.isRunning() );
		
		server.stopServer();
		assertFalse( server.isRunning() );
	}

	@Test
	public void testServerConnection() throws InterruptedException, IOException
	{
		ServerController server = new ServerController( 9001 );
		server.startServer();

		URL urlObject = new URL( "http://" + server.getHostName() );
		HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
		connection.connect();
		
		assertEquals( HttpURLConnection.HTTP_OK, connection.getResponseCode() );

		server.stopServer();
	}
}
