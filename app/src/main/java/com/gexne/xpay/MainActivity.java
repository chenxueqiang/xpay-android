package com.gexne.xpay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gexne.paylib.Xpay;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    /**
     * 本 URL 只供测试，实际需要换成自己的服务端地址
     */
    public static final String URL = "https://wx.bilifoo.com/demo/api/order_sign";

    private EditText goodsNameEdt, totalFeeEdt;
    private Button wapBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUi();
    }

    private void initUi() {
        goodsNameEdt = (EditText) findViewById(R.id.goods_name_edt);
        totalFeeEdt = (EditText) findViewById(R.id.total_fee_edt);
        wapBtn = (Button) findViewById(R.id.wap);
        wapBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wap:
                //先请求自己服务器，获得 charge
                getChargeAsync(Xpay.CHANNEL_WAP_ZHIMOU,
                        totalFeeEdt.getText().toString().trim(),
                        goodsNameEdt.getText().toString().trim());
                break;
        }
    }

    /**
     * 获取 charge 的方式根据需要与自己的服务端约定，这里只是给个例子
     */
    public void getChargeAsync(String channel, String totalFee, String goodsName) {
        FormBody formBody = new FormBody.Builder()
                .add("channel", channel)
                .add("total_fee", totalFee)
                .add("goods_name", goodsName)
                .build();
        final Request request = new Request.Builder()
                .url(URL)
                .post(formBody)
                .build();

        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "network error", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                /**
                 * 从自己服务器获取到 charge，调用 sdk 即可打开支付页面
                 */
                String chargeStr = response.body().string();
                Log.d(TAG, "chargeStr = " + chargeStr);

                Xpay.DEBUG = true;
                Xpay.createPayment(MainActivity.this, chargeStr);
            }
        });
    }

    /**
     * 重写 onActivityResult 方法，得到支付回调的结果
     * 注意：请勿直接使用客户端支付结果作为最终判定订单状态的依据，支付状态以服务端为准!!!
     * 在收到客户端同步返回结果时，请向自己的服务端请求来查询订单状态。
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Xpay.REQUEST_CODE_XPAY) {
            if (resultCode == RESULT_OK) {
                //有返回信息，可能成功，也可能失败
                String payResult = data.getStringExtra(Xpay.KEY_PAY_RESULT);
                String channel = data.getStringExtra(Xpay.KEY_CHANNEL);
                String extraMsg = data.getStringExtra(Xpay.KEY_EXTRA_MSG);
                String errorMsg = data.getStringExtra(Xpay.KEY_ERROR_MSG);
                Log.d(TAG, "payResult = " + payResult);
                Log.d(TAG, "channel = " + channel);
                Log.d(TAG, "extraMsg = " + extraMsg);
                Log.d(TAG, "errorMsg = " + errorMsg);
            }
        }
    }

}
