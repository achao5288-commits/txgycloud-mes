package cn.iocoder.txgy.module.mes.service.set.hazwaste;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.hazwaste.vo.*;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.hazwaste.MesSetHazardousWasteDO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.hazwaste.MesSetHazwasteManifestDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * MES 安全环保检测-危废链 Service 接口
 *
 * 台账（mes_set_hazardous_waste，环节只进不退） + 国家固废转移联单五方（mes_set_hazwaste_manifest）
 * + 门卫硬放行（无生效联单不放行） + HJ1276 标签。
 *
 * @author OPENLAB BS
 */
public interface MesSetHazwasteService {

    /**
     * 危废台账环节，按数组下标定序，只允许向后推进。
     */
    String[] STAGES = {"GENERATED", "STORED", "TRANSFERRED", "DISPOSED"};

    /**
     * 联单状态，按数组下标定序，只允许向后推进。
     */
    String[] MANIFEST_STATUSES = {"DRAFT", "DECLARED", "EFFECTIVE", "TRANSFERRED", "CLOSED"};

    /**
     * 会签业务类型（复用 mes_set_sign_record）
     */
    String SIGN_BIZ_TYPE = "HAZWASTE_MANIFEST";

    /**
     * 门卫放行前必须已签的角色（门卫本人的 GUARD 由放行动作自动补签）
     */
    String[] REQUIRED_SIGN_ROLES = {"HANDOVER", "DRIVER", "RECEIVER"};

    /**
     * 会签角色：HANDOVER(移交环保员)/DRIVER(押运司机)/RECEIVER(接收经手人)/GUARD(门卫)
     */
    String[] SIGN_ROLES = {"HANDOVER", "DRIVER", "RECEIVER", "GUARD"};

    /**
     * 五方角色：GENERATOR/CARRIER/RECEIVER_PARTY/STORAGE/DISPOSER
     */
    String[] PARTY_ROLES = {"GENERATOR", "CARRIER", "RECEIVER_PARTY", "STORAGE", "DISPOSER"};

    // ==================== 危废台账 ====================

    Long createHazardousWaste(@Valid MesSetHazardousWasteSaveReqVO createReqVO);

    void updateHazardousWaste(@Valid MesSetHazardousWasteSaveReqVO updateReqVO);

    void deleteHazardousWaste(Long id);

    MesSetHazardousWasteDO getHazardousWaste(Long id);

    PageResult<MesSetHazardousWasteDO> getHazardousWastePage(@Valid MesSetHazardousWastePageReqVO pageReqVO);

    /**
     * 推进台账环节（只能向后：产生→贮存→转移→处置），回退或不合法环节抛 1040829_003/004。
     *
     * @param id    台账编号
     * @param stage 目标环节
     * @param handler 经办人（留空取当前登录人昵称）
     */
    void advanceStage(Long id, String stage, String handler);

    // ==================== 危废转移联单（国家固废五方） ====================

    Long createManifest(@Valid MesSetHazwasteManifestSaveReqVO createReqVO);

    void updateManifest(@Valid MesSetHazwasteManifestSaveReqVO updateReqVO);

    void deleteManifest(Long id);

    MesSetHazwasteManifestDO getManifest(Long id);

    MesSetHazwasteManifestDO getManifestByNo(String manifestNo);

    PageResult<MesSetHazwasteManifestDO> getManifestPage(@Valid MesSetHazwasteManifestPageReqVO pageReqVO);

    /**
     * 产生方申报：DRAFT → DECLARED，记录申报时间。
     */
    void declareManifest(String manifestNo);

    /**
     * 五方之一确认。运输方与接收方都确认后，联单自动转 EFFECTIVE(已生效)。
     *
     * @param manifestNo 联单号
     * @param partyRole  GENERATOR/CARRIER/RECEIVER_PARTY/STORAGE/DISPOSER
     */
    void confirmParty(String manifestNo, String partyRole);

    /**
     * 回执归档：TRANSFERRED → CLOSED（危废出厂后收到接收方回执，留档 ≥5 年）。
     */
    void closeManifest(String manifestNo);

    /**
     * 四方会签（复用 mes_set_sign_record，只增不改删）。
     *
     * @param manifestNo 联单号
     * @param signRole   HANDOVER/DRIVER/RECEIVER/GUARD
     * @param opinion    签署意见
     * @param signImg    手写签名图片地址（可空）
     */
    void signManifest(String manifestNo, String signRole, String opinion, String signImg);

    /**
     * 国家平台倒排提醒待办：已申报未生效、且申报时限不晚于 now+days 天的联单（含已逾期）。
     */
    List<MesSetHazwasteManifestDO> listDueSoon(int days);

    // ==================== 门卫硬放行 ====================

    /**
     * 出厂校验（只读，可反复试）：联单存在 + 已生效 + 车牌一致 + 三方签字齐，四项缺一不放行。
     */
    MesSetHazwasteGateCheckRespVO checkGate(@Valid MesSetHazwasteGateCheckReqVO reqVO);

    /**
     * 门卫放行（硬）：重跑校验，任一不过按具体原因抛 1040829_007~010；
     * 通过则补签 GUARD、联单转 TRANSFERRED、同单号台账环节推至 TRANSFERRED。
     */
    MesSetHazwasteGateCheckRespVO releaseGate(@Valid MesSetHazwasteGateCheckReqVO reqVO);

    // ==================== HJ1276 标签 ====================

    /**
     * 取该容器/台账的 HJ1276 标签字段集（前端按国标模板渲染打印）。
     */
    MesSetHazwasteLabelRespVO getLabel(Long id);

    /**
     * 把标签字段集落成文件存入文件服务，URL 写入 label_url 并返回（换签/补印留痕）。
     */
    String archiveLabel(Long id);

}
