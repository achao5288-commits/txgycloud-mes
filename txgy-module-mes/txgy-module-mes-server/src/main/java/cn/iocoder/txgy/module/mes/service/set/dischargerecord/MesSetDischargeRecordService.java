package cn.iocoder.txgy.module.mes.service.set.dischargerecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.dischargerecord.vo.MesSetDischargeRecordPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.dischargerecord.MesSetDischargeRecordDO;

/**
 * MES 安全环保检测-排放合规流水 Service 接口
 *
 * 排放流水只增不改删：台账流转到 已排放(DISCHARGED) 时内部追加(createDischargeRecord)，对前端只读 page。
 *
 * @author OPENLAB BS
 */
public interface MesSetDischargeRecordService {

    /**
     * 追加排放合规流水（内部调用：台账 已排放 流转落档，须在调用方事务内）
     *
     * @param record 排放记录（ledger_id/source_record_no/destination/standard 等须由调用方组装）
     * @return 编号
     */
    Long createDischargeRecord(MesSetDischargeRecordDO record);

    /**
     * 获得排放合规流水分页
     *
     * @param pageReqVO 分页查询
     * @return 排放合规流水分页
     */
    PageResult<MesSetDischargeRecordDO> getDischargeRecordPage(MesSetDischargeRecordPageReqVO pageReqVO);

}
