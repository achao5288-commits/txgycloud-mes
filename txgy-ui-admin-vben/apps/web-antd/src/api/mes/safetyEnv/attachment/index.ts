import { requestClient } from '#/api/request';

export namespace MesSetAttachmentApi {
  /** 业务附件（文件本体在 infra 文件服务，这里只有地址） */
  export interface Attachment {
    id?: number;
    bizType?: string; // 业务关联类型
    bizNo?: string; // 业务关联单号
    fileName?: string; // 原始文件名
    fileUrl?: string; // 访问地址
    remark?: string;
    createTime?: number; // epoch 毫秒
  }
}

/** 登记附件（文件先经 /infra/file/upload 上传，这里只存地址） */
export function createAttachment(data: {
  bizType: string;
  bizNo: string;
  fileName: string;
  fileUrl: string;
  remark?: string;
}) {
  return requestClient.post<number>('/mes/safety-env/attachment/create', data);
}

/** 按业务键取附件列表 */
export function getAttachmentList(bizType: string, bizNo: string) {
  return requestClient.get<MesSetAttachmentApi.Attachment[]>(
    '/mes/safety-env/attachment/list',
    { params: { bizType, bizNo } },
  );
}

/** 删除附件（逻辑删） */
export function deleteAttachment(id: number) {
  return requestClient.delete(
    `/mes/safety-env/attachment/delete?id=${id}`,
  );
}
