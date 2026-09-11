package cn.iocoder.txgy.module.mes.job.pollution;

import cn.iocoder.txgy.framework.tenant.core.job.TenantJob;
import cn.iocoder.txgy.module.mes.service.pollution.MesPollutionControlService;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * MES 环保-批次过期冻结 Job（调度底座随一期落地的第一个任务）
 *
 * 职责：把已过期批次的在库行 `material_stock.frozen` 置冻（FEFO / 过期冻结）。
 *
 * 与复核路径的关系：冻结判据 {@code isBatchFrozen} 是**共享**的——建单/复核/删单都会实时重算，
 * 本 Job 只是兜底网，捞那些"到期后再没有产生任何判定动作"因而没人触发重算的批次。
 * 因此本 Job 幂等，重复执行与不执行都不会与实时路径冲突。
 *
 * 注意：过期是**只冻结、不污染**——过期不等于有害，不写批次污染戳、不登污染台账。
 *
 * @author OPENLAB BS
 */
@Component
public class MesSetBatchExpireFreezeJob {

    @Resource
    private MesPollutionControlService pollutionControlService;

    @XxlJob("mesSetBatchExpireFreezeJob")
    @TenantJob
    public String execute() {
        int rows = pollutionControlService.freezeExpiredBatchStock();
        return String.format("MES 批次过期冻结：更新在库行 %s 行", rows);
    }

}
