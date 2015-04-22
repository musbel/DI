package decibel.core;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.io.IOException;

public class HTTPServer implements Runnable
{
	private int port;
	
	private ServerSocket serverSocket = null;
	private boolean socketOpen = false;
	
	private QueryLogger logger = null;
	
	private ExecutorService threadPool = null;
	protected Thread thread = null;

	public HTTPServer( int port, int threadPoolSize )
	{
		this.port = port;
		configureLogger();
		threadPool = Executors.newFixedThreadPool( threadPoolSize );
	}
	
	private void configureLogger()
	{
		long time = System.currentTimeMillis();
		String logFilename = "/tmp/Log_" + time + ".log";
		logger = QueryLogger.getLogger( logFilename );
	}
	
	@Override
	public void run()
	{
		thread = Thread.currentThread();

		// Open server socket
		openServerSocket();
		
		while( socketOpen )
		{
			// Accept client socket
			Socket client = null;
			
			try
			{
				client = serverSocket.accept();
				threadPool.execute( new ServerWorker( client, logger ) );
			}
			catch( IOException e )
			{
				if( !socketOpen )
				{
					return;
				}
				
				throw new RuntimeException( "Could not accept a client connection on port " + port, e );
			}
		}
	}
	
	private void openServerSocket()
	{
		try
		{
			serverSocket = new ServerSocket( port );
			socketOpen = true;
			System.out.println( "Server is listening on port " + port );
		}
		catch( IOException e )
		{
			throw new RuntimeException( "Failed to open server socket using port: " + port, e );
		}
	}
	
	public void stop()
	{
		try
		{
			serverSocket.close();
			socketOpen = false;
			logger.close();
			
			threadPool.shutdown();

			try
			{
				threadPool.awaitTermination( Long.MAX_VALUE, TimeUnit.SECONDS );
			}
			catch( InterruptedException e )
			{
				e.printStackTrace();
			}
		}
		catch( IOException e )
		{
			throw new RuntimeException( "Unable to close server socket using port: " + port, e );
		}
	}
	
	public boolean isRunning()
	{
		return socketOpen;
	}
	
	public int getPort()
	{
		return port;
	}

	public String getHostName()
	{
		InetSocketAddress socketAddress = new InetSocketAddress( serverSocket.getInetAddress(), port );
		return socketAddress.getHostString() + ":" + socketAddress.getPort();
	}
}
