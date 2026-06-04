const app = getApp()
Page({
  data: {
    realUrl: '',
    nickName: '',//用户的名称
    avatarUrl: '', //用户的头像
    saveUserInfo: { username: "", password: "" }
  },
  doLogin: function (e) {
    var me = this;
    var formObject = e.detail.value;
    var username = formObject.username;
    var password = formObject.password;
    //简单验证
    if (username.length == 0 || password.length == 0) {   //反馈数据
      wx.showToast({
        title: '小主,用户名和密码不能为空哦',
        icon: 'none',//图标
        duration: 3000
      })
    }
    else {
      wx.showLoading({
        title: '请等待...',
      })
      // 调用后端登录接口
      wx.request({
        url: app.getApiUrl('/login'),
        method: 'POST',
        header: {
          'content-type': 'application/json'
        },
        data: {
          username: username,
          password: password
        },
        success: function(res) {
          wx.hideLoading();
          if (res.data.status == 200) {
            wx.showToast({
              title: '小主,登陆成功啦',
              icon: 'none',
              duration: 3000
            })
            // 保存用户信息到本地存储
            var saveUserInfo = me.data.saveUserInfo;
            saveUserInfo.password = password;
            saveUserInfo.username = username;
            app.saveUserInfo(saveUserInfo);
            // 保存用户信息到全局变量
            app.setGlobalUserInfo(res.data.data);
            var realUrl = me.data.realUrl;
            console.log("realUrl"+realUrl);
            realUrl = app.globalData.realUrl;
            console.log("publisherId" + app.globalData.publisherId);
            if (realUrl == ''||realUrl==null) {
              //跳转
              wx.switchTab({
                url: '../mine/mine',
              })
            }
            else {
              wx.redirectTo({
                url: realUrl
              })
            }
          } else {
            wx.showToast({
              title: res.data.msg,
              icon: 'none',
              duration: 3000
            })
          }
        },
        fail: function() {
          wx.hideLoading();
          wx.showToast({
            title: '登录失败，请确认后端8080已启动',
            icon: 'none',
            duration: 3000
          })
        }
      });
    }
  },
  getUserInfo: function (e) {
    var me = this;
    var rawData = JSON.parse(e.detail.rawData);
    var nickName = rawData.nickName;
    var avatarUrl = rawData.avatarUrl;
  },
  goRegist: function () {
    wx.navigateTo({
      url: '../userRegist/regist',
    })
  },
  onLoad: function (e) {
    console.log("result："+app.globalData.realUrl);
    var me = this;
    var realUrl = e.realUrl;
    console.log(realUrl);
    if (realUrl != null && realUrl != '' && realUrl != undefined) {
      realUrl = realUrl.replace('@', '=');
      realUrl = realUrl.replace('#', '?');
      //../videoinfo/videoinfo#videoInfo@[object Object]
      console.log("真实的路径" + realUrl);
      me.setData(
        {
          realUrl: realUrl
        }
      )
    }
    var user = app.getSaveUserInfo();
    var username = user.username;
    var password = user.password;
    if (username != null && username != undefined && username != '' && password != null && password != undefined
      && password != '') {
      me.setData(
        {
          username: username,
          password: password
        }
      )
    }

  }
})
