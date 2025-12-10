package top.ehre.mod.clientVersion.service;

import top.ehre.mod.clientVersion.domain.entity.ClientVersionEntity;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.extension.service.IService;
import top.ehre.mod.clientVersion.domain.vo.ClientVersionVO;
import top.ehre.mod.clientVersion.domain.dto.ClientVersionPageDTO;
import top.ehre.mod.clientVersion.domain.dto.ClientVersionAddDTO;
import top.ehre.mod.clientVersion.domain.dto.ClientVersionUpdateDTO;
import top.ehre.mod.util.PageResult;

import java.util.List;


/**
 * 客户端版本信息表 服务类
 *
 * @author LibrhHp_0928
 * @since 2025-12-10 19:58:16
 */
public interface ClientVersionService extends IService<ClientVersionEntity> {

    PageResult<ClientVersionVO> page(ClientVersionPageDTO clientVersionPageDTO);

    boolean add(ClientVersionAddDTO clientVersionAddDTO);

    boolean delete(String id);

    boolean batchDelete(List<String> ids);

    boolean update(ClientVersionUpdateDTO clientVersionUpdateDTO);

    ClientVersionVO get(String id);

    ClientVersionVO getPublicVersion();
}
