package decibel.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerWorker implements Runnable
{
	private Socket client;
	private QueryLogger queryLogger;

	private static final Logger log = Logger.getLogger( ServerWorker.class.getName() );
	
	public ServerWorker( Socket client, QueryLogger logger )
	{
		this.client = client;
		this.queryLogger = logger;
	}
	
	@Override
	public void run()
	{
		Thread thread = Thread.currentThread();
		log.log( Level.FINE, "Server worker running on thread: " + thread.getName() + " (" + thread.getId() + ")" );
		
		try
		{
			InetSocketAddress inet = (InetSocketAddress) client.getRemoteSocketAddress();
			String ipAddress = inet.getHostString() + ":" + inet.getPort();
			
			BufferedReader input = new BufferedReader( new InputStreamReader( client.getInputStream() ) );
			OutputStream output = client.getOutputStream();

			StringBuffer request = new StringBuffer();	 
			
			// Assume 200 response code as we got this far
			StringBuffer response = new StringBuffer();
			response.append( "HTTP/1.1 " + HttpURLConnection.HTTP_OK + " OK" );

			String inputLine = input.readLine();
			String query = inputLine;
			while( inputLine != null && inputLine.length() > 0 )
			{
				request.append( inputLine + "; " );
				inputLine = input.readLine();
			}

			queryLogger.log( ipAddress, query );

			response.append( "\n\n" );
			response.append( "<html><body><table>" );
			response.append( "<tr><td><b>Client</b></td><td>" + ipAddress + "</td></tr>" );
			response.append( "<tr><td><b>Thread</b></td><td>" + thread.getName() + " (" + thread.getId() + ")</td></tr>" );
			response.append( "<tr><td><b>Query</b></td><td>" + query + "</td></tr>" );
			response.append( "</table></html></body>" );

			output.write( response.toString().getBytes() );

			output.close();
			input.close();
			client.close();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}
}
