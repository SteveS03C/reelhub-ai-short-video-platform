var videoUtils = require('../../utils/videoUtils.js')
var util = require("../../utils/formateTime.js");

const app = getApp()


Page({
  /**
   * 页面的初始数据
   */
  data: {
    actionSheetHidden: true,
    cover: "cover",
    videoId: "",
    src: "",
    imagesrc: "",
    videoInfo: {},
    userLikeVideo: false,
    placeholder: "说点什么吧",
    CommentList: [],
    contentValue: "",
    showComment: false,
    isVideoCreator: false, // 是否是视频作者
    showAiAssistant: false,
    aiQuestion: "",
    aiAnswer: "",
    aiLoading: false,
    aiExamples: [
      "这个视频讲了什么？",
      "总结一下这个视频",
      "这个视频的重点是什么？"
    ],
    itemList: [{
      name: "下载视频"
    }, {
      name: '举报用户'
    }],
  },
  videoCtx: {},

  /**
   * 生命周期函数--监听页面加载
   */
  // listenerButton: function () {
  //   this.setData({
  //     actionSheetHidden: !this.data.actionSheetHidden
  //   });
  // },
  // listenerActionSheet: function () {

  //   this.setData({
  //     actionSheetHidden: !this.data.actionSheetHidden,
  //   })
  // },
  // operate: function (params) {

  //   var me = this;
  //   //先判断用户是否 登陆如果没有登陆 则提醒用户进行登陆
  //   var user = app.getGlobalUserInfo();
  //   var videoInfo = me.data.videoInfo;
  //   var videoId = videoInfo.id;
  //   var videoInfo = JSON.stringify(me.data.videoInfo);
  //   var realUrl = '../videoInfo/videoInfo#videoInfo@' + videoInfo;
  //   var url = '/video/userLike?userId=' + user.id + '&videoId=' + videoId + '&videoCreaterId=' + videoInfo.userId;
  //   if (user == null || user == '' || user == undefined) {
  //     wx.redirectTo({
  //       url: '../userLogin/login?realUrl=' + realUrl,
  //     })
  //   } else {
  //     var value = params;
  //     var videoInfo = this.data.videoInfo;
  //     var id = params.currentTarget.id;
  //      console.log("下载视频");
  //     if (id == 0) //下载视频
  //     {  
  //       // consol.log(videoInfo);
  //       // console.log('下载视频');
  //       // console.log(app.serverUrl + me.data.videoInfo.videoPath);
  //       // wx.showLoading({
  //       //   title: '下载中...',
  //       // })
  //       // wx.downloadFile({

  //       //   url: app.serverUrl + me.data.videoInfo.videoPath,
  //       //   success: function (res) {
  //       //     // 只要服务器有响应数据，就会把响应内容写入文件并进入 success 回调，业务需要自行判断是否下载到了想要的内容
  //       //     if (res.statusCode === 200) {

  //       //       wx.saveVideoToPhotosAlbum({
  //       //         filePath: res.tempFilePath,
  //       //         success: function (res) {
  //       //           wx.hideLoading();
  //       //         }
  //       //       })
  //       //     }
  //       //   }
  //       // })
  //     } else if (id == 1) //举报用户
  //     {
  //       wx.redirectTo({
  //         url: '../report/report?videoId=' + videoInfo.id + "&pubulisherId=" + videoInfo.userId,
  //       })

  //     }
  //   }
  // },
  onLoad: function (params) {
    var me = this;
    me.videoCtx = wx.createVideoContext('myVideo', me);
    var videoInfo;
    var realUrlParam = app.globalData.realUrlParam;
    if (realUrlParam != '' && realUrlParam != null && realUrlParam != undefined) {
      videoInfo = JSON.parse(realUrlParam);
      app.globalData.realUrl = null;
      app.globalData.realUrlParam = null;
    } else {
      videoInfo = JSON.parse(params.videoInfo);
    }
    //解决高和宽的问题
    //横屏和竖屏的问题。
    var height = videoInfo.videoHeight;
    var width = videoInfo.videoWidth;
    var cover = 'cover';
    if (width > height) {
      cover = '';
    }
    // 检查当前用户是否是视频的发布者
    var user = app.getGlobalUserInfo();
    var imagesrc = videoInfo.face_image || "../resource/images/noneface.png";
    // 如果当前用户是视频的发布者，使用本地存储的头像，按用户ID区分
    if (user && user.id === videoInfo.userId) {
      var userFaceUrlKey = 'userFaceUrl_' + user.id;
      var userFaceUrl = wx.getStorageSync(userFaceUrlKey);
      if (userFaceUrl) {
        imagesrc = userFaceUrl;
      }
    }
    var videoId = videoInfo.id;
    me.setData({
      videoId: videoId,
      src: videoInfo.videoPath, // 使用视频的本地路径
      imagesrc: imagesrc, // 使用视频发布者的头像或默认头像
      videoInfo: videoInfo,
      cover: cover,
    })
    var serverUrl = app.serverUrl;
    var loginUserId = '';
    if (user != null && user != undefined && user != '') {
      loginUserId = user.id;
    }
    // 检查用户是否已经点赞过该视频
    var userLikeVideo = false;
    var isVideoCreator = false;
    if (user) {
      // 检查是否是视频作者
      if (user.id === videoInfo.userId) {
        isVideoCreator = true;
      }
      // 检查是否已经点赞过该视频
      var likedVideosKey = 'likedVideos_' + user.id;
      var likedVideos = wx.getStorageSync(likedVideosKey) || [];
      for (var i = 0; i < likedVideos.length; i++) {
        if (likedVideos[i].id === videoId) {
          userLikeVideo = true;
          break;
        }
      }
    }
    me.setData({
      userLikeVideo: userLikeVideo,
      isVideoCreator: isVideoCreator,
    });
    // 从本地存储中获取评论列表
    var commentsKey = 'comments_' + videoInfo.id;
    var CommentList = wx.getStorageSync(commentsKey) || [];
    me.setData({
      CommentList: CommentList
    });
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },
  sendComment: function (e) {

    var me = this;
    var comment = e.detail.value;
    var user = app.getGlobalUserInfo();
    var videoInfo = me.data.videoInfo;
    var videoId = videoInfo.id;
    if (user == null || user == '' || user == undefined) {
      var videoInfo = JSON.stringify(me.data.videoInfo);
      var realUrl = '../videoInfo/videoInfo#videoInfo@' + videoInfo;
      app.globalData.realUrl = '../videoInfo/videoInfo';
      app.globalData.realUrlParam = videoInfo;
      wx.redirectTo({
        url: '../userLogin/login?realUrl=' + realUrl,
      })
    } else {
      // 创建新评论
      var newComment = {
        id: Date.now(),
        videoId: videoId,
        userId: user.id,
        comment: comment,
        createTime: util.formatDate(new Date()),
        face_image: user.faceImage || "../resource/images/noneface.png",
        nickName: user.nickname || "用户"
      };
      
      // 从本地存储中获取已有的评论列表
      var commentsKey = 'comments_' + videoId;
      var CommentList = wx.getStorageSync(commentsKey) || [];
      
      // 添加新评论到列表
      CommentList.unshift(newComment);
      
      // 保存回本地存储
      wx.setStorageSync(commentsKey, CommentList);
      
      // 更新页面数据
      me.setData({
        contentValue: '', //清空输入框
        CommentList: CommentList
      });
      
      wx.showToast({
        title: '发送成功',
        icon: 'success'
      });
    }

  },
  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    var me = this;
    me.videoCtx.play();

  },
  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {
    var me = this;
    me.videoCtx.pause();
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

  showSearch: function () {
    wx.switchTab({
      url: '../searchVideos/searchVideos',
      success: function (res) { },
      fail: function (res) { },
      complete: function (res) { },
    })

  },
  //点击上传自己的短视频
  upload: function () {
    var me = this;
    var videoInfo = JSON.stringify(me.data.videoInfo);
    var realUrl = '../videoInfo/videoInfo@videoInfo#' + videoInfo; //视频的信息
    app.globalData.realUrl = '../videoInfo/videoInfo';
    app.globalData.realUrlParam = videoInfo;
    var user = app.getGlobalUserInfo();
    if (user == null || user == '' || user == undefined) {
      wx.redirectTo({
        url: '../userLogin/login?realUrl=' + realUrl,
      })
    } else {
      videoUtils.uploadVideos();
    }
  },
  showIndex: function () {
    wx.switchTab({
      url: '../category/category',
    })
  },
  showPublisher: function () {
    var user = app.getGlobalUserInfo();
    var videoInfo = this.data.videoInfo;
    if (user == null || user == undefined || user == '') {
      var realUrl = '../videoInfo/publisherId@publisherId#' + videoInfo.userId; //视频的信息
      app.globalData.publisherId = videoInfo.userId;
      wx.redirectTo({
        url: '../userLogin/login?realUrl=' + realUrl,
      })
    } else {
      // 设置publisherId为视频发布者的userId，这样就会显示视频发布者的信息
      app.globalData.publisherId = videoInfo.userId;
      wx.switchTab({
        url: '../mine/mine'
      })
    }
    //对页面级别进行拦截

  },
  // 切换评论区显示/隐藏
  toggleComment: function () {
    var me = this;
    var user = app.getGlobalUserInfo();
    if (user == null || user == '' || user == undefined) {
      var videoInfo = JSON.stringify(me.data.videoInfo);
      var realUrl = '../videoInfo/videoInfo#videoInfo@' + videoInfo;
      app.globalData.realUrl = '../videoInfo/videoInfo';
      app.globalData.realUrlParam = videoInfo;
      wx.redirectTo({
        url: '../userLogin/login?realUrl=' + realUrl,
      })
    } else {
      me.setData({
        showComment: !me.data.showComment
      });
    }
  },
  // 旧的评论方法，保留但不再使用
  comments: function () {
    var me = this;
    me.toggleComment();
  },
  toggleAiAssistant: function () {
    this.setData({
      showAiAssistant: !this.data.showAiAssistant
    });
  },
  onAiQuestionInput: function (e) {
    this.setData({
      aiQuestion: e.detail.value || ""
    });
  },
  fillAiQuestion: function (e) {
    this.setData({
      aiQuestion: e.currentTarget.dataset.question || ""
    });
  },
  goReport: function () {
    var me = this;
    var user = app.getGlobalUserInfo();
    var videoInfo = me.data.videoInfo || {};
    var videoInfoStr = JSON.stringify(videoInfo);
    var realUrl = '../videoInfo/videoInfo#videoInfo@' + videoInfoStr;
    app.globalData.realUrl = '../videoInfo/videoInfo';
    app.globalData.realUrlParam = videoInfoStr;

    if (user == null || user == '' || user == undefined) {
      wx.redirectTo({
        url: '../userLogin/login?realUrl=' + realUrl,
      })
      return;
    }

    wx.navigateTo({
      url: '../report/report?videoId=' + videoInfo.id + "&pubulisherId=" + videoInfo.userId,
    })
  },
  askAiAssistant: function () {
    var me = this;
    if (me.data.aiLoading) {
      return;
    }

    var question = (me.data.aiQuestion || "").trim();
    if (!question) {
      wx.showToast({
        title: '请输入问题',
        icon: 'none'
      });
      return;
    }

    var src = me.data.src || "";
    if (!src) {
      wx.showToast({
        title: '未找到视频源',
        icon: 'none'
      });
      return;
    }

    me.setData({
      aiLoading: true
    });
    wx.showLoading({
      title: 'AI思考中...'
    });

    var videoInfo = me.data.videoInfo || {};
    var serverUrl = app.serverUrl || "";
    var relativeVideoPath = "";
    if (serverUrl && src.indexOf(serverUrl) === 0) {
      relativeVideoPath = src.substring(serverUrl.length);
    } else if (src.indexOf("/") === 0) {
      relativeVideoPath = src;
    }

    var requestData = {
      question: question,
      videoDesc: videoInfo.videoDesc || "",
      videoSeconds: videoInfo.videoSeconds || 0
    };

    var finishRequest = function () {
      wx.hideLoading();
      me.setData({
        aiLoading: false
      });
    };

    var handleSuccess = function (raw) {
      finishRequest();
      var res = raw;
      if (typeof raw === "string") {
        try {
          res = JSON.parse(raw);
        } catch (e) {
          res = null;
        }
      }
      if (!res || res.status !== 200 || !res.data || !res.data.answer) {
        wx.showModal({
          title: 'AI助手',
          content: res && res.msg ? res.msg : 'AI助手暂时无法回答，请稍后重试',
          showCancel: false
        });
        return;
      }
      me.setData({
        aiAnswer: res.data.answer,
        showAiAssistant: true
      });
    };

    var handleError = function (message) {
      finishRequest();
      wx.showModal({
        title: 'AI助手',
        content: message || 'AI问答请求失败，请稍后重试',
        showCancel: false
      });
    };

    if (relativeVideoPath) {
      requestData.videoPath = relativeVideoPath;
      wx.request({
        url: app.getApiUrl('/ai/video/qaByPath'),
        method: 'POST',
        header: {
          'content-type': 'application/x-www-form-urlencoded'
        },
        data: requestData,
        success: function (res) {
          if (res.statusCode !== 200) {
            handleError('AI服务响应异常: ' + res.statusCode);
            return;
          }
          handleSuccess(res.data);
        },
        fail: function () {
          handleError('AI服务连接失败，请检查 8080 接口是否启动');
        }
      });
      return;
    }

    wx.uploadFile({
      url: app.getApiUrl('/ai/video/qaUpload'),
      filePath: src,
      name: 'file',
      formData: requestData,
      success: function (res) {
        if (res.statusCode !== 200) {
          handleError('AI服务响应异常: ' + res.statusCode);
          return;
        }
        handleSuccess(res.data);
      },
      fail: function () {
        handleError('视频上传给 AI 失败，请稍后重试');
      }
    });
  },
  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function (res) {

    var me = this;
    var videoInfo = me.data.videoInfo;
    return {
      title: '快来看看ReelHub吧',
      path: '/pages/videoInfo/videoInfo?videoInfo=' +
        JSON.stringify(videoInfo),
    }
  },
  shareMe: function () {

    var me = this;
    //先判断用户是否 登陆如果没有登陆 则提醒用户进行登陆
    var user = app.getGlobalUserInfo();
    var videoInfo = me.data.videoInfo;
    var videoId = videoInfo.id;
    var videoInfo = JSON.stringify(me.data.videoInfo);
    var realUrl = '../videoInfo/videoInfo#videoInfo@' + videoInfo;
    app.globalData.realUrl = '../videoInfo/videoInfo';
    app.globalData.realUrlParam = videoInfo;
    var url = '/video/userLike?userId=' + user.id + '&videoId=' + videoId + '&videoCreaterId=' + videoInfo.userId;
    if (user == null || user == '' || user == undefined) {
      wx.redirectTo({
        url: '../userLogin/login?realUrl=' + realUrl,
      })
    } else {
      var videoInfo = this.data.videoInfo;
      var me = this;
      var user = app.getGlobalUserInfo();
      wx.getSystemInfo({
        success: function (result) {
          //选项集合
          let itemList;
          if (result.platform == 'android') {
            itemList = ['下载到本地', '举报用户', '分享到微信群', '取消']
          } else {
            itemList = ['下载到本地', '举报用户', '分享到微信群',]
          }
          wx.showActionSheet({
            itemList: itemList,
            success: function (res) {
              if (res.tapIndex == 0) {
                const downloadTask = wx.downloadFile({
                  url: app.serverUrl + me.data.videoInfo.videoPath,
                  header: ' Content-Type ',
                  success: function (res) {
                    // 只要服务器有响应数据，就会把响应内容写入文件并进入 success 回调，业务需要自行判断是否下载到了想要的内容
                    console.log(res);
                    if (res.statusCode === 200) {
                      wx.saveVideoToPhotosAlbum({
                        filePath: res.tempFilePath,
                        success: function (res) {
                          wx.hideLoading();
                        }
                      })
                    }
                  }
                })
                downloadTask.onProgressUpdate((res) => {
                  console.log('下载进度', res.progress)
                  wx.showLoading({
                    title: '下载进度:' + res.progress,
                  })
                  console.log('已经下载的数据长度', res.totalBytesWritten)
                  console.log('预期需要下载的数据总长度', res.totalBytesExpectedToWrite)
                })

              } else if (res.tapIndex == 1) {
                // 举报
                if (user.id)
                  wx.redirectTo({
                    url: '../report/report?videoId=' + videoInfo.id + "&pubulisherId=" + videoInfo.userId,
                  })
              } else if (res.tapIndex == 2) {
                wx.showToast({
                  title: '使用右上角转发',
                  duration: 5000
                })
              }

            },
          })
        },
      })
    }
  },

  likeVideoOrNot: function () {
    var me = this;
    //先判断用户是否 登陆如果没有登陆 则提醒用户进行登陆
    var user = app.getGlobalUserInfo();
    var videoInfo = me.data.videoInfo;
    var videoId = videoInfo.id;
 
    if (user == null || user == '' || user == undefined) {
      var videoInfo = JSON.stringify(me.data.videoInfo);
      var realUrl = '../videoInfo/videoInfo#videoInfo@' + videoInfo;
      app.globalData.realUrl = '../videoInfo/videoInfo';
      app.globalData.realUrlParam = videoInfo;
      wx.redirectTo({ 
        url: '../userLogin/login?realUrl=' + realUrl, 
      })
    } else {
      var userLikeVideo = me.data.userLikeVideo;
      // 从本地存储中获取用户点赞的视频列表
      var likedVideosKey = 'likedVideos_' + user.id;
      var likedVideos = wx.getStorageSync(likedVideosKey) || [];
      
      if (userLikeVideo) {
        // 取消点赞，从列表中移除
        likedVideos = likedVideos.filter(function(item) {
          return item.id !== videoId;
        });
      } else {
        // 检查视频是否已经在列表中
        var isVideoInList = false;
        for (var i = 0; i < likedVideos.length; i++) {
          if (likedVideos[i].id === videoId) {
            isVideoInList = true;
            break;
          }
        }
        // 只有当视频不在列表中时，才添加到列表中
        if (!isVideoInList) {
          likedVideos.push(videoInfo);
        }
      }
      
      // 保存回本地存储
      wx.setStorageSync(likedVideosKey, likedVideos);
      
      // 更新页面状态
      userLikeVideo = !userLikeVideo;
      me.setData({
        userLikeVideo: userLikeVideo,
      });
      
      // 显示操作成功提示
      wx.showToast({
        title: userLikeVideo ? '点赞成功' : '取消点赞',
        icon: 'success'
      });
    }
  },
  // 删除视频
  deleteVideo: function () {
    var me = this;
    var videoInfo = me.data.videoInfo;
    var videoId = videoInfo.id;
    var userId = videoInfo.userId;
    
    // 显示确认对话框
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这个视频吗？',
      success: function (res) {
        if (res.confirm) {
          // 从本地存储中删除视频
          var userVideoListKey = 'videoList_' + userId;
          var videoList = wx.getStorageSync(userVideoListKey) || [];
          videoList = videoList.filter(function(item) {
            return item.id !== videoId;
          });
          wx.setStorageSync(userVideoListKey, videoList);
          
          // 从所有用户的点赞列表中删除该视频
          try {
            // 获取本地存储中的所有键
            var keys = wx.getStorageInfoSync().keys;
            // 遍历所有键，找到所有likedVideos_开头的键
            for (var i = 0; i < keys.length; i++) {
              if (keys[i].startsWith('likedVideos_')) {
                var likedVideos = wx.getStorageSync(keys[i]) || [];
                likedVideos = likedVideos.filter(function(item) {
                  return item.id !== videoId;
                });
                wx.setStorageSync(keys[i], likedVideos);
              }
            }
          } catch (e) {
            console.log('删除点赞列表失败:', e);
          }
          
          // 从本地存储中删除该视频的评论
          var commentsKey = 'comments_' + videoId;
          wx.removeStorageSync(commentsKey);
          
          // 显示删除成功的提示
          wx.showToast({
            title: '删除成功',
            icon: 'success'
          });
          
          // 跳转到mine页面
          setTimeout(function () {
            wx.switchTab({
              url: '../mine/mine',
            })
          }, 1000);
        }
      }
    });
  }
})
