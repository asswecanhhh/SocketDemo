package com.example.socketclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	private TextView tv_msg = null;
	private EditText et_msg = null;
	private Button btn_send = null;
	private static final String HOST = "10.0.2.2";
	private static final int PORT = 9999;
	private Socket socket = null;
	private BufferedReader in = null;
	private PrintWriter out = null;
	private String content = "";
	private String contents = "";
	private  Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			System.out.println("handler");
			if(msg.what == 0x234) {
				tv_msg.setText(content);
				et_msg.setText("");
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tv_msg = (TextView) findViewById(R.id.tv_msg);
		et_msg = (EditText) findViewById(R.id.et_msg);
		btn_send = (Button) findViewById(R.id.btn_msg);

		btn_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String msg = et_msg.getText().toString();
				if(socket.isConnected()) {
					if(!socket.isOutputShutdown()) {
						out.println(msg);
					}
				}
			}
		});

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					socket = new Socket(HOST, PORT);
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					ShowDialog("login exception:" + e.getMessage());
				}
				try {
					while(true) {
						if(!socket.isClosed()){
							if(socket.isConnected()){
								if(!socket.isInputShutdown()) {
									if((contents = in.readLine()) != null){
										content += contents + "\n";
										Message mesg = Message.obtain();
										mesg.what = 0x234;
										handler.sendMessage(mesg);
										System.out.println("message");
									}
								}
							}
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println(e.toString());
					e.printStackTrace();
					//ShowDialog("login exception:" + e.getMessage());
				}
			}

		}).start();
	}

	public void ShowDialog(String msg){
		new AlertDialog.Builder(this).setTitle("notification").setMessage(msg)
		.setPositiveButton("ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		}).show();
	}

}
