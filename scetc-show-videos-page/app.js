//app.js
App({
  globalData:
  {
      //跳转的真实路径
      realUrl:null,
      //跳转的参数
      realUrlParam: null,
      //跳转到视频发布者的id
    publisherId:null,
  },
  // 本地后端 API 地址，部署时请按实际环境修改
  serverUrl: "http://127.0.0.1:8080",
  getApiUrl: function(path) {
    var base = this.serverUrl || "";
    if (base.endsWith("/")) {
      base = base.substring(0, base.length - 1);
    }
    if (!path.startsWith("/")) {
      path = "/" + path;
    }
    return base + path;
  },
  userInfo: null,
  //switchTab 不能携带参数，只好将参数作为全局变量来进行保存
  //内网ip的方式访问
  setGlobalUserInfo: function (user) {
    wx.setStorageSync("userInfo", user);
  },
  getGlobalUserInfo: function () {
    return wx.getStorageSync("userInfo");
  },
  saveUserInfo: function (saveUser) {
    wx.setStorageSync("saveUser", saveUser);
  },
  getSaveUserInfo: function () {
    return wx.getStorageSync("saveUser");
  }
})
