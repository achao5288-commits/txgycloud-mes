package cn.iocoder.txgy.module.mes.service.set.emergencymaterial;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.common.util.object.BeanUtils;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencymaterial.vo.MesSetEmergencyMaterialPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.emergencymaterial.vo.MesSetEmergencyMaterialSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.emergencymaterial.MesSetEmergencyMaterialDO;
import cn.iocoder.txgy.module.mes.dal.mysql.set.emergencymaterial.MesSetEmergencyMaterialMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cn.iocoder.txgy.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_MATERIAL_NOT_EXISTS;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_MATERIAL_NO_DUPLICATE;
import static cn.iocoder.txgy.module.mes.enums.ErrorCodeConstants.SET_EMERGENCY_MATERIAL_TYPE_INVALID;

/**
 * MES 安全环保检测-应急物资 Service 实现类
 *
 * @author OPENLAB BS
 */
@Service
@Validated
public class MesSetEmergencyMaterialServiceImpl implements MesSetEmergencyMaterialService {

    /**
     * 物资类型（设计文档 §八.2 点名的八类，不自己加）
     */
    private static final Set<String> MATERIAL_TYPES = new HashSet<>(Arrays.asList(
            "DRY_SAND", "OIL_ABSORBENT", "CHEM_SUIT", "GAS_MASK",
            "EXPLOSION_TOOL", "SANDBAG", "EYE_WASH", "DRY_POWDER"));

    public static final String STATUS_NORMAL = "NORMAL";
    public static final String STATUS_EXPIRING = "EXPIRING";
    public static final String STATUS_EXPIRED = "EXPIRED";
    public static final String STATUS_OUT = "OUT";

    /**
     * 临期阈值（天）。设计文档只说"临期预警"没给数，取 30 天：
     * 防化服/灭火器这类采购周期通常在两周以上，30 天留得出补货时间。
     */
    private static final int EXPIRING_DAYS = 30;

    @Resource
    private MesSetEmergencyMaterialMapper emergencymaterialMapper;

    @Override
    public Long createEmergencyMaterial(MesSetEmergencyMaterialSaveReqVO createReqVO) {
        validateBase(createReqVO, null);
        MesSetEmergencyMaterialDO obj = BeanUtils.toBean(createReqVO, MesSetEmergencyMaterialDO.class);
        applyStatus(obj);
        emergencymaterialMapper.insert(obj);
        return obj.getId();
    }

    @Override
    public void updateEmergencyMaterial(MesSetEmergencyMaterialSaveReqVO updateReqVO) {
        MesSetEmergencyMaterialDO exist = validateEmergencyMaterialExists(updateReqVO.getId());
        validateBase(updateReqVO, exist.getMaterialNo());
        MesSetEmergencyMaterialDO obj = BeanUtils.toBean(updateReqVO, MesSetEmergencyMaterialDO.class);
        applyStatus(obj);
        emergencymaterialMapper.updateById(obj);
    }

    @Override
    public void deleteEmergencyMaterial(Long id) {
        validateEmergencyMaterialExists(id);
        emergencymaterialMapper.deleteById(id);
    }

    @Override
    public MesSetEmergencyMaterialDO getEmergencyMaterial(Long id) {
        return emergencymaterialMapper.selectById(id);
    }

    @Override
    public PageResult<MesSetEmergencyMaterialDO> getEmergencyMaterialPage(MesSetEmergencyMaterialPageReqVO pageReqVO) {
        return emergencymaterialMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MesSetEmergencyMaterialDO> getAlerts() {
        // ponytail: 全表取回内存筛。应急物资是几十行的台账，不值得为它写 SQL 条件；
        // 真涨到上千行再把派生状态落列 + 加索引。
        List<MesSetEmergencyMaterialDO> all = emergencymaterialMapper.selectList();
        return all.stream()
                .peek(this::applyStatus)
                .filter(m -> !STATUS_NORMAL.equals(m.getStatus()))
                .sorted(Comparator.comparingInt((MesSetEmergencyMaterialDO m) -> severity(m.getStatus())))
                .toList();
    }

    private int severity(String status) {
        if (STATUS_OUT.equals(status)) {
            return 0;
        }
        if (STATUS_EXPIRED.equals(status)) {
            return 1;
        }
        return 2;
    }

    private MesSetEmergencyMaterialDO validateEmergencyMaterialExists(Long id) {
        MesSetEmergencyMaterialDO obj = emergencymaterialMapper.selectById(id);
        if (obj == null) {
            throw exception(SET_EMERGENCY_MATERIAL_NOT_EXISTS);
        }
        return obj;
    }

    /**
     * 基础校验：编号唯一(改单时排除自身)、物资类型枚举
     */
    private void validateBase(MesSetEmergencyMaterialSaveReqVO reqVO, String origin) {
        MesSetEmergencyMaterialDO exist = emergencymaterialMapper.selectByMaterialNo(reqVO.getMaterialNo());
        if (exist != null && !exist.getMaterialNo().equals(origin)) {
            throw exception(SET_EMERGENCY_MATERIAL_NO_DUPLICATE);
        }
        if (reqVO.getMaterialType() != null && !MATERIAL_TYPES.contains(reqVO.getMaterialType())) {
            throw exception(SET_EMERGENCY_MATERIAL_TYPE_INVALID);
        }
    }

    /**
     * 状态现算，覆盖表单值：缺货 &gt; 过期 &gt; 临期 &gt; 正常。
     * **缺货排在过期前面**——灭火器过期但还有 3 只，比"没有灭火器"强，先喊最要命的。
     * 没填有效期就没有临期一说（不是"永不过期"，是"没登记"），不猜。
     */
    private void applyStatus(MesSetEmergencyMaterialDO obj) {
        if (obj.getQuantity() == null || obj.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            obj.setStatus(STATUS_OUT);
            return;
        }
        LocalDate expire = obj.getExpireDate();
        if (expire == null) {
            obj.setStatus(STATUS_NORMAL);
            return;
        }
        LocalDate today = LocalDate.now();
        if (expire.isBefore(today)) {
            obj.setStatus(STATUS_EXPIRED);
        } else if (!expire.isAfter(today.plusDays(EXPIRING_DAYS))) {
            obj.setStatus(STATUS_EXPIRING);
        } else {
            obj.setStatus(STATUS_NORMAL);
        }
    }

}
