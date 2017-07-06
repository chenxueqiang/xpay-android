# XPay-android SDK 使用文档

##配置工程
一、拷贝 xpay.aar 到 libs 目录

二、build.gradle 中添加

```
repositories {  
    flatDir {  
        dirs 'libs'  
    }  
}  

dependencies {  
    compile(name:'xpay', ext:'aar')  
}  
```

##开始接入
一、获得 Charge 对象

Charge 是一个包含支付信息的 JSON 字符串，是 Xpay SDK 发起支付的必要参数。该参数需要请求用户服务器获得，服务端生成 charge 的方式参考 服务端接入文档。SDK 中的 demo 获取 charge 的方法仅供用户参考。

二、发起支付

因为 Xpay 已经封装好了相应的调用方法，所以只需要调用支付方法即可调起支付流程：

```
Xpay.createPayment(activity, charge);
```

三、获取支付状态

重写 Activity 的 onActivityResult 方法，获得支付结果。
最终支付结果以用户服务端为准。

```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == Xpay.REQUEST_CODE_XPAY) {
        if (resultCode == RESULT_OK) {
            String payResult = data.getStringExtra(Xpay.KEY_PAY_RESULT);
            String channel = data.getStringExtra(Xpay.KEY_CHANNEL);
            String extraMsg = data.getStringExtra(Xpay.KEY_EXTRA_MSG);
            String errorMsg = data.getStringExtra(Xpay.KEY_ERROR_MSG);
        }
    }
}
```

**调试时可打开 Xpay 日志**

```
Xpay.DEBUG = true;
```

## 注意事项
* 请勿直接使用客户端支付结果作为最终判定订单状态的依据，支付状态以服务端为准!!!在收到客户端同步返回结果时，请向自己的服务端请求来查询订单状态。


I