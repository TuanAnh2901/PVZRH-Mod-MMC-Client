package top.ehre.mod.clientVersion.domain.vo;

import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import java.util.List;


/**
 * 客户端版本信息表视图VO
 *
 * @author LibrhHp_0928
 * @since 2025-12-10 19:58:16
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "ClientVersion视图对象", description = "客户端版本信息表视图VO")
public class ClientVersionVO {

    @ApiModelProperty("ID")
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