package com.vacomall.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 会议室实体类
 */
@TableName("t_rome")
public class MeetingRome extends Model<MeetingRome> {


    private static final long serialVersionUID = -8340388869236462615L;

    @TableId(type= IdType.UUID)
    private String id;
    //会议室名称
    private String name;
    //会议室编号
    private String no;
    //会议室详细位置
    private String location;
    //会议室所属校区
    private String campus;
    //会议室所属办公楼
    private String building;
    //会议室容量，人数
    private Integer volume;
    //会议室设施，比如投影仪。。
    private String facilities;
    //会议室的详细说明
    private String detail;


    private Date createTime;

    public Date getCreateTime() {
        return createTime == null ? new Date() : this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
