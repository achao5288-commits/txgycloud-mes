package cn.iocoder.txgy.module.mes.service.set.chemical;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.chemical.vo.*;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.chemical.MesSetChemicalProfileDO;

/**
 * MES 安全环保检测-危化品档案 Service（设计文档 §5 主线A：分级储存 / 禁配 / 储量上限 / 五双）
 *
 * @author OPENLAB BS
 */
public interface MesSetChemicalProfileService {

    /**
     * 五双作业类型：双人收发 / 双人保管 / 双人双锁 / 双人领料 / 双人运输（设计文档 §5.3）
     */
    String[] DOUBLE_SIGN_ACTIONS = {"RECEIVE", "KEEP", "LOCK", "ISSUE", "TRANSPORT"};

    /**
     * 五双签字在 mes_set_sign_record 里的业务类型
     */
    String DOUBLE_SIGN_BIZ_TYPE = "CHEMICAL_DOUBLE";

    Long createProfile(MesSetChemicalProfileSaveReqVO createReqVO);

    void updateProfile(MesSetChemicalProfileSaveReqVO updateReqVO);

    void deleteProfile(Long id);

    MesSetChemicalProfileDO getProfile(Long id);

    MesSetChemicalProfileDO getProfileByNo(String profileNo);

    PageResult<MesSetChemicalProfileDO> getProfilePage(MesSetChemicalProfilePageReqVO pageReqVO);

    /**
     * 库位/入库校验（只判不写）：MSDS 挂载 → 专区要求 → 同库位禁配 → 储量上限。
     */
    MesSetChemicalStorageCheckRespVO checkStorage(MesSetChemicalStorageCheckReqVO reqVO);

    /**
     * 危化品入库：过与 checkStorage 同一套校验后累加存量。任一条不过即拒，不写库。
     */
    MesSetChemicalStorageCheckRespVO stockIn(MesSetChemicalStockInReqVO reqVO);

    /**
     * 五双双人签字：同一作业同一动作，须两个**不同账号**各签一次。
     */
    MesSetChemicalDoubleSignRespVO doubleSign(MesSetChemicalDoubleSignReqVO reqVO);

    MesSetChemicalDoubleSignRespVO getDoubleSignStatus(String bizNo, String signAction);

    /**
     * 预警汇总：超量 / MSDS 缺失 / MSDS 版本将到期。
     *
     * @param days MSDS 到期预警天数（设计文档 §5 资质到期前 30 天预警）
     */
    MesSetChemicalAlertRespVO getAlerts(Integer days);

}
