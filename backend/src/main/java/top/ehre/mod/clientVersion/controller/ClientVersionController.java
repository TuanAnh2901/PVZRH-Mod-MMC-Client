package top.ehre.mod.clientVersion.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import top.ehre.mod.clientVersion.service.ClientVersionService;

import top.ehre.mod.clientVersion.domain.vo.ClientVersionVO;
import top.ehre.mod.clientVersion.domain.dto.ClientVersionPageDTO;
import top.ehre.mod.clientVersion.domain.dto.ClientVersionAddDTO;
import top.ehre.mod.clientVersion.domain.dto.ClientVersionUpdateDTO;

import top.ehre.mod.util.PageResult;
import top.ehre.mod.util.Result;

import java.util.List;


/**
 * 客户端版本信息表 前端控制器
 *
 * @author LibrhHp_0928
 * @since 2025-12-10 19:58:16
 */
@RestController
@RequestMapping("/client-version")
public class ClientVersionController {
    @Resource
    private ClientVersionService clientVersionService;

    @PostMapping("/page")
    @PreAuthorize("hasAuthority('business:version:get')")
    public Result page(@RequestBody ClientVersionPageDTO clientVersionPageDTO) {
        PageResult<ClientVersionVO> page = clientVersionService.page(clientVersionPageDTO);
        return Result.success(page);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('business:version:add')")
    public Result add(@RequestBody ClientVersionAddDTO clientVersionAddDTO) {
        boolean added = clientVersionService.add(clientVersionAddDTO);
        return Result.info(added, null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('business:version:get')")
    public Result delete(@PathVariable("id") String id) {
        boolean deleted = clientVersionService.delete(id);
        return Result.info(deleted, null);
    }

    @PostMapping("/batchDelete")
    @PreAuthorize("hasAuthority('business:version:del')")
    public Result batchDelete(@RequestBody List<String> ids) {
        boolean deleted = clientVersionService.batchDelete(ids);
        return Result.info(deleted, null);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('business:version:upd')")
    public Result update(@RequestBody ClientVersionUpdateDTO clientVersionUpdateDTO) {
        boolean updated = clientVersionService.update(clientVersionUpdateDTO);
        return Result.info(updated, null);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('business:version:get')")
    public Result get(@PathVariable("id") String id) {
        ClientVersionVO clientVersionVO = clientVersionService.get(id);
        return Result.success(clientVersionVO);
    }

}
