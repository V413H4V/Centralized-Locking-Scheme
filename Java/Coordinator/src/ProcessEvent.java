import java.io.Serializable;
import java.util.Comparator;

/*
 * @author: Vaibhav Murkute
 * Project: Centralized File-Locking in Distributed Systems. (Coordinator) 
 * date: 11/15/2018
 */

public class ProcessEvent implements Serializable{
	private static final long serialVersionUID = 1001626620L;
	private String process_id;
	private int pid;
	private String file;
	private boolean lock_release = false;
	private boolean approved = false;
//	private String event_id;
//	private boolean ack = false;
	
	public String getProcess_id() {
		return process_id;
	}
	
	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public void setProcess_id(String process_id) {
		this.process_id = process_id;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public boolean isLock_release() {
		return lock_release;
	}

	public void setLock_release(boolean lock_release) {
		this.lock_release = lock_release;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}
	
	
}
