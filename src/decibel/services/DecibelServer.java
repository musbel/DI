package decibel.services;

import java.util.Scanner;

import decibel.core.ServerController;
import decibel.tests.TestRequests;

public class DecibelServer
{
	private static Scanner scan = new Scanner( System.in );
	private static ServerController server = null;

	private static int port = 9000;
	private static int numberOfServerThreads = 4;
	private static int numberOfClientThreads = 4;
	private static int numberOfClientRequests = 5000;
	
	private static String getUserOption()
	{
		System.out.println();
		System.out.println( "Choose from the following options:" );
		System.out.println( " [A] Start Server (port " + port + ")" );
		System.out.println( " [B] Stop Server" );
		System.out.println( " [C] Run Test" );
		System.out.println( " [Q] Quit" );
		System.out.println();
		
		String selectedOption = scan.nextLine().toUpperCase();
		return selectedOption;
	}

	private static void runTest() throws InterruptedException
	{
		// Create HTTP server which listens for GET requests on the port
		server.startServer( numberOfServerThreads );

		long startTime = System.currentTimeMillis();
		
		// Execute a number of GET requests on the port
		TestRequests.runTest( "http://" + server.getHostName(), numberOfClientRequests, numberOfClientThreads );
		
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println( "Test time: " + elapsedTime + "ms" );

		server.stopServer();
	}

	public static void main( String[] args )
	{
		if( args.length != 0 )
		{
			try
			{
				port = Integer.parseInt( args[0] );
			}
			catch( NumberFormatException e )
			{
				System.out.println( "Port number must be an integer value!" );
				System.exit( 0 );
			}
		}
		
		try
		{
			server = new ServerController( port );
			
			String selectedOption = getUserOption();
			while( !selectedOption.equals( "Q" ) )
			{
				if( selectedOption.equals( "A" ) )
				{
					server.startServer( port );
				}
				else if( selectedOption.equals( "B" ) )
				{
					server.stopServer();
				}
				else if( selectedOption.equals( "C" ) )
				{
					runTest();
				}
				
				selectedOption = getUserOption();
			}
	
			server.stopServer();
			scan.close();
		}
		catch( InterruptedException e )
		{
			e.printStackTrace();
		}
	}
}
