const app = getApp()

Page({
  data: {
    totalPage: 1,
    page: 1,
    videoList: [],
    serverUrl: "",
    screenWidth: 350,
    searchContent: "",
    category: ""
  },
  onLoad: function (params) {
    var me = this;
    var screenWidth = wx.getSystemInfoSync().screenWidth;
    var category = params.id;
    if (category != null && category != '' && category != undefined) {
      me.setData({
        category: category,
      })
    }
    me.setData({
      screenWidth: screenWidth,
    })
    var searchContent = params.search;
    var isSaveRecord = params.isSaveRecord;
    if (isSaveRecord == null || isSaveRecord == '') {
      isSaveRecord = 0;
    }
    if (searchContent != undefined) {
      me.setData(
        {
          searchContent: searchContent
        }
      );
    }
    var page = me.data.page;
    me.getAllVideoList(page, isSaveRecord);
  },
  getAllVideoList: function (page, isSaveRecord) {
    var me = this;
    wx.showLoading({
      title: '请等待...加载中',
    })
    var searchContent = me.data.searchContent;
    var category = me.data.category;
    // 从本地存储中获取所有用户的视频列表
    var allVideoList = [];
    try {
      // 获取本地存储中的所有键
      var keys = wx.getStorageInfoSync().keys;
      // 遍历所有键，找到所有videoList_开头的键
      for (var i = 0; i < keys.length; i++) {
        if (keys[i].startsWith('videoList_')) {
          var userVideos = wx.getStorageSync(keys[i]) || [];
          allVideoList = allVideoList.concat(userVideos);
        }
      }
    } catch (e) {
      console.log('获取本地存储失败:', e);
    }
    // 过滤视频列表
    var filteredVideoList = allVideoList;
    // 如果有搜索内容，进行搜索过滤
    if (searchContent) {
      filteredVideoList = allVideoList.filter(function(video) {
        return video.videoDesc && video.videoDesc.indexOf(searchContent) !== -1;
      });
    }
    // 如果有分类，严格按分类过滤；不再把 define 当成“全部”。
    if (category) {
      filteredVideoList = filteredVideoList.filter(function(video) {
        return video.videoCategory === category;
      });
    }
    wx.hideLoading();
    wx.hideNavigationBarLoading();
    wx.stopPullDownRefresh();
    // 判断当前是否是第一页
    if (page == 1) {
      me.setData(
        {
          videoList: []
        }
      );
    }
    // 遍历视频信息，确保图片路径正确
    for (var i = 0; i < filteredVideoList.length; i++) {
      // 确保头像路径正确
      if (!filteredVideoList[i].face_image) {
        filteredVideoList[i].face_image = "../resource/images/noneface.png";
      }
      // 确保封面图路径正确
      if (!filteredVideoList[i].coverPath) {
        filteredVideoList[i].coverPath = "../resource/images/video.png";
      }
    }
    var newVideoList = me.data.videoList;
    me.setData(
      {
        videoList: newVideoList.concat(filteredVideoList),
        page: page,
        totalPage: 1, // 本地存储只有一页
        serverUrl: app.serverUrl
      }
    );
  },
  onReachBottom: function () {
    var me = this;
    var currentPage = me.data.page;
    var totalPage = me.data.totalPage;
    if (currentPage == totalPage) {
      wx.showToast({
        title: '已经没有视频啦',
        icon: "none"

      })
      return;
    }
    var page = currentPage + 1;
    me.getAllVideoList(page, 0);

  },
  onPullDownRefresh: function () {
    wx.showNavigationBarLoading();
    this.getAllVideoList(1, 0);
  },
  showVideoInfo: function (e) {
    var me = this;
    var videoList = me.data.videoList;
    var arrindex = e.target.dataset.arrindex;
    var videoInfo = JSON.stringify(videoList[arrindex]);//获取视频信息对象
    wx.navigateTo({
      url: '../videoInfo/videoInfo?videoInfo=' + videoInfo,
    })
  }

})
