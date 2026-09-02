package cn.iocoder.txgy.module.mes.service.set.noiserecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.noiserecord.vo.MesSetNoiseRecordPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.noiserecord.vo.MesSetNoiseRecordSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.noiserecord.MesSetNoiseRecordDO;

import jakarta.validation.Valid;

/**
 * MES 安全环保检测-噪声检测记录 Service 接口
 *
 * @author OPENLAB BS
 */
public interface MesSetNoiseRecordService {

    /**
     * 创建噪声检测记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createNoiseRecord(@Valid MesSetNoiseRecordSaveReqVO createReqVO);

    /**
     * 更新噪声检测记录
     *
     * @param updateReqVO 更新信息
     */
    void updateNoiseRecord(@Valid MesSetNoiseRecordSaveReqVO updateReqVO);

    /**
     * 删除噪声检测记录
     *
     * @param id 编号
     */
    void deleteNoiseRecord(Long id);

    /**
     * 校验噪声检测记录存在
     *
     * @param id 编号
     */
    void validateNoiseRecordExists(Long id);

    /**
     * 获得噪声检测记录
     *
     * @param id 编号
     * @return 噪声检测记录
     */
    MesSetNoiseRecordDO getNoiseRecord(Long id);

    /**
     * 获得噪声检测记录分页
     *
     * @param pageReqVO 分页查询
     * @return 噪声检测记录分页
     */
    PageResult<MesSetNoiseRecordDO> getNoiseRecordPage(MesSetNoiseRecordPageReqVO pageReqVO);

}
