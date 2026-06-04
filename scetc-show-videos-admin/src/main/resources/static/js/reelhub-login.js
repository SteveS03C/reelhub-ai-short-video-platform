function byId(id) {
  return document.getElementById(id);
}

function qs(selector) {
  return document.querySelector(selector);
}

function getValue(selector) {
  var el = qs(selector);
  return el ? el.value || "" : "";
}

function trim(value) {
  return (value || "").replace(/^\s+|\s+$/g, "");
}

function setScene(value) {
  var root = byId("sceneRoot");
  if (!root) return;
  root.setAttribute("data-scene", value);
}

function showLoginMessage(message) {
  var el = byId("message");
  if (!el) return;
  el.innerText = message || "";
}

function setButtonLoading(loading) {
  var btnEl = byId("loginBtn");
  if (!btnEl) return;
  btnEl.disabled = !!loading;
  btnEl.innerText = loading ? "登录中..." : "登录";
}

function focusSceneFromActive() {
  var active = document.activeElement;
  if (!active) {
    setScene("idle");
    return;
  }
  if (active.name === "username") {
    setScene("focus-username");
    return;
  }
  if (active.name === "password") {
    var input = byId("passwordInput");
    if (input && input.type === "text") {
      setScene("reveal-password");
      return;
    }
    setScene("focus-password");
    return;
  }
  if (active.name === "verifyCode") {
    setScene("focus-captcha");
    return;
  }
  setScene("idle");
}

function serializeForm(form) {
  var parts = [];
  if (!form || !form.elements) return "";
  for (var i = 0; i < form.elements.length; i++) {
    var el = form.elements[i];
    if (!el || !el.name || el.disabled) continue;
    if (el.type === "button" || el.type === "submit") continue;
    parts.push(encodeURIComponent(el.name) + "=" + encodeURIComponent(el.value || ""));
  }
  return parts.join("&");
}

function setLoginLoading(loading) {
  setButtonLoading(loading);
  setScene(loading ? "submitting" : "idle");
}

var __isSubmitting = false;

function setPupilOffset(eyeEl, dx, dy) {
  var maxX = 6;
  var maxY = 4;
  var x = Math.max(-maxX, Math.min(maxX, dx * maxX));
  var y = Math.max(-maxY, Math.min(maxY, dy * maxY));
  eyeEl.style.setProperty("--px", x + "px");
  eyeEl.style.setProperty("--py", y + "px");
}

function setupEyes() {
  var eyes = Array.prototype.slice.call(document.querySelectorAll("[data-eye]"));
  if (!eyes.length) return;
  var mouse = { x: window.innerWidth / 2, y: window.innerHeight / 2 };
  var rafId = 0;
  function update() {
    rafId = 0;
    for (var i = 0; i < eyes.length; i++) {
      var rect = eyes[i].getBoundingClientRect();
      var cx = rect.left + rect.width / 2;
      var cy = rect.top + rect.height / 2;
      var vx = mouse.x - cx;
      var vy = mouse.y - cy;
      var len = Math.sqrt(vx * vx + vy * vy) || 1;
      setPupilOffset(eyes[i], vx / len, vy / len);
    }
  }
  function schedule() {
    if (rafId) return;
    rafId = window.requestAnimationFrame(update);
  }
  document.addEventListener("mousemove", function (e) {
    mouse.x = e.clientX;
    mouse.y = e.clientY;
    schedule();
  });
  update();
}

var __captchaObjectUrl = null;
function loadCaptcha() {
  var img = byId("verifyCodeImage");
  if (!img) return;
  var baseUrl = img.getAttribute("data-url") || "/other/imageCode.do";
  var url = baseUrl + (baseUrl.indexOf("?") >= 0 ? "&" : "?") + "t=" + new Date().getTime();
  var xhr = new XMLHttpRequest();
  xhr.open("GET", url, true);
  xhr.responseType = "arraybuffer";
  xhr.onreadystatechange = function () {
    if (xhr.readyState !== 4) return;
    if (xhr.status >= 200 && xhr.status < 300) {
      try {
        var bytes = xhr.response;
        var blob = new Blob([bytes], { type: "image/jpeg" });
        var nextUrl = (window.URL || window.webkitURL).createObjectURL(blob);
        if (__captchaObjectUrl) {
          (window.URL || window.webkitURL).revokeObjectURL(__captchaObjectUrl);
        }
        __captchaObjectUrl = nextUrl;
        img.src = nextUrl;
      } catch (e) {
        img.src = url;
      }
      return;
    }
    img.src = url;
  };
  xhr.send();
}

function changeCode() {
  loadCaptcha();
  var input = byId("user_input_verifyCode");
  if (input) {
    input.value = "";
    input.focus();
  }
  setScene("focus-captcha");
}

