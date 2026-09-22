package cn.iocoder.txgy.module.mes.dal.mysql.set.pollutioncheck;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.controller.admin.set.pollutioncheck.vo.MesSetPollutionCheckPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.pollutioncheck.MesSetPollutionCheckDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * MES 安全环保检测-污染判定记录 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetPollutionCheckMapper extends BaseMapperX<MesSetPollutionCheckDO> {

    default PageResult<MesSetPollutionCheckDO> selectPage(MesSetPollutionCheckPageReqVO reqVO) {
        LambdaQueryWrapperX<MesSetPollutionCheckDO> query = new LambdaQueryWrapperX<MesSetPollutionCheckDO>()
                .eqIfPresent(MesSetPollutionCheckDO::getStage, reqVO.getStage())
                .eqIfPresent(MesSetPollutionCheckDO::getBizNo, reqVO.getBizNo())
                .eqIfPresent(MesSetPollutionCheckDO::getBatchId, reqVO.getBatchId())
                .eqIfPresent(MesSetPollutionCheckDO::getBatchNo, reqVO.getBatchNo())
                .eqIfPresent(MesSetPollutionCheckDO::getAiResult, reqVO.getAiResult())
                .eqIfPresent(MesSetPollutionCheckDO::getReviewResult, reqVO.getReviewResult())
                .eqIfPresent(MesSetPollutionCheckDO::getFinishedResult, reqVO.getFinishedResult())
                .eqIfPresent(MesSetPollutionCheckDO::getReviewBy, reqVO.getReviewBy())
                .betweenIfPresent(MesSetPollutionCheckDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MesSetPollutionCheckDO::getId);
        // 物料名称模糊
        if (reqVO.getItemName() != null && !reqVO.getItemName().isEmpty()) {
            query.like(MesSetPollutionCheckDO::getItemName, reqVO.getItemName());
        }
        // 判定状态过滤：null=全部，true=已复核，false=待复核
        if (reqVO.getReviewed() != null) {
            if (reqVO.getReviewed()) {
                query.isNotNull(MesSetPollutionCheckDO::getReviewResult);
            } else {
                query.isNull(MesSetPollutionCheckDO::getReviewResult);
            }
        }
        // 批次关联状态：null=全部，true=已锚定批次，false=历史手工录入的未关联行（实测占 46%）
        if (reqVO.getLinked() != null) {
            if (reqVO.getLinked()) {
                query.isNotNull(MesSetPollutionCheckDO::getBatchId);
            } else {
                query.isNull(MesSetPollutionCheckDO::getBatchId);
            }
        }
        return selectPage(reqVO, query);
    }

    /**
     * 按批次取该批全部判定的记录号（批次全链追溯用，去重）
     */
    default List<String> selectRecordNoListByBatchNo(String batchNo) {
        return selectList(new LambdaQueryWrapperX<MesSetPollutionCheckDO>()
                .eq(MesSetPollutionCheckDO::getBatchNo, batchNo))
                .stream()
                .map(MesSetPollutionCheckDO::getRecordNo)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

}
