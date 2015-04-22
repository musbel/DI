package decibel.core;

public class ServerController
{
	private HTTPServer server = null;
	private Thread serverThread = null;
	private int port;
	
	public ServerController( int port )
	{
		this.port = port;
	}
	
	public void startServer( int threadPoolSize ) throws InterruptedException
	{
		if( server != null && server.isRunning() )
		{
			return;
		}
		
		server = new HTTPServer( port, threadPoolSize );
		serverThread = new Thread( server );
		serverThread.start();

		while( !server.isRunning() )
		{
			Thread.sleep( 10 );
		}
		
		System.out.println( "Server started (" + port + ")" );
	}
	
	public void startServer() throws InterruptedException
	{
		int threadPoolSize = Runtime.getRuntime().availableProcessors();
		startServer( threadPoolSize );
	}

	public void stopServer() throws InterruptedException
	{
		if( server != null && server.isRunning() )
		{
			server.stop();

			while( server.isRunning() )
			{
				Thread.sleep( 10 );
			}

			System.out.println( "Server stopped (" + port + ")" );
		}
	}
	
	public boolean isRunning()
	{
		return server.isRunning();
	}
	
	public String getHostName()
	{
		return server.getHostName();
	}

	public HTTPServer getHttpServer()
	{
		return server;
	}
}
