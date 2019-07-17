package com.example.joanna.myapplication;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

import android.os.Handler;
import android.os.Message;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.model.ValueShape;

public class MainActivity extends AppCompatActivity {

    private TextView lightdata;//show light_data
    private TextView updowns;//show up-downs counts
    private TextView gesture;
    private TextView music;
    private Button start;
    private Button stop;//start & stop listen light
    private SensorManager sensorManager;//sensormanager -- get sensor service,choice sensor type
    private Sensor sensor;//sensor object,store light data
    private SensorEventListener sensorEventListener;//sensor event listener,listen sensor event

    private LineChartView lineChart;
    private LineChartView linechartview;
    private Timer timer;
    //private List<PointValue> points;
    private List<Line> lines;
    private Axis axisX,axisY;
    private LineChartData linechartdata;
    private int position = 0;
    private Random random = new Random();
    private List<PointValue> pointvalues;
    private LinkedList<PointValue> pointv;
    private float x;

    private long starttime = 0;
    private long lasttime;
    private long currenttime;
    private int startmark = 0;
    private int mul = 2;

    private List<List> lightlist;
    private List<Long> lightandtime;

    private long lastlight;
    private long lastchangetime;

    private int currenttimecount;
    private List<List> currentlightlist;

    private int down = 0;
    private int downs = 0;
    private int up = 0;
    private int ups = 0;
    private int downdown;
    private int top = 0;
    private int bottom = 1000;
    private int upmark = 0;
    private int downmark = 0;
    private long lastlightud;
    private long lastchangetimeud;
    private List<Long> bottomtime;
    List<Long> bottoms ;

    private int g1;
    private int g2;
    private int g3;
    private int gcount;
    private int lastg = 0;

    private MediaPlayer mp;
    private float vo = 0.5f;
    private String mu = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init text & button
        lightdata = (TextView) findViewById(R.id.textView);
        updowns = (TextView)findViewById(R.id.updowns);
        gesture = (TextView)findViewById(R.id.gesture);
        music = (TextView)findViewById(R.id.music);
        start = (Button) findViewById(R.id.button);
        stop = (Button) findViewById(R.id.button2);

        //init sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//get sensor service
        sensor = sensorManager.getDefaultSensor(sensor.TYPE_LIGHT); //sensor type

        lightlist = new ArrayList<List>();

        setlistener();

        lineChart = (LineChartView) findViewById(R.id.chart);
        initView();

        timer = new Timer();

