package decibel.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestClient implements Runnable
{	
	private String url;
	private static final Logger log = Logger.getLogger( TestClient.class.getName() );

	public TestClient( String url, int testNumber )
	{
		this.url = url + "?id=" + testNumber;
	}

	@Override
	public void run()
	{
		Thread thread = Thread.currentThread();
		log.log( Level.FINE, "Client running on thread: " + thread.getName() + " (" + thread.getId() + ")" );

		url = url + "&threadId=" + thread.getId();
		
		try
		{
			log.log( Level.FINE, "Thread [" + thread.getId() + "]: Sending GET request to URL: " + url );

			URL urlObject = new URL( url );
			HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
			
			connection.setRequestMethod( "GET" );
			
			BufferedReader input = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while( ( inputLine = input.readLine() ) != null )
			{
				response.append( inputLine );
			}
			
			log.log( Level.FINE, "Thread [" + thread.getId() + "]: Response = " + response.toString() );

			input.close();
		}
		catch( MalformedURLException e )
		{
			e.printStackTrace();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}
}
