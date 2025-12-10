package top.ehre.mod.clientVersion.mapper;

import top.ehre.mod.clientVersion.domain.entity.ClientVersionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.ehre.mod.clientVersion.domain.dto.ClientVersionPageDTO;
import top.ehre.mod.clientVersion.domain.vo.ClientVersionVO;
import org.springframework.stereotype.Component;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;

/**
 * 客户端版本信息表 Mapper 接口
 *
 * @author LibrhHp_0928
 * @since 2025-12-10 19:58:16
 */
@Mapper
@Component
public interface ClientVersionMapper extends BaseMapper<ClientVersionEntity> {



    @SelectProvider(type = ClientVersionSqlProvider.class, method = "queryPage")
    List<ClientVersionVO> queryPage(Page page, @Param("pageDTO") ClientVersionPageDTO pageDTO);

    class ClientVersionSqlProvider {
        public String queryPage(final Page page, final ClientVersionPageDTO pageDTO) {
            return new SQL() {{
                SELECT("id,version_number,version_description,update_content,download_url,is_released,created_time,updated_time");
                FROM("client_version");
                if (pageDTO != null) {
                }
            }}.toString();
        }
    }

    ClientVersionEntity getPublishVersion();

}
