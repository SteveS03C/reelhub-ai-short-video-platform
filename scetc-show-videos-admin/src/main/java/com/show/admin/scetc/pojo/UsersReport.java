package com.show.admin.scetc.pojo;

import java.util.Date;
import javax.persistence.*;

@Table(name = "users_report")
public class UsersReport {
	@Id
	private String id;

	@Column(name = "deal_user_id")
	private String dealUserId;

	@Column(name = "deal_video_id")
	private String dealVideoId;

	private String title;

	private String content;

	private String userid;

	@Column(name = "create_date")
	private Date createDate;

	@Column(name = "process_status")
	private String processStatus;

	@Column(name = "process_result")
	private String processResult;

	@Column(name = "process_admin")
	private String processAdmin;

	@Column(name = "process_time")
	private Date processTime;

	/**
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return deal_user_id
	 */
	public String getDealUserId() {
		return dealUserId;
	}

	/**
	 * @param dealUserId
	 */
	public void setDealUserId(String dealUserId) {
		this.dealUserId = dealUserId;
	}

	/**
	 * @return deal_video_id
	 */
	public String getDealVideoId() {
		return dealVideoId;
	}

	/**
	 * @param dealVideoId
	 */
	public void setDealVideoId(String dealVideoId) {
		this.dealVideoId = dealVideoId;
	}

	/**
	 * @return title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return userid
	 */
	public String getUserid() {
		return userid;
	}

	/**
	 * @param userid
	 */
	public void setUserid(String userid) {
		this.userid = userid;
	}

	/**
	 * @return create_date
	 */
	public Date getCreateDate() {
		return createDate;
	}

	/**
	 * @param createDate
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

	public String getProcessResult() {
		return processResult;
	}

	public void setProcessResult(String processResult) {
		this.processResult = processResult;
	}

	public String getProcessAdmin() {
		return processAdmin;
	}

	public void setProcessAdmin(String processAdmin) {
		this.processAdmin = processAdmin;
	}

	public Date getProcessTime() {
		return processTime;
	}

	public void setProcessTime(Date processTime) {
		this.processTime = processTime;
	}
}


