package server.state;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.simple.JSONObject;

public class MessageSendThread extends Thread {
	private boolean run = true;
	private DataOutputStream out;
	private Socket socket;
	private BlockingQueue<JSONObject> messageQueue;

	public MessageSendThread(Socket socket) throws IOException {
		this.socket=socket;
		this.out = new DataOutputStream(socket.getOutputStream());
		this.messageQueue= new LinkedBlockingQueue<JSONObject>();
	}

	public void run() {
		try {
			while(run) {
				if(messageQueue!=null&&!messageQueue.isEmpty()){
					JSONObject msg = messageQueue.take();
					this.MessageSend(msg);
				}
			}
			this.out.close();
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			try {
				this.out.close();
				this.socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally {
			try {
				if(this.out!=null)
					this.out.close();
				if(this.socket!=null)
					this.socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void MessageSend(JSONObject msg) throws IOException {
		System.out.println("send to client:"+msg);
		this.out.write((msg.toJSONString() + "\n").getBytes("UTF-8"));
		this.out.flush();
	}

	public BlockingQueue<JSONObject> getMessageQueue() {
		return messageQueue;
	}

	public void setRun(boolean run){
		this.run=run;
	}
}
