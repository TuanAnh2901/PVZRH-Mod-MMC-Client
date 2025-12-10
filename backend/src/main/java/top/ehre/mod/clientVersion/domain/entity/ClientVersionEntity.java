package top.ehre.mod.clientVersion.domain.entity;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 客户端版本信息表
 *
 * @author LibrhHp_0928
 * @since 2025-12-10 19:58:16
 */
@Data
@Accessors(chain = true)
@TableName("client_version")
@ApiModel(value = "clientVersion对象", description = "客户端版本信息表")
public class ClientVersionEntity {


    @ApiModelProperty("ID")
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    @ApiModelProperty("版本号")
    private String versionNumber;

    @ApiModelProperty("版本描述")
    private String versionDescription;

    @ApiModelProperty("版本更新内容")
    private String updateContent;

    @ApiModelProperty("版本下载地址")
    private String downloadUrl;

    @ApiModelProperty("是否发布版本（0:未发布，1:已发布）")
    private Boolean isReleased;

    @ApiModelProperty("创建时间")
    private LocalDateTime createdTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updatedTime;
}
