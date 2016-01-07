package register;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.iknowway.R;

public class Register extends Activity implements SensorEventListener {

	private Button start;
	private Button stop;
	private ImageView map;
	private EditText edit;
	private boolean isstart = false;

	// 잡다한 것들
	private int count = 0, tmp = 0;
	private int ischecked = 0;
	float a = 0, min = 0, max = 0, Stmp = 0;
	double Stime, Etime;
	double Time_result;
	String Layer = "읎어";
	String before_Layer="읎어";

	// 데이터를 저장할 변수들
	float[] m_acc_data = null, m_mag_data = null;
	// float[] m_azi_data = null;
	float[] m_rotation = new float[9];
	float[] m_result_data = new float[3];

	// 값 저장
	ArrayList<Float> Acc_Z = new ArrayList<Float>();
	ArrayList<String> xtmp = new ArrayList<String>();
	ArrayList<String> ytmp = new ArrayList<String>();

	// 걸음 체크
	private int index = 0;
	private boolean FlagA = false;
	private boolean FlagB = false;
	private boolean FlagC = false;
	private boolean FlagD = false;
	private boolean FlagE = false;

	double x = 250;
	double y = 250;
	String xyResult = "-999";
	int step = 0;

	int idnum = 0;

	// Sensor sensor;
	SensorManager SManager;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		// TODO Auto-generated method stub
		start = (Button) findViewById(R.id.start);
		stop = (Button) findViewById(R.id.stop);
		map = (ImageView) findViewById(R.id.register_image);
		edit = (EditText) findViewById(R.id.Editlayer);

		SManager = (SensorManager) this
				.getSystemService(Context.SENSOR_SERVICE);

		SManager.registerListener(this,
				SManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
				SensorManager.SENSOR_DELAY_UI);

		SManager.registerListener(this,
				SManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_UI);

		SManager.registerListener(this,
				SManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_UI);

		/*
		 * //////데이터 리셋 bt = (Button) findViewById(R.id.button1);
		 * bt.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated
		 * methodstub upload(0, 1, 0); } });
		 */

		start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Layer = edit.getText().toString();
				before_Layer = edit.getText().toString();

