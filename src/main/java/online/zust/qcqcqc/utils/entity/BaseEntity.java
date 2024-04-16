package online.zust.qcqcqc.utils.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import online.zust.qcqcqc.utils.generators.annotation.ColumnType;
import online.zust.qcqcqc.utils.generators.enums.DataType;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author qcqcqc
 */
public class BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = -7199278847764058483L;
    /**
     * 主键ID
     */
    @TableId
    @ColumnType(type = DataType.Bigint, length = 20, nullable = false, comment = "主键ID")
    private Long id;

    /**
     * 本条记录创建人，insert操作的时候自动为该字段赋值
     */
    @TableField(fill = FieldFill.INSERT)
    @ColumnType(type = DataType.Bigint, length = 20, nullable = false, comment = "本条记录创建人", defaultValue = "0")
    private Long createBy;

    /**
     * 本条记录创建时间，insert操作的时候自动为该字段赋值
     */
    @TableField(fill = FieldFill.INSERT)
    @ColumnType(type = DataType.Datetime, nullable = false, comment = "本条记录创建时间", defaultValue = "CURRENT_TIMESTAMP")
    private Date createTime;

    /**
     * 本条记录更新人，insert或update操作的时候自动为该字段赋值，select = false
     */
    @TableField(fill = FieldFill.INSERT_UPDATE, select = false)
    @ColumnType(type = DataType.Bigint, length = 20, nullable = false, comment = "本条记录更新人", defaultValue = "0")
    private Long updateBy;

    /**
     * 本条记录更新时间，insert或update操作的时候自动为该字段赋值，select = false
     */
    @TableField(fill = FieldFill.INSERT_UPDATE, select = false)
    @ColumnType(type = DataType.Datetime, nullable = false, comment = "本条记录更新时间", defaultValue = "CURRENT_TIMESTAMP")
    private Date updateTime;

    /**
     * 逻辑删除标识，0表示未删除，1表示已删除
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @ColumnType(type = DataType.Tinyint, length = 1, nullable = false, comment = "逻辑删除标识", defaultValue = "0")
    private Boolean deleted;

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
