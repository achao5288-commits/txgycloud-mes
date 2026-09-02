package cn.iocoder.txgy.module.mes.service.set.electricalrecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.electricalrecord.vo.MesSetElectricalRecordPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.electricalrecord.vo.MesSetElectricalRecordSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.electricalrecord.MesSetElectricalRecordDO;

import jakarta.validation.Valid;

/**
 * MES 安全环保检测-电气安全检测记录 Service 接口
 *
 * @author OPENLAB BS
 */
public interface MesSetElectricalRecordService {

    /**
     * 创建电气安全检测记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createElectricalRecord(@Valid MesSetElectricalRecordSaveReqVO createReqVO);

    /**
     * 更新电气安全检测记录
     *
     * @param updateReqVO 更新信息
     */
    void updateElectricalRecord(@Valid MesSetElectricalRecordSaveReqVO updateReqVO);

    /**
     * 删除电气安全检测记录
     *
     * @param id 编号
     */
    void deleteElectricalRecord(Long id);

    /**
     * 校验电气安全检测记录存在
     *
     * @param id 编号
     */
    void validateElectricalRecordExists(Long id);

    /**
     * 获得电气安全检测记录
     *
     * @param id 编号
     * @return 电气安全检测记录
     */
    MesSetElectricalRecordDO getElectricalRecord(Long id);

    /**
     * 获得电气安全检测记录分页
     *
     * @param pageReqVO 分页查询
     * @return 电气安全检测记录分页
     */
    PageResult<MesSetElectricalRecordDO> getElectricalRecordPage(MesSetElectricalRecordPageReqVO pageReqVO);

}
