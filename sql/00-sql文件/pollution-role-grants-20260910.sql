-- 需求文档 §5.6 角色权限表落地：IQC/质检员、车间操作员、仓库管理员 三个角色的环保模块授权
-- 背景：60190/60192/60196 三个角色当前 system_role_menu 为空（对应用户登录后看不到任何菜单）
-- 口径（严格照抄需求文档 §5.6 表）：
--   IQC/质检员(60192 质检审核员)  → 采购入库/中间废弃物/成品的 AI 初筛查看与人工复核
--   车间操作员(60190 工序上报员)  → 报工时标记废弃物、登记中间物去向
--   仓库管理员(60196 物资管理员)  → 领用放行复核、污染品入受控环境登记
-- 菜单：34000 安全环保检测(分组) / 34301 污染判定 (34311 查询 34312 新增 34313 复核) / 34370 污染追溯 (34371 查询)
-- 注意：本模块是裸 SQL 授权，绕过 @CacheEvict → 需清 system-server 的 menu_role_ids:*、permission_menu_ids:*

-- IQC / 质检员：看 + 复核 + 批次反查追溯
INSERT IGNORE INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
 (60192, 34000, 'sql', NOW(), 'sql', NOW(), 0, 2010),
 (60192, 34301, 'sql', NOW(), 'sql', NOW(), 0, 2010),
 (60192, 34311, 'sql', NOW(), 'sql', NOW(), 0, 2010),
 (60192, 34313, 'sql', NOW(), 'sql', NOW(), 0, 2010),
 (60192, 34370, 'sql', NOW(), 'sql', NOW(), 0, 2010),
 (60192, 34371, 'sql', NOW(), 'sql', NOW(), 0, 2010);

-- 车间操作员：看 + 新建判定（报工时登记中间物去向/标记废弃物），不给复核（终态裁决不归车间）
INSERT IGNORE INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
 (60190, 34000, 'sql', NOW(), 'sql', NOW(), 0, 2010),
 (60190, 34301, 'sql', NOW(), 'sql', NOW(), 0, 2010),
 (60190, 34311, 'sql', NOW(), 'sql', NOW(), 0, 2010),
 (60190, 34312, 'sql', NOW(), 'sql', NOW(), 0, 2010);

-- 仓库管理员：领用放行复核 + 污染品入受控环境登记，台账只读（台账查询走 mes:set-pollution-check:query）
INSERT IGNORE INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
 (60196, 34000, 'sql', NOW(), 'sql', NOW(), 0, 2010),
 (60196, 34301, 'sql', NOW(), 'sql', NOW(), 0, 2010),
 (60196, 34311, 'sql', NOW(), 'sql', NOW(), 0, 2010),
 (60196, 34313, 'sql', NOW(), 'sql', NOW(), 0, 2010),
 (60196, 34370, 'sql', NOW(), 'sql', NOW(), 0, 2010),
 (60196, 34371, 'sql', NOW(), 'sql', NOW(), 0, 2010);

-- 校验
SELECT r.id, r.name, COUNT(*) AS menu_cnt
FROM system_role r JOIN system_role_menu rm ON rm.role_id = r.id
WHERE r.id IN (60190, 60192, 60196) GROUP BY r.id, r.name ORDER BY r.id;

SELECT rm.role_id, m.permission FROM system_role_menu rm JOIN system_menu m ON m.id = rm.menu_id
WHERE rm.role_id IN (60190, 60192, 60196) AND m.permission IS NOT NULL ORDER BY rm.role_id, m.permission;
