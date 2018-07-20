package com.example.develop.mqtt_test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener {

    public final String TAG = "MainActivity";
    public Button BTN_mqtt_publish;
    public TextView TV_log;
    public String LOG = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BTN_mqtt_publish = (Button) findViewById(R.id.mqtt_publish_btn);
        TV_log = (TextView)findViewById(R.id.log_tv);

        BTN_mqtt_publish.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.mqtt_publish_btn:
                Log.i(TAG, "publish button click!");
                LOG += "publish button click!" + "\n";
                TV_log.setText(LOG);

                MemoryPersistence memPer = new MemoryPersistence();

                final MqttAndroidClient client = new MqttAndroidClient(
                        getApplicationContext(), "tcp://10.42.0.1:1884", UUID.randomUUID().toString(), memPer);

                try {
                    Log.i(TAG,"Start connect target url");
                    LOG += "Start connect target url" + "\n";
                    TV_log.setText(LOG);
                    client.connect(null, new IMqttActionListener() {

                        @Override
                        public void onSuccess(IMqttToken mqttToken) {
                            Log.i(TAG, "Client connected");
                            LOG += "Client connected" + "\n";
                            TV_log.setText(LOG);
                            Log.i(TAG, "Topics="+mqttToken.getTopics());
                            LOG += "Topics="+mqttToken.getTopics() + "\n";
                            TV_log.setText(LOG);

                            String jsonObj = "{"
                                    + "\"subject\":\"wifi\","
                                    + "\"CTRLID\":1100,"
                                    + "\"gateway\":580,"
                                    + "\"ID\":180,"
                                    + "\"speed\":50,"
                                    + "\"gps\":\"131.2542;25.2555\""
                                    + "}";
                            MqttMessage message = new MqttMessage(jsonObj.getBytes());
                            message.setQos(2);
                            message.setRetained(false);

                            try {
                                client.publish("Sensor/wifi", message);
                                Log.i(TAG, "Message published");
                                LOG += "Message published" + "\n";
                                TV_log.setText(LOG);

                                client.disconnect();
                                Log.i(TAG, "client disconnected");
                                LOG += "client disconnected" + "\n";
                                TV_log.setText(LOG);

                            } catch (MqttPersistenceException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();

                            } catch (MqttException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(IMqttToken arg0, Throwable arg1) {
                            // TODO Auto-generated method stub
                            Log.i(TAG, "Client connection failed: "+arg1.getMessage());
                            LOG += "Client connection failed: "+arg1.getMessage() + "\n";
                            TV_log.setText(LOG);
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                    Log.i(TAG,"[ERROR]Start connect target url error.");
                    LOG += "[ERROR]Start connect target url error."  + "\n";
                    TV_log.setText(LOG);
                }
                break;
        }
    }
}
