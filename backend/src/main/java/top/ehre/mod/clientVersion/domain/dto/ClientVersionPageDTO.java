package top.ehre.mod.clientVersion.domain.dto;

import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import top.ehre.mod.util.PageParam;

/**
 * 客户端版本信息表分页查询DTO
 *
 * @author LibrhHp_0928
 * @since 2025-12-10 19:58:16
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "ClientVersion分页查询对象", description = "客户端版本信息表分页查询DTO")
public class ClientVersionPageDTO extends PageParam {
}
