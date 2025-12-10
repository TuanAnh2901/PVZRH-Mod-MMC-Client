package top.ehre.mod.clientVersion.service.impl;

import top.ehre.mod.clientVersion.domain.entity.ClientVersionEntity;
import top.ehre.mod.clientVersion.mapper.ClientVersionMapper;
import top.ehre.mod.clientVersion.service.ClientVersionService;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import top.ehre.mod.clientVersion.domain.vo.ClientVersionVO;
import top.ehre.mod.clientVersion.domain.dto.ClientVersionPageDTO;
import top.ehre.mod.clientVersion.domain.dto.ClientVersionAddDTO;
import top.ehre.mod.clientVersion.domain.dto.ClientVersionUpdateDTO;
import top.ehre.mod.util.PageResult;
import top.ehre.mod.util.PageUtil;
import top.ehre.mod.exception.BusinessException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
// ---------------------- 联表导包 -------------------------
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
// ---------------------------------------------------------

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * 客户端版本信息表 服务实现类
 *
 * @author LibrhHp_0928
 * @since 2025-12-10 19:58:16
 */
@Service
public class ClientVersionServiceImpl extends ServiceImpl<ClientVersionMapper, ClientVersionEntity> implements ClientVersionService {

    @Resource
    ClientVersionMapper clientVersionMapper;



    public PageResult<ClientVersionVO> page(ClientVersionPageDTO clientVersionPageDTO) {
        Page<?> page = PageUtil.convert2PageQuery(clientVersionPageDTO);
        List<ClientVersionVO> list = clientVersionMapper.queryPage(page, clientVersionPageDTO);
        PageResult<ClientVersionVO> pageResult = PageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean add(ClientVersionAddDTO clientVersionAddDTO) {
        ClientVersionEntity clientVersion = new ClientVersionEntity();
        BeanUtils.copyProperties(clientVersionAddDTO, clientVersion);
        boolean saved = save(clientVersion);
        if (!saved) {
            throw new BusinessException("添加失败");
        }
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean delete(String id) {
        ClientVersionEntity exists = getById(id);
        if (exists == null) throw new BusinessException("不存在该对象");
        boolean removed = removeById(id);
        if (!removed) throw new BusinessException("删除失败");
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean batchDelete(List<String> ids) {
        boolean removed = removeBatchByIds(ids);
        if (!removed) throw new BusinessException("删除失败");
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean update(ClientVersionUpdateDTO clientVersionUpdateDTO) {
        ClientVersionEntity clientVersion = new ClientVersionEntity();
        BeanUtils.copyProperties(clientVersionUpdateDTO, clientVersion);
        if (clientVersion.getId() == null) throw new BusinessException("主键不能为空");
        else {
            ClientVersionEntity exists = getById(clientVersion.getId());
            if (exists == null) throw new BusinessException("不存在该对象");
        }
        boolean updated = updateById(clientVersion);
        if (!updated) {
            throw new BusinessException("更新失败");
        }
        return true;
    }

    public ClientVersionVO get(String id) {
        ClientVersionEntity clientVersion = getById(id);
        if (clientVersion == null) {
            throw new BusinessException("不存在该对象");
        }
        ClientVersionVO clientVersionVO = new ClientVersionVO();
        BeanUtils.copyProperties(clientVersion, clientVersionVO);
        return clientVersionVO;
    }

    @Override
    public ClientVersionVO getPublicVersion() {
        ClientVersionEntity clientVersion = clientVersionMapper.getPublishVersion();
        if (clientVersion == null) {
            throw new BusinessException("不存在该对象");
        }
        ClientVersionVO clientVersionVO = new ClientVersionVO();
        BeanUtils.copyProperties(clientVersion, clientVersionVO);
        return clientVersionVO;
    }
}
