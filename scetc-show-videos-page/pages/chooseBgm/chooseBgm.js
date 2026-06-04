// pages/chooseBgm/chooseBgm.js
const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    serverUrl: "",
    bgmList: [],
    videoParam: {},
    selectedBgmId: "",
    playingBgmId: "",
    innerAudioContext: null,
    items: [
      { name: 'define', value: '光影现场', checked: true },
      { name: 'food', value: '治愈料理' },
      { name: 'dress', value: '潮流穿搭' },
      { name: 'city', value: '城市漫游' }
    ],
    boxValue: "define",
    isUpload: false,
    isUploadingBgm: false
  },

  getCategoryOptions: function (selectedName) {
    var current = selectedName || "define";
    return [
      { name: 'define', value: '光影现场', checked: current === 'define' },
      { name: 'food', value: '治愈料理', checked: current === 'food' },
      { name: 'dress', value: '潮流穿搭', checked: current === 'dress' },
      { name: 'city', value: '城市漫游', checked: current === 'city' }
    ];
  },

  formatSeconds: function (seconds) {
    var total = Math.max(0, parseInt(seconds || 0, 10));
    var mins = Math.floor(total / 60);
    var secs = total % 60;
    return (mins < 10 ? '0' + mins : '' + mins) + ':' + (secs < 10 ? '0' + secs : '' + secs);
  },

  ensureAudioContext: function () {
    if (this.data.innerAudioContext) {
      return this.data.innerAudioContext;
    }
    var ctx = wx.createInnerAudioContext();
    var me = this;
    ctx.onEnded(function () {
      me.setData({
        playingBgmId: ""
      });
    });
    ctx.onStop(function () {
      me.setData({
        playingBgmId: ""
      });
    });
    this.setData({
      innerAudioContext: ctx
    });
    return ctx;
  },

  updateBgmDuration: function (bgmId, seconds) {
    var list = this.data.bgmList || [];
    for (var i = 0; i < list.length; i++) {
      if (String(list[i].id) === String(bgmId)) {
        list[i].durationText = this.formatSeconds(seconds);
        break;
      }
    }
    this.setData({
      bgmList: list
    });
  },

  resolveBgmDuration: function (item) {
    var me = this;
    var src = (this.data.serverUrl || '').replace(/\/$/, '') + (item.path || '');
    if (!src) {
      return;
    }
    var ctx = wx.createInnerAudioContext();
    var finished = false;
    var cleanUp = function () {
      if (finished) {
        return;
      }
      finished = true;
      try {
        ctx.stop();
        ctx.destroy();
      } catch (e) {}
    };
    var timer = setTimeout(function () {
      var seconds = parseInt(ctx.duration || 0, 10);
      if (seconds > 0) {
        me.updateBgmDuration(item.id, seconds);
      }
      cleanUp();
    }, 1500);
    ctx.src = src;
    ctx.autoplay = false;
    ctx.onCanplay(function () {
      setTimeout(function () {
        var seconds = parseInt(ctx.duration || 0, 10);
        if (seconds > 0) {
          me.updateBgmDuration(item.id, seconds);
        }
        clearTimeout(timer);
        cleanUp();
      }, 300);
    });
    ctx.onError(function () {
      clearTimeout(timer);
      cleanUp();
    });
  },

  loadBgmList: function () {
    var me = this;
    wx.request({
      url: app.getApiUrl('/bgm/list'),
      method: 'post',
      success: function (res) {
        if (res.data.status == 200) {
          var list = res.data.data || [];
          var normalizedList = [];
          for (var i = 0; i < list.length; i++) {
            var item = list[i] || {};
            var estimatedSeconds = 30;
            item.durationText = me.formatSeconds(estimatedSeconds);
            item.checked = false;
            normalizedList.push(item);
          }
          me.setData({
            bgmList: normalizedList,
            serverUrl: (app.serverUrl || '').replace(/\/$/, '')
          });
          for (var j = 0; j < normalizedList.length; j++) {
            me.resolveBgmDuration(normalizedList[j]);
          }
        } else {
          wx.showToast({
            title: 'BGM列表加载失败',
            icon: 'none'
          });
        }
      },
      fail: function () {
        wx.showToast({
          title: 'BGM列表加载失败',
          icon: 'none'
        });
      }
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (params) {
    var me = this;
    console.log('页面加载参数:', params);
    me.setData(
      {
        videoParam: params,
        isUpload:false
      }
    )
    wx.showToast({
      title: '请等待....',
    })
    me.loadBgmList();
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {

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
    if (this.data.innerAudioContext) {
      this.data.innerAudioContext.destroy();
    }
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

  },

  categoryChange: function (e) {
    var selected = e.detail.value || 'define';
    this.setData({
      items: this.getCategoryOptions(selected),
      boxValue: selected
    });
  },

  toggleBgm: function (e) {
    var bgmId = e.currentTarget.dataset.id || '';
    var bgmPath = e.currentTarget.dataset.path || '';
    var list = this.data.bgmList || [];
    var selectedBgmId = this.data.selectedBgmId;
    var playingBgmId = this.data.playingBgmId;
    var ctx = this.ensureAudioContext();
    var nextSelectedId = selectedBgmId === bgmId ? '' : bgmId;
    var nextPlayingId = playingBgmId;

    if (playingBgmId === bgmId) {
      ctx.stop();
      nextPlayingId = '';
    } else {
      ctx.stop();
      ctx.src = this.data.serverUrl + bgmPath;
      ctx.play();
      nextPlayingId = bgmId;
    }

    for (var i = 0; i < list.length; i++) {
      list[i].checked = String(list[i].id) === String(nextSelectedId);
    }

    this.setData({
      bgmList: list,
      selectedBgmId: nextSelectedId,
      playingBgmId: nextPlayingId
    });
  },

  clearSelectedBgm: function () {
    var list = this.data.bgmList || [];
    for (var i = 0; i < list.length; i++) {
      list[i].checked = false;
    }
    var ctx = this.data.innerAudioContext;
    if (ctx) {
      ctx.stop();
    }
    this.setData({
      bgmList: list,
      selectedBgmId: "",
      playingBgmId: ""
    });
  },

  resetFormState: function () {
    this.clearSelectedBgm();
    this.setData({
      items: this.getCategoryOptions("define"),
      boxValue: "define"
    });
  },

  chooseUserBgm: function () {
    var me = this;
    var userInfo = app.getGlobalUserInfo();
    if (!userInfo || !userInfo.id) {
      wx.showToast({
        title: '请先登录后再上传BGM',
        icon: 'none'
      });
      return;
    }
    if (me.data.isUploadingBgm) {
      return;
    }

    wx.chooseMessageFile({
      count: 1,
      type: 'file',
      extension: ['mp3'],
      success: function (chooseRes) {
        var tempFile = chooseRes.tempFiles && chooseRes.tempFiles[0];
        if (!tempFile) {
          return;
        }
        me.setData({
          isUploadingBgm: true
        });
        wx.showLoading({
          title: '上传BGM中...'
        });
        wx.uploadFile({
          url: app.getApiUrl('/bgm/upload'),
          filePath: tempFile.path,
          name: 'file',
          formData: {
            userId: userInfo.id
          },
          success: function (res) {
            try {
              var data = JSON.parse(res.data);
              if (data.status == 200) {
                wx.showToast({
                  title: 'BGM上传成功',
                  icon: 'success'
                });
                me.loadBgmList();
              } else {
                wx.showToast({
                  title: data.msg || 'BGM上传失败',
                  icon: 'none'
                });
              }
            } catch (err) {
              wx.showToast({
                title: 'BGM上传失败',
                icon: 'none'
              });
            }
          },
          fail: function () {
            wx.showToast({
              title: 'BGM上传失败',
              icon: 'none'
            });
          },
          complete: function () {
            wx.hideLoading();
            me.setData({
              isUploadingBgm: false
            });
          }
        });
      }
    });
  },

  upload: function (e) {
    wx.showLoading({
      title: '上传中...',
    })
    var me = this;
    var bgmId = me.data.selectedBgmId || '';
    var desc = e.detail.value.desc;
    var duration = me.data.videoParam.duration;
    var tmpHeight = me.data.videoParam.tmpHeight;
    var tmpWidth = me.data.videoParam.tmpWidth;
    var tmpVideoUrl = me.data.videoParam.tmpVideoUrl;
    var tmpCoverUrl = me.data.videoParam.tmpCoverUrl;
    var videoCategory = me.data.boxValue;
    // 移除了滤镜参数
    var videoFilter = "define";
    
    console.log('上传参数:', {
      bgmId: bgmId,
      desc: desc,
      duration: duration,
      tmpHeight: tmpHeight,
      tmpWidth: tmpWidth,
      tmpVideoUrl: tmpVideoUrl,
      tmpCoverUrl: tmpCoverUrl,
      videoCategory: videoCategory,
      videoFilter: videoFilter
    });
    
    //如果用户没有点击任何的选项卡的时候 则不允许用户进行上传
    if (videoCategory == '') {
      wx.showToast({
        title: '请选择一个视频分类',
        icon: 'none'
      })
      return;
    }
    
    //判断上传操作是否已经提交 ，如果在当前页面已经被提交就不能上传了
    if (me.data.isUpload==true) {
      wx.showToast({
        title: '正在进行上传,请不要反复提交上传',
      })
      return;
    } else {
      me.setData({
        isUpload: true
      })
    }
    
    var userInfo = app.getGlobalUserInfo();
    if (!userInfo || !userInfo.id) {
      wx.hideLoading();
      wx.showToast({
        title: '用户信息失效，请重新登录',
        icon: 'none'
      });
      me.setData({
        isUpload: false
      });
      return;
    }
    
    console.log('用户信息:', userInfo);
    console.log('服务器URL:', app.serverUrl);
    
    // 上传视频文件
    wx.uploadFile({
      url: app.getApiUrl('/video/upload'),
      filePath: tmpVideoUrl,
      name: 'file',
      formData: {
        userId: userInfo.id,
        bgmId: bgmId,
        videoSeconds: duration,
        videoWidth: tmpWidth,
        videoHeight: tmpHeight,
        desc: desc,
        videoCategory: videoCategory,
        videoFilter: videoFilter
      },
      success: function (res) {
        console.log('上传成功响应:', res);
        try {
          var data = JSON.parse(res.data);
          console.log('解析后的响应数据:', data);
          if (data.status == 200) {
            // 上传成功后，保存视频信息到本地存储
            var userFaceUrlKey = 'userFaceUrl_' + userInfo.id;
            var userFaceUrl = wx.getStorageSync(userFaceUrlKey) || userInfo.faceImage || "../resource/images/noneface.png";
            var videoInfo = {
              id: data.data,
              userId: userInfo.id,
              videoDesc: desc,
              videoPath: tmpVideoUrl,
              coverPath: tmpCoverUrl,
              createTime: new Date(),
              likeCounts: 0,
              commentCounts: 0,
              shareCounts: 0,
              face_image: userFaceUrl,
              nickName: userInfo.nickname || "用户",
              videoCategory: videoCategory,
              videoFilter: videoFilter
            };
            var userVideoListKey = 'videoList_' + userInfo.id;
            var videoList = wx.getStorageSync(userVideoListKey) || [];
            videoList.unshift(videoInfo);
            wx.setStorageSync(userVideoListKey, videoList);
            
            wx.hideLoading();
            wx.showToast({
              title: '上传成功',
              icon: "success",
            })
            setTimeout(function () {
              wx.switchTab({
                url: '../mine/mine',
              })
            }, 1000) //延迟时间 这里是1秒
          } else {
            wx.hideLoading();
            wx.showToast({
              title: data.msg,
              icon: "none"
            })
          }
        } catch (e) {
          console.error('解析响应数据失败:', e);
          wx.hideLoading();
          wx.showToast({
            title: '上传视频失败',
            icon: "none"
          })
        }
      },
      fail: function (res) {
        console.error('上传失败:', res);
        wx.hideLoading();
        wx.showToast({
          title: '上传视频失败',
          icon: "none"
        })
      },
      complete: function () {
        me.setData({
          isUpload: false
        })
      }
    })

  }
})
