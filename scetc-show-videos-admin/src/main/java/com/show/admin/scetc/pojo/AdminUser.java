package com.show.admin.scetc.pojo;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 定义管理员账户
 * 
 * @author Ray
 */
@Table(name = "adminusers")
public class AdminUser {

	@Id
	@Column(name = "id")
	private Long id;
	@Column(name = "username")
	private String username;// 账号
	@Column(name = "realname")
	private String realName;// 真实名称
	@Column(name = "password")
	private String password;
	@Column(name = "phonenumber")
	private String phoneNumber;
	@Column(name = "email")
	private String email;
	@Column(name = "position")
	private String position;
	@Column(name = "salt")
	private String salt;// 随机盐
	@Column(name = "qq")
	private String qq;
	@Column(name = "latitude")
	private Double latitude;
	@Column(name = "longitude")
	private Double longitude;// 经度
	@Column(name = "registerdate")
	private Date registerDate;
	@Column(name = "updatedate")
	private Date updateDate;
	@Column(name = "loginip")
	private String loginIp;// 登陆ip
	@Column(name = "useragent")
	private String userAgent;// 用户代理
	@Column(name = "geohash")
	private Long GeoHash;// 用于经纬度
	@Column(name = "isdeleted")
	private Boolean isDeleted;// 软删除

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getLoginIp() {
		return loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public Long getGeoHash() {
		return GeoHash;
	}

	public void setGeoHash(Long geoHash) {
		GeoHash = geoHash;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	@Override
	public String toString() {
		return "AdminUser [id=" + id + ", username=" + username + ", realName=" + realName + ", password=" + password
				+ ", phoneNumber=" + phoneNumber + ", email=" + email + ", position=" + position + ", salt=" + salt
				+ ", qq=" + qq + ", latitude=" + latitude + ", Longitude=" + longitude + ", registerDate="
				+ registerDate + ", updateDate=" + updateDate + ", loginIp=" + loginIp + ", userAgent=" + userAgent
				+ ", GeoHash=" + GeoHash + ", isDeleted=" + isDeleted + "]";
	}

}



