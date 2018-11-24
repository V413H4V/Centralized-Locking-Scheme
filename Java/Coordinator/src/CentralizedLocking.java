
/*
 * @author: Vaibhav Murkute
 * Project: Centralized File-Locking in Distributed Systems. (Coordinator) 
 * date: 11/15/2018
 */

public class CentralizedLocking {

	public static void main(String[] args) {
		LockingController controller = new LockingController();
		controller.init();


		try {
			if(LockingController.connectionThread != null){
				LockingController.connectionThread.join();
			}
			if(LockingController.orderThread != null){
				LockingController.orderThread.join();
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Process "+LockingController.process_id+" ended!");

	}

}