function handleLoginResponse(res) {
  if (res && res.status == 200) {
    setScene("success");
    window.location.href = "/index";
    return;
  }
  setScene("error");
  changeCode();
  var p = qs("input[name='password']");
  if (p) p.value = "";
  showLoginMessage(res && res.msg ? res.msg : "登录失败，请重试");
  setTimeout(function () {
    focusSceneFromActive();
  }, 650);
}

function handleLoginError() {
  setScene("error");
  showLoginMessage("请求失败，请重试");
  setTimeout(function () {
    focusSceneFromActive();
  }, 650);
}

function loginSubmit() {
  if (__isSubmitting) return;
  showLoginMessage("");
  setLoginLoading(true);
  __isSubmitting = true;
  var form = byId("myform");
  var payload = serializeForm(form);
  var url = "/adminUser/loginSubmit";

  if (window.jQuery && window.jQuery.ajax) {
    window.jQuery.ajax({
      url: url,
      data: payload,
      type: "post",
      dataType: "json",
      success: function (res) {
        handleLoginResponse(res);
      },
      error: function () {
        handleLoginError();
      },
      complete: function () {
        setLoginLoading(false);
        __isSubmitting = false;
      },
    });
    return;
  }

  var xhr = new XMLHttpRequest();
  xhr.open("POST", url, true);
  xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
  xhr.onreadystatechange = function () {
    if (xhr.readyState !== 4) return;
    setLoginLoading(false);
    __isSubmitting = false;
    if (xhr.status >= 200 && xhr.status < 300) {
      try {
        var res = JSON.parse(xhr.responseText);
        handleLoginResponse(res);
      } catch (e) {
        handleLoginError();
      }
      return;
    }
    handleLoginError();
  };
  xhr.send(payload);
}

function handleLoginTrigger() {
  if (__isSubmitting) return;
  var username = trim(getValue("input[name='username']"));
  var password = trim(getValue("input[name='password']"));
  var verifyCode = trim(getValue("#user_input_verifyCode"));
  showLoginMessage("");

  if (!username) {
    showLoginMessage("请输入用户名");
    var u = qs("input[name='username']");
    if (u) u.focus();
    setScene("focus-username");
    return;
  }
  if (!password) {
    showLoginMessage("请输入密码");
    var p = qs("input[name='password']");
    if (p) p.focus();
    setScene("focus-password");
    return;
  }
  if (!verifyCode) {
    showLoginMessage("请输入验证码");
    var c = byId("user_input_verifyCode");
    if (c) c.focus();
    setScene("focus-captcha");
    return;
  }
  loginSubmit();
}

function initLoginPage() {
  loadCaptcha();

  var usernameEl = qs("input[name='username']");
  var passwordEl = qs("input[name='password']");
  var captchaEl = byId("user_input_verifyCode");

  if (usernameEl) usernameEl.addEventListener("focus", function () { setScene("focus-username"); });
  if (passwordEl) passwordEl.addEventListener("focus", function () {
    var input = byId("passwordInput");
    if (input && input.type === "text") {
      setScene("reveal-password");
      return;
    }
    setScene("focus-password");
  });
  if (captchaEl) captchaEl.addEventListener("focus", function () { setScene("focus-captcha"); });

  var blurTargets = [usernameEl, passwordEl, captchaEl];
  for (var i = 0; i < blurTargets.length; i++) {
    if (!blurTargets[i]) continue;
    blurTargets[i].addEventListener("blur", function () {
      setTimeout(function () {
        focusSceneFromActive();
      }, 0);
    });
  }

  var pwdToggle = byId("pwdToggle");
  if (pwdToggle) {
    pwdToggle.addEventListener("click", function () {
      var input = byId("passwordInput");
      var isOpen = this.getAttribute("data-open") === "true";
      var next = !isOpen;
      this.setAttribute("data-open", next ? "true" : "false");
      if (input) input.type = next ? "text" : "password";
      if (next) {
        setScene("reveal-password");
        return;
      }
      focusSceneFromActive();
    });
  }

  var captchaImg = byId("verifyCodeImage");
  if (captchaImg) captchaImg.addEventListener("click", changeCode);
  var captchaRefresh = byId("captchaRefresh");
  if (captchaRefresh) captchaRefresh.addEventListener("click", changeCode);

  var form = byId("myform");
  if (form) {
    form.addEventListener("keypress", function (e) {
      var key = e.which || e.keyCode;
      if (key === 13) {
        e.preventDefault();
        handleLoginTrigger();
      }
    });
  }

  var btn = byId("loginBtn");
  if (btn) btn.addEventListener("click", function () { handleLoginTrigger(); });

  setupEyes();
}

if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", initLoginPage);
} else {
  initLoginPage();
}

window.changeCode = changeCode;