				// TODO Auto-generated method stub
				if (Layer.equals("")) {// 층수 입력 안됬을때
					Toast.makeText(getApplicationContext(), "츠..층수를 입력해줫...!",
							Toast.LENGTH_SHORT).show();
					isstart = false;
				} else { // 층수 입력해야만 시작

					if (isstart == true) { // 이미 시작했을경우
						Toast.makeText(getApplicationContext(),
								"이미 수집 시작했어...ㅅ!", Toast.LENGTH_SHORT).show();
					} else {// 새로 시작

						// delete = 0;
						a = min = max = 0;
						Time_result++;
						idnum++;
						count++;

						// //인터넷 연결 끝나고 맨 처음 자리부터!
						x = y = 500;
						xtmp.clear();
						ytmp.clear();
						step = 0;
						tmp = 0;
						xyResult = "-999";
						
						Toast.makeText(getApplicationContext(),
								"위치 수집을 시작합니다.", Toast.LENGTH_LONG).show();
						
						ischecked = 1;
					}
					isstart = true;
				}
				map.setImageResource(R.drawable.square);
			}
		});

		stop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isstart == false) { // 시작이 안되어있었엉
					Toast.makeText(getApplicationContext(),
							"다닫당황하지지말고고 이일단 시작부터 해줫...!", Toast.LENGTH_LONG)
							.show();
				} else {// 시작되어있던걸 종료하잣
					for (tmp = 0; tmp < step; tmp++) {
						xyResult();
					}
					Toast.makeText(getApplicationContext(), "위치 수집을 종료합니다.",
							Toast.LENGTH_LONG).show();
					finish();
				}
				isstart = false;
			}
		});
	}

	public void xyPotision(float step_length, double degree) {

		step_length *= 3;
		double radian = Math.toRadians(degree);

		x += step_length * Math.cos(radian);
		y += step_length * Math.sin(radian);

		xyResult += "%20" + x + "%20" + y;

		xtmp.add("" + x);
		ytmp.add("" + y);
	}

	public void xyResult() {
		try {
			ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnected()) {
				upload(count, 0, 0);
			} else {
				throw new Exception("실패해?!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("deprecation")
	public void upload(final int count, final int delete, final double Time) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {

				TelephonyManager mng = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				String num = mng.getLine1Number();

				if (delete == 1) {
					xtmp.add("-999");
					ytmp.add("-999");
				}

				try {
					HttpClient client = new DefaultHttpClient();

					String str = "http://edu.argos.or.kr/~nongyack/project/getposition.php?count="
							+ count
							+ "&x="
							+ xtmp.get(tmp)
							+ "&y="
							+ ytmp.get(tmp)
							+ "&delete="
							+ delete
							+ "&id="
							+ num + "&time=" + Time + "&layer="+Layer;

					Log.d("----------------------------",
							"--------------------------");
				/*
					Log.d("count", "" + tmp);
					Log.d("x", xtmp.get(tmp));
					Log.d("y", ytmp.get(tmp));
*/
					HttpPost post = new HttpPost(str);
					client.execute(post);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		thread.start();
		/*
		 * TextView asdf = (TextView) findViewById(R.id.textView1);
		 * 
		 * asdf.setText("" + step);
		 */
		Toast.makeText(this, "데이터 전송하고 이쪄여...", Toast.LENGTH_SHORT).show();

		thread.interrupt();

	}

	public void Counter(ArrayList<Float> mAcc_Data, float degree) {
		if (index != 0) {
			/*
			 * //1번경우 if (mAcc_Data.get(index-1)<0 && mAcc_Data.get(index) > 0)
			 * FlagA = true; else if (mAcc_Data.get(index-1)>0 &&
			 * mAcc_Data.get(index) < 0) FlagB = true;
			 * 
			 * if (FlagA == true && FlagB ==true){ mCounter++; FlagA = false;
			 * FlagB = false; }
			 */

			/*
			 * // 2번경우 : z값이 - + - 로 바뀌면서 1보다 클 때를 한 주기로 if
			 * (mAcc_Data.get(index-1)<0 && mAcc_Data.get(index) > 0) FlagA =
			 * true; else if (mAcc_Data.get(index-1)>0 && mAcc_Data.get(index) <
			 * 0) FlagB = true;
			 * 
			 * if (FlagA == true && mAcc_Data.get(index) > 1) FlagC = true;
			 * 
			 * if (FlagC == true && FlagB==true) { mCounter ++; FlagA = false;
			 * FlagB = false; FlagC = false; }
			 */// /////////////////////////////

			// 3번경우
			if (mAcc_Data.get(index - 1) < -1 && mAcc_Data.get(index) > -1)
				FlagA = true;
			else if (FlagA == true && mAcc_Data.get(index) > 1)
				FlagB = true;
			else if (mAcc_Data.get(index - 1) > 0 && mAcc_Data.get(index) < 0)
				FlagC = true;
			else if (FlagC == true && mAcc_Data.get(index) < -1)
				FlagD = true;

			if (FlagB == true && FlagD == true) {
				step++;
				Log.d("step", "" + step);
				xyPotision(1, degree);
				FlagA = false;
				FlagB = false;
				FlagC = false;
				FlagD = false;
			}

		}
		index++;

	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(final SensorEvent event) {
		// TODO Auto-generated method stub

		float[] m_azi_data = null;
		// TODO Auto-generated method stub

		if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
			// 가속 센서가 전달한 데이터인 경우
			// 수치 데이터를 복사한다.
			m_acc_data = event.values.clone();

		} else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			// 자기장 센서가 전달한 데이터인 경우
			// 수치 데이터를 복사한다.
			m_mag_data = event.values.clone();
		} else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			// 가속 센서가 전달한 데이터인 경우
			// 수치 데이터를 복사한다.
			m_azi_data = event.values.clone();
		}
		

		
		// Toast.makeText(this, "m_acc_data = "
		// +m_acc_data+"\nm_mag_data = "+m_mag_data[0],
		// Toast.LENGTH_SHORT).show();
		// 데이터가 존재하는 경우
		if (m_acc_data != null && m_mag_data != null && m_azi_data != null) {
			// 가속 데이터와 자기장 데이터로 회전 매트릭스를 얻는다.
			SensorManager.getRotationMatrix(m_rotation, null, m_azi_data,
					m_mag_data);
			// 회전 매트릭스로 방향 데이터를 얻는다.
			SensorManager.getOrientation(m_rotation, m_result_data);

			String str;
			// Radian 값을 Degree 값으로 변환한다.
			m_result_data[0] = (float) Math.toDegrees(m_result_data[0]);

			// 0 이하의 값인 경우 360을 더한다.
			if (m_result_data[0] < 0)
				m_result_data[0] += 360;

			// int delete = 0;
			if (isstart) {

				//중간에 층수를 바꿀경우의 문제
				edit = (EditText)findViewById(R.id.Editlayer);
				if(edit.getText().toString().equals(Layer) == false){
					Toast.makeText(this, "수집 도중에 변경하지 마쇼...", Toast.LENGTH_SHORT).show();
					edit.setText(before_Layer);
				}
				/*
				 * if(m_acc_data[2]<0){ if(min<m_acc_data[2]){ min =
				 * m_acc_data[2]; } } if(min<-1) Stmp++;
				 * 
				 * if(m_acc_data[2]>0){ if(max>m_acc_data[2]){ max =
				 * m_acc_data[2]; } } if(max>1) Stmp++;
				 * 
				 * if(Stmp >= 2) { step++; min=max=0; }
				 */// //////////////////////
				Acc_Z.add(m_acc_data[2]);
				Counter(Acc_Z, m_result_data[0]);

				ischecked = 1;
				/*
				 * Etime = System.currentTimeMillis(); Stime = Etime;
				 */// ///////////////////////////

				// xyPotision(1,m_result_data[0]);
				// Log.d("yeeeeh", "sensorOn");

			} else {


			}
		}
	}

}
