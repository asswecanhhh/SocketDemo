import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SocketServer {
	private static final int PORT = 9999;
	private List<Socket> mList = new ArrayList<Socket>();
	private ServerSocket server = null;
	private ExecutorService mExecutorService = null;
	
	public static void main(String[] args){
		new SocketServer();
	}
	
	public SocketServer() {
		try {
			server = new ServerSocket(PORT);
			mExecutorService = Executors.newCachedThreadPool();
			System.out.println("服务器已启动");
			Socket client = null;
			while(true) {
				client = server.accept();
				mList.add(client);
				mExecutorService.execute(new Service(client));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	class Service implements Runnable {
		private Socket socket;
		private BufferedReader in = null;
		private String msg = "";
		
		public Service(Socket socket){
			this.socket = socket;
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				msg = "服务器地址：" + this.socket.getInetAddress() + "come total:" 
						+ mList.size() + "(服务器发送)";
				this.sendmsg();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		public void run() {
			// TODO Auto-generated method stub
			try {
				while(true){
					if((msg = in.readLine())!= null){
						if(msg.equals("exit")){
							System.out.println("ssssssss");
							mList.remove(socket);
							in.close();
							msg = "user:" + socket.getInetAddress() + "exit tatal" 
									+ mList.size();
							socket.close();
							this.sendmsg();
							break;
						} else {
							msg = socket.getInetAddress() + ":" + msg +"(服务器发送)";
							this.sendmsg();
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		public void sendmsg() {
			System.out.println(msg);
			int num = mList.size();
			for(int i = 0; i < num; i++){
				Socket mSocket = mList.get(i);
				PrintWriter pout = null;
				try {
					pout = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(mSocket.getOutputStream())),true);
					pout.println(msg);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}
		
	}
}
