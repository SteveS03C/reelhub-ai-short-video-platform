const app = getApp()

Page({
  data: {
    faceUrl: "../resource/images/noneface.png",
    fansCounts: 0,
    followCounts: 0,
    videoList: [],
    receiveLikeCounts: 0,
    nickname: "",
    username: "",
    isMe: true,
    isFollowed: false,
    serverUrl: "",
    publisherId: "",
    targetUserId: "",
    activeTab: "publish",
    isLikedListPublic: true
  },

  onLoad: function () {
    this.refreshPageData();
  },

  onShow: function () {
    this.refreshPageData();
  },

  getCurrentUser: function () {
    return app.getGlobalUserInfo();
  },

  ensureLogin: function () {
    var user = this.getCurrentUser();
    if (!user) {
      wx.redirectTo({
        url: '../userLogin/login',
      });
      return null;
    }
    return user;
  },

  getStorageArray: function (key) {
    return wx.getStorageSync(key) || [];
  },

  countFansForUser: function (targetUserId) {
    var fanCount = 0;
    try {
      var keys = wx.getStorageInfoSync().keys || [];
      for (var i = 0; i < keys.length; i++) {
        if (keys[i].indexOf('followList_') === 0) {
          var followList = wx.getStorageSync(keys[i]) || [];
          for (var j = 0; j < followList.length; j++) {
            if (String(followList[j]) === String(targetUserId)) {
              fanCount++;
              break;
            }
          }
        }
      }
    } catch (e) {
      console.log('统计粉丝失败:', e);
    }
    return fanCount;
  },

  refreshPageData: function () {
    var user = this.ensureLogin();
    if (!user) {
      return;
    }

    var publisherId = app.globalData.publisherId;
    var isMe = !(publisherId && String(publisherId) !== String(user.id));
    var targetUserId = isMe ? user.id : publisherId;
    var targetNickname = isMe ? user.nickname : ('创作者 ' + targetUserId);
    var targetUsername = isMe ? user.username : ('用户ID：' + targetUserId);
    var faceUrl = wx.getStorageSync('userFaceUrl_' + targetUserId) || "../resource/images/noneface.png";
    var followList = this.getStorageArray('followList_' + user.id);
    var targetFollowList = this.getStorageArray('followList_' + targetUserId);
    var targetLikedList = this.getStorageArray('likedVideos_' + targetUserId);
    var isFollowed = false;
    var i = 0;

    for (i = 0; i < followList.length; i++) {
      if (String(followList[i]) === String(targetUserId)) {
        isFollowed = true;
        break;
      }
    }

    this.setData({
      faceUrl: faceUrl,
      fansCounts: this.countFansForUser(targetUserId),
      followCounts: targetFollowList.length,
      receiveLikeCounts: targetLikedList.length,
      nickname: targetNickname || "测试用户",
      username: targetUsername || "",
      isMe: isMe,
      isFollowed: isFollowed,
      publisherId: isMe ? "" : publisherId,
      targetUserId: targetUserId,
      serverUrl: app.serverUrl
    });

    this.initLikedListPublicStatus();
    this.refreshVideoList(this.data.activeTab);
    app.globalData.publisherId = null;
  },

  showVideoInfo: function (e) {
    var me = this;
    var videoList = me.data.videoList;
    var arrindex = e.currentTarget.dataset.arrindex;
    var videoInfo = JSON.stringify(videoList[arrindex]);//获取视频信息对象
    wx.navigateTo({
      url: '../videoInfo/videoInfo?videoInfo=' + videoInfo,
    })
  },

  followMe: function () {
    var user = app.getGlobalUserInfo();
    var publisherId = this.data.targetUserId;
    var me = this;
    
    // 从本地存储中获取用户的关注列表
    var followListKey = 'followList_' + user.id;
    var followList = wx.getStorageSync(followListKey) || [];
    
    // 检查是否已经关注
    var isFollowed = false;
    for (var i = 0; i < followList.length; i++) {
      if (followList[i] === publisherId) {
        isFollowed = true;
        break;
      }
    }
    
    // 如果没有关注，添加到关注列表
    if (!isFollowed) {
      followList.push(publisherId);
      wx.setStorageSync(followListKey, followList);
    }
    
    // 更新页面状态
    me.setData({
      isFollowed: true,
      fansCounts: Number(me.data.fansCounts || 0) + 1
    });
    
    // 显示操作成功提示
    wx.showToast({
      title: '关注成功',
      icon: 'success'
    });
  },
  UnfollowMe: function () {
    var user = app.getGlobalUserInfo();
    var publisherId = this.data.targetUserId;
    var me = this;
    
    // 从本地存储中获取用户的关注列表
    var followListKey = 'followList_' + user.id;
    var followList = wx.getStorageSync(followListKey) || [];
    
    // 从关注列表中移除
    followList = followList.filter(function(item) {
      return item !== publisherId;
    });
    wx.setStorageSync(followListKey, followList);
    
    // 更新页面状态
    me.setData({
      isFollowed: false,
      fansCounts: Math.max(0, Number(me.data.fansCounts || 0) - 1)
    });
    
    // 显示操作成功提示
    wx.showToast({
      title: '取消关注',
      icon: 'success'
    });
  },
  changeFace: function () {
    var isMe = this.data.isMe;
    if (!isMe) {
      return;
    }
    var that = this;
    wx.showLoading({
      title: '请等待...',
      icon: 'none'
    });
    wx.chooseImage({
      count: 1, // 默认9
      sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
      sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
      success: function (res) {
        // 返回选定照片的本地文件路径列表，tempFilePath可以作为img标签的src属性显示图片
        var tempFilePaths = res.tempFilePaths; //獲得一個數組 console 長度為1的數組
        var user = app.getGlobalUserInfo();
        // 保存头像到本地存储，按用户ID区分
        var userFaceUrlKey = 'userFaceUrl_' + user.id;
        var faceUrl = tempFilePaths[0];
        wx.setStorageSync(userFaceUrlKey, faceUrl);
        // 更新用户信息
        if (user) {
          user.faceImage = faceUrl;
          app.setGlobalUserInfo(user);
          wx.setStorageSync('userInfo', user);
        }
        wx.hideLoading();
        wx.showToast({
          title: '上传成功!~',
          icon: "success" //图标
        });
        that.setData({
          faceUrl: faceUrl
        });
      },
      fail: function () {
        wx.hideLoading();
      },
      complete: function () {
        wx.hideLoading();
      }
    });
  },
  //上传短视频js
  uploadVideo: function () {
    var that = this
    wx.chooseVideo({
      sourceType: ['album'],
      success: function (res) {
        var duration = res.duration;
        var tmpheight = res.height;
        var tmpwidth = res.width;
        var tmpVideoUrl = res.tempFilePath;
        var tmpCoverUrl = res.thumbTempFilePath;
        console.log(duration);
        if (duration > 30) {
          wx.showToast({
            title: '视频长度不能超过30秒',
            icon: "none",
            duration: 2500
          })
        }
        else if (duration < 2) {
          wx.showToast({
            title: '视频长度太短',
            icon: "icon",
            duration: 2500
          })
        }
        else {
          //打开选择bgm页面
          //上传视频选择验证通过之后 跳转到选择bgm页面 
          wx.navigateTo({
            url: '../chooseBgm/chooseBgm?duration='
              + duration + '&tmpHeight=' + tmpheight + '&tmpWidth=' + tmpwidth
              + '&tmpVideoUrl=' + tmpVideoUrl + '&tmpCoverUrl=' + tmpCoverUrl

          })

        }
      }
    })

  },
  //注销清空缓存
  logout: function (params) {
    wx.showLoading({
      title: '请等待...',
    })
    // 模拟注销成功
    setTimeout(function () {
      wx.hideLoading();
      // 清空用户信息
      app.userInfo = null;
      // 移除本地存储的用户信息
      wx.removeStorageSync("userInfo");
      // 跳转到登录页面
      wx.redirectTo({
        url: '../userLogin/login',
      })
    }, 1000); // 模拟1秒的处理时间
  },
  // 切换标签
  switchTab: function (e) {
    var tab = e.currentTarget.dataset.tab;
    this.setData({
      activeTab: tab
    });
    this.refreshVideoList(tab);
  },

  refreshVideoList: function (tab) {
    var targetUserId = this.data.targetUserId;
    var videoList = [];

    if (tab === 'publish') {
      videoList = this.getStorageArray('videoList_' + targetUserId);
      this.setData({
        videoList: videoList
      });
      return;
    }

    if (tab === 'liked') {
      var isLikedListPublic = wx.getStorageSync('isLikedListPublic_' + targetUserId);
      if (isLikedListPublic === undefined || isLikedListPublic === null) {
        isLikedListPublic = true;
      }

      if (this.data.isMe || isLikedListPublic) {
        videoList = this.getStorageArray('likedVideos_' + targetUserId);
        this.setData({
          isLikedListPublic: isLikedListPublic,
          videoList: videoList
        });
      } else {
        wx.showToast({
          title: '该用户的点赞列表不公开',
          icon: 'none'
        });
        this.setData({
          videoList: []
        });
      }
    }
  },
  // 设置点赞列表是否公开
  setLikedListPublic: function () {
    var me = this;
    var user = app.getGlobalUserInfo();
    var isLikedListPublicKey = 'isLikedListPublic_' + user.id;
    // 直接从本地存储中读取状态，不使用默认值
    var isLikedListPublic = wx.getStorageSync(isLikedListPublicKey);
    // 如果本地存储中没有值，才使用默认值
    if (isLikedListPublic === undefined || isLikedListPublic === null) {
      isLikedListPublic = true;
    }
    
    // 切换公开状态
    isLikedListPublic = !isLikedListPublic;
    wx.setStorageSync(isLikedListPublicKey, isLikedListPublic);
    
    // 更新页面状态
    me.setData({
      isLikedListPublic: isLikedListPublic
    });
    
    // 显示操作成功提示
    wx.showToast({
      title: isLikedListPublic ? '点赞列表已设置为公开' : '点赞列表已设置为私密',
      icon: 'success'
    });
  },
  
  // 初始化点赞列表公开状态
  initLikedListPublicStatus: function () {
    var me = this;
    var targetUserId = me.data.targetUserId;
    var isLikedListPublic = wx.getStorageSync('isLikedListPublic_' + targetUserId);
    if (isLikedListPublic === undefined || isLikedListPublic === null) {
      isLikedListPublic = true;
    }
    me.setData({
      isLikedListPublic: isLikedListPublic
    });
  },
  
  // 清除本地存储的用户信息数据
  clearUserInfo: function() {
    wx.showModal({
      title: '确认清除',
      content: '确定要清除所有用户信息数据吗？',
      success: function (res) {
        if (res.confirm) {
          // 清除本地存储的用户信息
          wx.removeStorageSync("userInfo");
          wx.removeStorageSync("saveUser");
          // 清除所有与用户相关的本地存储数据
          try {
            var keys = wx.getStorageInfoSync().keys;
            for (var i = 0; i < keys.length; i++) {
              if (keys[i].startsWith('userFaceUrl_') || 
                  keys[i].startsWith('videoList_') || 
                  keys[i].startsWith('likedVideos_') || 
                  keys[i].startsWith('followList_') ||
                  keys[i].startsWith('isLikedListPublic_')) {
                wx.removeStorageSync(keys[i]);
              }
            }
          } catch (e) {
            console.log('清除本地存储失败:', e);
          }
          // 显示清除成功的提示
          wx.showToast({
            title: '清除成功',
            icon: 'success'
          });
          // 跳转到登录页面
          setTimeout(function () {
            wx.redirectTo({
              url: '../userLogin/login',
            })
          }, 1000);
        }
      }
    });
  }



})
