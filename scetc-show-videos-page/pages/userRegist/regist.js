const app = getApp()

Page({
  data: {

  },
  doRegist: function(e) {
    var formObject = e.detail.value;
    var username = formObject.username;
    var password = formObject.password;
    //简单验证
    if (username.length == 0 || password.length == 0) { //反馈数据
      wx.showToast({
        title: '用户名或者密码不能为空',
        icon: 'none', //图标
        duration: 3000
      })
    } else {
      wx.showLoading({
        title: '请等待...',
      })
      // 调用后端注册接口
      wx.request({
        url: app.getApiUrl('/regist'),
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
              title: '注册成功啦~~',
              icon: 'none',
              duration: 3000
            })
            // 保存用户信息到全局变量
            app.setGlobalUserInfo(res.data.data);
            // 保存用户信息到本地存储
            var saveUserInfo = {
              username: username,
              password: password
            };
            app.saveUserInfo(saveUserInfo);
            //页面跳转
            wx.navigateTo({
              url: '../userLogin/login',
            })
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
            title: '注册失败，请确认后端8080已启动',
            icon: 'none',
            duration: 3000
          })
        }
      });
    }
  },
  goLoginPage: function() {
    wx.navigateTo({
      url: '../userLogin/login',
    })
  }
})
