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
import java.util.ArrayList;

/*
 * @author: Vaibhav Murkute
 * Project: Centralized File-Locking in Distributed Systems. (Coordinator) 
 * date: 11/15/2018
 */

public class LockingController {
	public static final String process_id = "P0";
	public static final int pid = 0;
	public static final String shared_file = "shared.txt";
	private static InetAddress SERVER_HOSTADD = InetAddress.getLoopbackAddress();
	public static int[] PROCESS_PORTLIST = new int[]{4444,5555,6666,7777};
	private static final int NUM_PROCESSES = PROCESS_PORTLIST.length;
	public static volatile ArrayList<ProcessEvent> event_buffer = new ArrayList<>();
	public static int lockHolder = 0;
	public static boolean locked = false;
	private static int process_served = 0;
	private static ServerSocket server_socket;
	public static Thread connectionThread = null;
	public static Thread orderThread = null;
	
	public LockingController() {
		LockingController.lockHolder = 0;
		LockingController.locked = false;
	}
	
	
	public void init(){
		System.out.println("Process: Locking Coordinator (P0)");
		
		try {
			File file = new File(shared_file);
			Files.write(Paths.get(file.getAbsolutePath()), "0".getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Thread 01: manageConnections
		connectionThread = (new Thread(){
			@Override
			public void run(){
				manageConnections();
				return;
			}
		});

		connectionThread.start();
		
		orderThread = (new Thread(){
			@Override
			public void run(){
				enforceLockingOrder();
				return;
			}
		});
		
		orderThread.start();

	}

	public void manageConnections(){
		int server_port = PROCESS_PORTLIST[pid];
		try {
			server_socket = new ServerSocket(server_port, 0, SERVER_HOSTADD);
			while(true){
				Socket clientSocket = server_socket.accept();
				ObjectInputStream obj_ip = new ObjectInputStream(clientSocket.getInputStream());
				ProcessEvent event = (ProcessEvent)obj_ip.readObject();
				clientSocket.close();
				(new Thread(){
					@Override
					public void run(){
						manageLockRequests(event);
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

	public synchronized void manageLockRequests(ProcessEvent event){
		try {	
			if(!event.isLock_release()){
				event_buffer.add(event);
			}else{
				LockingController.locked = false;
				LockingController.lockHolder = pid;
				System.out.println("File Lock Released");
				LockingController.process_served += 1;
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void enforceLockingOrder(){
		while(true){
			if(!LockingController.locked){
				if(!event_buffer.isEmpty()){
					ProcessEvent firstRequest = event_buffer.get(0);
					LockingController.locked = true;
					LockingController.lockHolder = firstRequest.getPid();
					System.out.println("File Lock Acquired by: "+firstRequest.getProcess_id());
					event_buffer.remove(0);
					sendApproval();
				}
			}
		}
	}

	public void sendApproval(){
		try {
			ProcessEvent myEvent = new ProcessEvent();
			myEvent.setPid(pid);
			myEvent.setProcess_id(process_id);
			myEvent.setApproved(true);
			myEvent.setFile(shared_file);
			myEvent.setLock_release(false);

			int port = PROCESS_PORTLIST[lockHolder];
			Socket socket = new Socket(SERVER_HOSTADD,port);;
			ObjectOutputStream obj_op = new ObjectOutputStream(socket.getOutputStream());;
			obj_op.writeObject(myEvent);

			obj_op.close();
			socket.close();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
