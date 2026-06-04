var app = getApp()

Page({
  data: {
    categoryInfo: [],
    featureTags: [],
    serverUrl: ''
  },
  //生命周期函数--监听页面加载
  onLoad: function () {
    var me = this;
    var serverUrl = app.serverUrl;
    var categoryInfo = [
      {
        id: 1,
        name: "光影现场",
        label: "define",
        content: "记录舞台、派对和夜色下的高光时刻",
        imageUrl: "/pages/resource/images/dsp.jpg",
        badge: "主推",
        stats: "2.3w 人正在看",
        accent: "linear-gradient(135deg, #5b86ff 0%, #7f63ff 100%)"
      },
      {
        id: 2,
        name: "治愈料理",
        label: "food",
        content: "把日常烟火做成值得回味的短视频",
        imageUrl: "/pages/resource/images/food.jpg",
        badge: "热门",
        stats: "1.6w 人收藏",
        accent: "linear-gradient(135deg, #ff8a65 0%, #ff7043 100%)"
      },
      {
        id: 3,
        name: "潮流穿搭",
        label: "dress",
        content: "穿搭灵感、妆容推荐和生活方式合集",
        imageUrl: "/pages/resource/images/dress.jpg",
        badge: "上新",
        stats: "敬请期待",
        accent: "linear-gradient(135deg, #ff7eb3 0%, #ff758c 100%)"
      },
      {
        id: 4,
        name: "城市漫游",
        label: "city",
        content: "旅行日记、街头记录和更多灵感主题",
        imageUrl: "/pages/resource/images/tm.jpg",
        badge: "探索",
        stats: "1.1w 人在浏览",
        accent: "linear-gradient(135deg, #3ec7a6 0%, #00bfa5 100%)"
      }
    ];

    me.setData({
      serverUrl: serverUrl,
      categoryInfo: categoryInfo,
      featureTags: ["精选专题", "高颜值封面", "沉浸式浏览"]
    });
  },
  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
  },
  // 每条List点击事件
  jump: function (e) {
    var id = e.currentTarget.dataset.id;
    if (id === 'define' || id === 'food' || id === 'dress' || id === 'city') {
      wx.navigateTo({
        url: '../index/index?id=' + id,
      })
    }
  },


  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },



  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})
