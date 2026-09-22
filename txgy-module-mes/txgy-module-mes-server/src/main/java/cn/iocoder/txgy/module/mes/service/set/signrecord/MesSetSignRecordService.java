package cn.iocoder.txgy.module.mes.service.set.signrecord;

import cn.iocoder.txgy.framework.common.exception.ErrorCode;
import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.signrecord.vo.MesSetSignRecordPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.signrecord.vo.MesSetSignRecordSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.signrecord.MesSetSignRecordDO;

/**
 * MES 安全环保检测-签字记录 Service 接口
 *
 * 签字记录只增不改删：仅由复核/审批等流程内部追加(createSignRecord)，对前端只读 page。
 *
 * @author OPENLAB BS
 */
public interface MesSetSignRecordService {

    /**
     * 追加签字记录（内部调用：复核签字落档，须在调用方事务内）
     *
     * @param record 签字（biz_type/biz_no/sign_role 等须由调用方组装）
     * @return 编号
     */
    Long createSignRecord(MesSetSignRecordDO record);

    /**
     * 手工追加签字（追溯页「选择签字人并签字」）。
     *
     * 与 createSignRecord 的区别：这条来自人对前端的一次点击，故在这里补齐签字时间、
     * 按账号现查签字人昵称（不信任前端传的名字）、并另记实际操作账号（可代签，代签要查得出）。
     *
     * @param reqVO 签字请求
     * @return 编号
     */
    Long createManualSign(MesSetSignRecordSaveReqVO reqVO);

    /**
     * 获得签字记录分页
     *
     * @param pageReqVO 分页查询
     * @return 签字记录分页
     */
    PageResult<MesSetSignRecordDO> getSignRecordPage(MesSetSignRecordPageReqVO pageReqVO);

    /**
     * 人为改动环保数据的签字卡口：签名缺失直接抛异常拒掉，签了则落一条签字记录。
     *
     * 与 {@link #createSignRecord} 的分工：那条是流程内部自动补签（门卫放行、复核收口），签名可空；
     * 这条服务于「人在页面上手点的操作」，签名就是这次操作成立的凭据，空签名 = 操作不成立。
     *
     * 须在调用方事务内调用（{@link #createSignRecord} 只做 insert，不回滚别人的业务写）。
     *
     * @param payload     签字要素
     * @param missingCode 缺签名时抛的错误码（各业务自己的码，前端据此定位到具体动作）
     */
    void requireSigned(SignPayload payload, ErrorCode missingCode);

    /**
     * 该业务单是否已有签字。
     *
     * bizType/bizNo 必须**成对**传：签字表以这两列为索引，且 idx_biz 不是唯一键（同号多次签字是
     * 设计），跨 biz_type 同名会张冠李戴 —— 只传 bizNo 会把危废联单的签字认成判定的。
     *
     * @param bizType 业务类型（如 CHECK / LEDGER）
     * @param bizNo   业务单号
     */
    boolean isSigned(String bizType, String bizNo);

    /**
     * 一次人为操作的签字要素。
     *
     * bizType/bizNo/signRole 一律由服务端按业务键解析后传入 —— 前端不传，免得两边各算一份、算岔了
     * 签字就挂到了错的单子上。做成 record 是因为 5 个相邻的同类型参数在调用处太容易串位，
     * 编译器帮不上忙。
     */
    record SignPayload(String bizType, String bizNo, String signRole, String signImg, String opinion) {
    }

}
