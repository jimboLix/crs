package com.vacomall.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据字典项实体类
 */
@TableName("t_dict")
public class Dict implements Serializable {
    private static final long serialVersionUID = -6177738677015292373L;

    @TableId(type = IdType.UUID)
    private String id;

    private String name;

    private String value;

    private String typeValue;//保存的是dicttype的value值，用于建立dict于dictType的关联关系

    private String desc;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(String typeValue) {
        this.typeValue = typeValue;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