        mp = MediaPlayer.create(this,R.raw.music);
        mp.setLooping(true);
        mp.seekTo(0);
        mp.setVolume(0.5f,0.5f);

    }

    private void setlistener(){

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                lightandtime = new ArrayList<Long>();
                currentlightlist = lightlist;
                if(startmark == 0){
                    startmark = 1;
                    sensorManager.registerListener(sensorEventListener,sensor,SensorManager.SENSOR_DELAY_GAME);
                }
                currenttime = System.currentTimeMillis() - starttime;
                String infro = "";//store light data
                infro = "Light" + sensorEvent.values[0];
                lightdata.setText(infro);
                currenttime = currenttime / 100;
                lightandtime.add(currenttime);
                lightandtime.add((long)sensorEvent.values[0]);
                lightlist.add(lightandtime);

                System.out.println(" "+  lightandtime);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //register sensor
                sensorManager.registerListener(sensorEventListener,sensor,SensorManager.SENSOR_DELAY_GAME);
                //timer = new Timer();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sensorManager.unregisterListener(sensorEventListener);
                //timer.cancel();
            }
        });
    }

    private void initView(){
        linechartview = (LineChartView) findViewById(R.id.chart);
        pointvalues = new ArrayList<>();
        lines = new ArrayList<>();

        axisX = new Axis();
        axisX.setLineColor(Color.parseColor("#aab2bd"));
        axisY = new Axis();
        axisY.setLineColor(Color.parseColor("#aab2bd"));
        axisY.setTextColor(Color.parseColor("#aab2bd"));

        //init linecharat data
        linechartdata = new LineChartData();
        linechartdata.setAxisXBottom(axisX);
        linechartdata.setAxisYLeft(axisY);

        linechartview.setLineChartData(linechartdata);
        Viewport port = initViewPort(0,50);
        linechartview.setCurrentViewportWithAnimation(port);
        linechartview.setInteractive(false);
        linechartview.setScrollEnabled(true);
        linechartview.setValueTouchEnabled(true);
        linechartview.setFocusableInTouchMode(true);
        linechartview.setViewportCalculationEnabled(false);
        linechartview.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        linechartview.startDataAnimation();


    }

    @Override
    protected void onResume() {
        super.onResume();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                lines.clear();
                g1= 0;
                g2 = 0;
                g3 =0;
                List<Long> bottoms = new ArrayList<>();
                pointv = new LinkedList<>();
                //add new point
                long tt = System.currentTimeMillis() - starttime;
                currenttimecount = (int) tt / 100 ;
                int size = lightlist.size();
                int down = currenttimecount - 10;
                int downdown = currenttimecount - 50;
                downs = 0;
                ups = 0;
                if(size == 0){
                    //System.out.println("if");
                    if(starttime == 0){
                            //System.out.println("if if");
                            starttime = System.currentTimeMillis();
                            System.out.println("运行  start= ");
                            tt = System.currentTimeMillis() - starttime;
                            currenttimecount = (int) tt / 100 ;
                        }
                    PointValue value = new PointValue(currenttimecount, 0);
                    value.setLabel("00:00");
                    pointv.addFirst(value);
                    x = value.getX();
                    downs = 0;
                    ups = 0;

                }else{
                    //System.out.println("else");
                    int i = size -1;
                    List<Long> current = new ArrayList<>();
                    current = lightlist.get(i);
                    //System.out.println("list = "+  current);
                    lastchangetime = current.get(0);
                    lastlight = current.get(1);
                    //up-downs
                    if(lastchangetime < downdown){
                        ups = 0;
                        downs = 0;
                        System.out.println("lastone < 50 ");
                    }else{
                        int d = size - 1;
                        List<Long> currentupdown = new ArrayList<>();
                        currentupdown = lightlist.get(d);
                        lastchangetimeud = currentupdown.get(0);
                        lastlightud = currentupdown.get(1);
                        for(d = size - 1;lastchangetimeud >= downdown;d -- ){
                            if(d < 0 || d - 1 < 0){
                                //System.out.println("lastone" +  lastchangetimeud);
                                break;
                            }
                            currentupdown = lightlist.get(d - 1);
                            long currenrudtime = currentupdown.get(0);
                            long currentudlight = currentupdown.get(1);
                            int gap;
                            if(currentudlight < lastlightud){
                                //downs /
                                //     /
                                //    /
                                //bottom = 1000;
                                //System.out.println("d" +  lastlightud + "  "+ currentudlight);
                                if(upmark == 1){
                                    upmark = 0;
                                }
                                if(top == 0){
                                    top = (int) lastlightud ;
                                    bottom = 1000;
                                }
                                gap = (int)Math.abs(currentudlight - top);
                                float gg = 0.15f * top;
                                if(gap  >= 0.15f * top){
                                    if(downmark == 0){
                                        downs ++;
                                        downmark = 1;
                                        //System.out.println("downs" + downs);
                                        upmark = 0;
                                    }
                                }

                            }else{
                                //ups \
                                //     \
                                //      \
                                //System.out.println("u"+  lastlightud + "  "+ currentudlight);
                                if(bottom == 1000){
                                    bottom = (int) lastlightud;
                                    top = 0;
                                    if(downmark == 1){
                                        bottoms.add(lastchangetimeud);
                                        System.out.println("bottoms  "+  lastchangetimeud);
                                        downmark = 0;
                                    }
                                }
                                gap = (int)Math.abs(currentudlight - bottom);
                                float gg = 0.15f * bottom;
                                if(gap >= 0.15f * bottom){
                                    if(upmark == 0){
                                        ups ++;
                                        //bottoms.add(lastchangetimeud);
                                        upmark = 1;
                                        //System.out.println("ups" + ups);
                                        downmark = 0;
                                    }
                                }
                            }

                        lastlightud = currentudlight;
                        lastchangetimeud = currenrudtime;

                        }

                    }

                    //update charts
                    if(lastchangetime < down){

                        //System.out.println("else if");
                        PointValue value = new PointValue(currenttimecount, lastlight);
                        value.setLabel("00:00");
                        pointv.addFirst(value);
                        x = currenttimecount;
                        //System.out.println("va = "+  value);
                    }
                    for(i = size - 1;lastchangetime >= down;i -- ){
                        //System.out.println("time = "+  lastchangetime + "light= "+lastlight );
                        if(i == size - 1 && lastchangetime <= down){
                            List<Long> lightandtimes;
                            lightandtimes = new ArrayList<>();
                            PointValue value = new PointValue(currenttimecount, lastlight);
                            lightandtimes.add((long) currenttimecount);
                            lightandtimes.add((long)lastlight);
                            lightlist.add(lightandtimes);
                            value.setLabel("00:00");
                            pointv.addFirst(value);
                            x = currenttimecount;
                        }else{
                            PointValue value = new PointValue(lastchangetime , lastlight);
                            value.setLabel("00:00");
                            pointv.addFirst(value);
                            x = currenttimecount;

                        }
                        if(i == 0){
                            break; }


                        int ss = pointvalues.size();

                        List<Long> currents = new ArrayList<>();
                        currents = lightlist.get(i - 1);
                        //System.out.println("list = "+  currents);
                        lastchangetime = currents.get(0);
                        lastlight = currents.get(1);
                    }

                }
                int ss = pointv.size();
                for(int j = 0;j <= ss - 1;j ++  ){
                    PointValue value = pointv.get(j);
                    pointvalues.add(value);
                    //System.out.println("v = "+  value);
                }

                ss = pointvalues.size();

                //System.out.println("time = "+  tt);
                System.out.println("time = "+  currenttimecount);
                System.out.println("posotion = "+  x);
                System.out.println("lightlist = "+  lightandtime);


                //get new lines
                Line line = new Line(pointvalues);
                line.setColor(Color.RED);
                line.setShape(ValueShape.CIRCLE);
                line.setCubic(true);

                lines.clear();
                lines.add(line);
                linechartdata = new LineChartData(lines);
                linechartdata.setAxisXBottom(axisX);
                linechartdata.setAxisYLeft(axisY);
                linechartview.setLineChartData(linechartdata);

                Viewport port;
                if(x > 50){
                    port = initViewPort(x - 50,x);

                }else{
                    port = initViewPort(0,x);
                }


                linechartview.setCurrentViewport(port);//current window
                Viewport maxport = initMaxViewPort(x);
                linechartview.setMaximumViewport(maxport);

               //can't update UI(textview) in thread timer -- handler
                Message message = new Message();
                //Message message = Message().obtain(handler);
                message.what = 1;
                message.arg1 = Math.min(ups,downs);
                handler.sendMessage(message);
                //updowns.setText(infro);

                //int t = bottoms.size();
                //System.out.println("size = "+  bottoms.size());
                gcount = 0;
                long timelast = currenttimecount;
                long timecurr;
                int ttt = bottoms.size() - 1;
                for(int t = 0;t <= ttt ;t++){
                    timecurr =bottoms.get(t);
                    int gapb = Math.abs((int)(timecurr - timelast));
                    System.out.println("bgap = "+  gapb);
                    if(gapb <= 10){
                        gcount++;
                    }else{
                        break;
                    }
                    //System.out.println("btime = "+  timecurr);
                    timelast = timecurr;
                }
                System.out.println("uodaowns = "+ ups +" "+ downs);
                System.out.println("gg  "+  gcount);

                message = new Message();
                //Message message = Message().obtain(handler);
                message.what = 2;
                message.arg1 = gcount;
                handler.sendMessage(message);
            }
        //}
        },1000,1000);


    }

    private Viewport initViewPort(float left,float right){
        Viewport port = new Viewport();
        port.top = 1000;
        port.bottom = 0;
        port.left = left;
        port.right = right;
        return port;
    }

    private Viewport initMaxViewPort(float right){
        Viewport port = new Viewport();
        port.top = 1000;
        port.bottom = 0;
        port.left = 0;
        port.right = right + 50;
        return port;
    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message){
            switch (message.what) {
                case 1:
                    String infro = "";
                    infro = "updowns: " + message.arg1;
                    updowns.setText(infro);
                    break;
                case 2:
                    int g = message.arg1;
                    String inf = "";
                    if(g == 0){
                        if(lastg == 1){
                            if(!mp.isPlaying()){
                                mp.start();
                                inf = "gesture: " + "Gesture 1 ";
                                mu = "Start";
                            }else{
                                mp.pause();
                                inf = "gesture: " + "Gesture 1 ";
                                mu = "Pause";
                            }
                        }else if(lastg == 2){
                            inf = "gesture: " + "Gesture 2";
                            if(vo!=1){
                                vo = vo + 0.1f;}
                            mp.setVolume(vo,vo);
                            inf = "gesture: " + "Gesture 2";
                            mu = "volume up" + vo;

                        }else if(lastg == 0){
                            inf = "gesture: " + "None";
                        }
                        lastg = 0;
                        gesture.setText(inf);
                        music.setText(mu);
                    }else if(g == 1){
                        lastg = 1;
                        inf = "gesture: " + "None";
                        gesture.setText(inf);
                        music.setText(mu);

                        //SystemClock.sleep(2000);
                    }else if(g == 2){
                        lastg = 2;
                        inf = "gesture: " + "None";
                        gesture.setText(inf);
                        music.setText(mu);
                        //inf = "gesture: " + "Gesture 2";
                        //SystemClock.sleep(1000);
                    }else if(g == 3){
                        if(vo>=0.1){
                            vo = vo - 0.1f;}
                        inf = "gesture: " + "Gesture 3";
                        mp.setVolume(vo,vo);
                        mu = "volume down" + vo;
                        gesture.setText(inf);
                        music.setText(mu);
                        lastg = 3;
                    }else{
                        inf = "gesture: " + "Wrong";
                        gesture.setText(inf);
                        music.setText(mu);
                        lastg = 0;
                    }
                    //gesture.setText(inf);
                    break;

            }
        }
    };

}
