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

	// ����� �͵�
	private int count = 0, tmp = 0;
	private int ischecked = 0;
	float a = 0, min = 0, max = 0, Stmp = 0;
	double Stime, Etime;
	double Time_result;
	String Layer = "����";
	String before_Layer="����";

	// �����͸� ������ ������
	float[] m_acc_data = null, m_mag_data = null;
	// float[] m_azi_data = null;
	float[] m_rotation = new float[9];
	float[] m_result_data = new float[3];

	// �� ����
	ArrayList<Float> Acc_Z = new ArrayList<Float>();
	ArrayList<String> xtmp = new ArrayList<String>();
	ArrayList<String> ytmp = new ArrayList<String>();

	// ���� üũ
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
		 * //////������ ���� bt = (Button) findViewById(R.id.button1);
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
				if (Layer.equals("")) {// ���� �Է� �ȉ�����
					Toast.makeText(getApplicationContext(), "��..������ �Է��آZ...!",
							Toast.LENGTH_SHORT).show();
					isstart = false;
				} else { // ���� �Է��ؾ߸� ����

					if (isstart == true) { // �̹� �����������
						Toast.makeText(getApplicationContext(),
								"�̹� ���� �����߾�...��!", Toast.LENGTH_SHORT).show();
					} else {// ���� ����

						// delete = 0;
						a = min = max = 0;
						Time_result++;
						idnum++;
						count++;

						// //���ͳ� ���� ������ �� ó�� �ڸ�����!
						x = y = 500;
						xtmp.clear();
						ytmp.clear();
						step = 0;
						tmp = 0;
						xyResult = "-999";
						
						Toast.makeText(getApplicationContext(),
								"��ġ ������ �����մϴ�.", Toast.LENGTH_LONG).show();
						
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
				if (isstart == false) { // ������ �ȵǾ��־���
					Toast.makeText(getApplicationContext(),
							"�ٴݴ�Ȳ����������� ���ϴ� ���ۺ��� �آZ...!", Toast.LENGTH_LONG)
							.show();
				} else {// ���۵Ǿ��ִ��� ��������
					for (tmp = 0; tmp < step; tmp++) {
						xyResult();
					}
					Toast.makeText(getApplicationContext(), "��ġ ������ �����մϴ�.",
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
				throw new Exception("������?!");
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
		Toast.makeText(this, "������ �����ϰ� ���ǿ�...", Toast.LENGTH_SHORT).show();

		thread.interrupt();

	}

	public void Counter(ArrayList<Float> mAcc_Data, float degree) {
		if (index != 0) {
			/*
			 * //1����� if (mAcc_Data.get(index-1)<0 && mAcc_Data.get(index) > 0)
			 * FlagA = true; else if (mAcc_Data.get(index-1)>0 &&
			 * mAcc_Data.get(index) < 0) FlagB = true;
			 * 
			 * if (FlagA == true && FlagB ==true){ mCounter++; FlagA = false;
			 * FlagB = false; }
			 */

			/*
			 * // 2����� : z���� - + - �� �ٲ�鼭 1���� Ŭ ���� �� �ֱ�� if
			 * (mAcc_Data.get(index-1)<0 && mAcc_Data.get(index) > 0) FlagA =
			 * true; else if (mAcc_Data.get(index-1)>0 && mAcc_Data.get(index) <
			 * 0) FlagB = true;
			 * 
			 * if (FlagA == true && mAcc_Data.get(index) > 1) FlagC = true;
			 * 
			 * if (FlagC == true && FlagB==true) { mCounter ++; FlagA = false;
			 * FlagB = false; FlagC = false; }
			 */// /////////////////////////////

			// 3�����
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
			// ���� ������ ������ �������� ���
			// ��ġ �����͸� �����Ѵ�.
			m_acc_data = event.values.clone();

		} else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			// �ڱ��� ������ ������ �������� ���
			// ��ġ �����͸� �����Ѵ�.
			m_mag_data = event.values.clone();
		} else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			// ���� ������ ������ �������� ���
			// ��ġ �����͸� �����Ѵ�.
			m_azi_data = event.values.clone();
		}
		

		
		// Toast.makeText(this, "m_acc_data = "
		// +m_acc_data+"\nm_mag_data = "+m_mag_data[0],
		// Toast.LENGTH_SHORT).show();
		// �����Ͱ� �����ϴ� ���
		if (m_acc_data != null && m_mag_data != null && m_azi_data != null) {
			// ���� �����Ϳ� �ڱ��� �����ͷ� ȸ�� ��Ʈ������ ��´�.
			SensorManager.getRotationMatrix(m_rotation, null, m_azi_data,
					m_mag_data);
			// ȸ�� ��Ʈ������ ���� �����͸� ��´�.
			SensorManager.getOrientation(m_rotation, m_result_data);

			String str;
			// Radian ���� Degree ������ ��ȯ�Ѵ�.
			m_result_data[0] = (float) Math.toDegrees(m_result_data[0]);

			// 0 ������ ���� ��� 360�� ���Ѵ�.
			if (m_result_data[0] < 0)
				m_result_data[0] += 360;

			// int delete = 0;
			if (isstart) {

				//�߰��� ������ �ٲܰ���� ����
				edit = (EditText)findViewById(R.id.Editlayer);
				if(edit.getText().toString().equals(Layer) == false){
					Toast.makeText(this, "���� ���߿� �������� ����...", Toast.LENGTH_SHORT).show();
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
