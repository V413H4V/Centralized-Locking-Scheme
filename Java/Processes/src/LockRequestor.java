import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;

/*
 * @author: Vaibhav Murkute
 * Project: Centralized File-Locking in Distributed Systems. (Requestor Process) 
 * date: 11/15/2018
 */

public class LockRequestor {
	public static final String process_id = "P1";
	public static final int pid = 1;
	public static String required_file = "shared.txt";
	private static InetAddress SERVER_HOSTADD = InetAddress.getLoopbackAddress();
	public static final int[] PROCESS_PORTLIST = new int[]{4444,5555,6666,7777};
	public static final int CONTROLLER_PID = 0;
	private static final int NUM_PROCESSES = PROCESS_PORTLIST.length;
	private static ServerSocket server_socket;
	public static Thread connectionThread = null;
	public static boolean requestServed = false;
		
	public void init(){
		System.out.println("Process: "+LockRequestor.process_id);
		// Thread 01: manageConnections
		connectionThread = (new Thread(){
			@Override
			public void run(){
				manageConnections();
				return;
			}
		});

		connectionThread.start();
		
		// Request file lock
		System.out.println("File lock requested.");
		sendRequest();

	}

	public void manageConnections(){
		int server_port = PROCESS_PORTLIST[pid];
		try {
			server_socket = new ServerSocket(server_port, 0, SERVER_HOSTADD);
			while(!requestServed){
				Socket clientSocket = server_socket.accept();
				ObjectInputStream obj_ip = new ObjectInputStream(clientSocket.getInputStream());
				ProcessEvent event = (ProcessEvent)obj_ip.readObject();
				clientSocket.close();
				(new Thread(){
					@Override
					public void run(){
						manageRequests(event);
						return;
					}
				}).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public synchronized void manageRequests(ProcessEvent event){
		try {
			if(event.getPid() == CONTROLLER_PID){
				if(!requestServed && event.isApproved() && event.getFile().equalsIgnoreCase(required_file)){
					System.out.println("Lock acquired for: " + required_file);
					updateFile();
					requestServed = true;
					sendRequest();
					System.out.println("Lock released.");
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendRequest(){
		try {
			ProcessEvent myEvent = new ProcessEvent();
			myEvent.setPid(pid);
			myEvent.setProcess_id(process_id);
			myEvent.setFile(required_file);
			
			if(!requestServed){
				myEvent.setLock_release(false);
				myEvent.setApproved(false);
			}else{
				myEvent.setLock_release(true);
				myEvent.setApproved(true);
			}
			int port = PROCESS_PORTLIST[CONTROLLER_PID];
			Socket socket = new Socket(SERVER_HOSTADD,port);
			ObjectOutputStream obj_op = new ObjectOutputStream(socket.getOutputStream());
			obj_op.writeObject(myEvent);
			
			obj_op.close();
			socket.close();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void updateFile(){
		File file = new File(required_file);
		try {
			if(file.exists()){
				byte[] file_bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
				String file_content = new String(file_bytes, "UTF-8");
				if(file_content.length() > 0){
					int counter = Integer.parseInt(file_content);
					counter += 1;
					Files.write(Paths.get(file.getAbsolutePath()), String.valueOf(counter).getBytes());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}catch (NumberFormatException e) {
			System.out.println("Counter value in the file: "+required_file+" is not an integer....Cannot increment it.");
			e.printStackTrace();
		}
	}
}
