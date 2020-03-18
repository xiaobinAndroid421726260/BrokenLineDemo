package com.dbz.example;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<BrokenLineView.XValue> xValues = new ArrayList();
    private List<BrokenLineView.YValue> yValues = new ArrayList();
    private List<BrokenLineView.LineValue> lineValues = new ArrayList();

    private List<BrokenLineView.XValue> xValues1 = new ArrayList();
    private List<BrokenLineView.YValue> yValues1 = new ArrayList();
    private List<BrokenLineView.LineValue> lineValues1 = new ArrayList();

    private BrokenLineView mBrokenLineView, mBrokenLineView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBrokenLineView = findViewById(R.id.lineview);
        mBrokenLineView1 = findViewById(R.id.lineview1);
        initView();
    }

    private void initView() {

        /**
         * 注意 我这里用的是随机数据， 当用接口返回的数据时  要取最大值设置数据， 但是Y轴也是最高值，
         * 这样在显示数据时 Y轴上点位信息数据会向上顶， 也就是说会顶的看不见 ， 所以在绘制的时候，
         * Y轴的最大值要比真实的数据要大， 最好在最大值上乘以 百分之二十
         * 比如： 最大值是10   10 + 10 * 0.2
         */

        //模拟折线数据
        for (int i = 1; i <= 10; i++) {
            BrokenLineView.XValue xValue = new BrokenLineView.XValue(i, "3月" + i + "号");
            xValues.add(xValue);
            double value = new BigDecimal(Math.random() * 10).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            BrokenLineView.LineValue lineValue = new BrokenLineView.LineValue(value, value + "");
            lineValues.add(lineValue);
        }
        for (int i = 0; i <= 12; i++) {
            BrokenLineView.YValue yValue = new BrokenLineView.YValue(i, i + "");
            yValues.add(yValue);
        }
        mBrokenLineView.setValue(lineValues, xValues, yValues);

        //模拟折线数据
        for (int i = 1; i <= 10; i++) {
            BrokenLineView.XValue xValue = new BrokenLineView.XValue(i, "3月" + i + "号");
            xValues1.add(xValue);
            int value = new BigDecimal(Math.random() * 500).setScale(2, BigDecimal.ROUND_HALF_UP).intValue();
            BrokenLineView.LineValue lineValue = new BrokenLineView.LineValue(value, value + "");
            lineValues1.add(lineValue);
        }
        for (int i = 0; i <= 600; i++) {
            BrokenLineView.YValue yValue = new BrokenLineView.YValue(i, i + "");
            yValues1.add(yValue);
        }
        mBrokenLineView1.setValue(lineValues1, xValues1, yValues1);
        mBrokenLineView1.setSelectIndex(8);
    }
}