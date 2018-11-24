
/*
 * @author: Vaibhav Murkute
 * Project: Centralized File-Locking in Distributed Systems. (Requestor Process) 
 * date: 11/15/2018
 */

public class Process {

	public static void main(String[] args) {
		LockRequestor requestor = new LockRequestor();
		requestor.init();


		try {
			if(LockRequestor.connectionThread != null){
				LockRequestor.connectionThread.join();
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Process "+LockRequestor.process_id+" ended!");

	}

}
