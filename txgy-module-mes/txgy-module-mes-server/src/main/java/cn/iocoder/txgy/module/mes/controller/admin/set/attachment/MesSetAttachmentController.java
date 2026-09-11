package cn.iocoder.txgy.module.mes.controller.admin.set.attachment;

import cn.iocoder.txgy.framework.common.pojo.CommonResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.attachment.vo.MesSetAttachmentSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.attachment.MesSetAttachmentDO;
import cn.iocoder.txgy.module.mes.service.set.attachment.MesSetAttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.txgy.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - MES 安全环保检测-业务附件")
@RestController
@RequestMapping("/mes/safety-env/attachment")
@Validated
public class MesSetAttachmentController {

    @Resource
    private MesSetAttachmentService attachmentService;

    @PostMapping("/create")
    @Operation(summary = "登记业务附件（文件先经 /infra/file/upload 上传，这里只存地址）")
    @PreAuthorize("@ss.hasPermission('mes:set-attachment:update')")
    public CommonResult<Long> createAttachment(@Valid @RequestBody MesSetAttachmentSaveReqVO saveReqVO) {
        return success(attachmentService.createAttachment(saveReqVO));
    }

    @GetMapping("/list")
    @Operation(summary = "按业务键获得附件列表")
    @Parameter(name = "bizType", description = "业务关联类型", required = true)
    @Parameter(name = "bizNo", description = "业务关联单号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-attachment:query')")
    public CommonResult<List<MesSetAttachmentDO>> getAttachmentList(@RequestParam("bizType") String bizType,
                                                                    @RequestParam("bizNo") String bizNo) {
        return success(attachmentService.getAttachmentList(bizType, bizNo));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除业务附件（逻辑删，解除与单据的关联）")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mes:set-attachment:update')")
    public CommonResult<Boolean> deleteAttachment(@RequestParam("id") Long id) {
        attachmentService.deleteAttachment(id);
        return success(true);
    }

}
