package com.github.phonenumbermanager.listener;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.github.phonenumbermanager.constant.SystemConstant;
import com.github.phonenumbermanager.constant.enums.ImportOrExportStatusEnum;
import com.github.phonenumbermanager.entity.Company;
import com.github.phonenumbermanager.entity.PhoneNumber;
import com.github.phonenumbermanager.entity.Subcontractor;
import com.github.phonenumbermanager.util.RedisUtil;

/**
 * 导入读取监听器
 *
 * @author 廿二月的天
 */
public abstract class ImportReadListener<T> implements ReadListener<T> {
    private static final int BATCH_COUNT = 10 * 10;
    protected List<T> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    private final RedisUtil redisUtil;
    private final Long importId;
    protected final List<Company> companyAll;
    protected List<Subcontractor> subcontractorAll;
    protected List<PhoneNumber> phoneNumberAll;

    public ImportReadListener(RedisUtil redisUtil, Long importId, List<Company> companyAll,
        List<Subcontractor> subcontractorAll, List<PhoneNumber> phoneNumberAll) {
        this.redisUtil = redisUtil;
        this.importId = importId;
        this.companyAll = companyAll;
        this.subcontractorAll = subcontractorAll;
        this.phoneNumberAll = phoneNumberAll;
        redisUtil.setEx(SystemConstant.EXPORT_ID_KEY + SystemConstant.REDIS_EXPLODE + importId,
            ImportOrExportStatusEnum.HANDLING.getValue(), 20, TimeUnit.MINUTES);
    }

    @Override
    public void invoke(T object, AnalysisContext analysisContext) {
        cachedDataList.add(object);
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();
        redisUtil.setEx(SystemConstant.EXPORT_ID_KEY + SystemConstant.REDIS_EXPLODE + importId,
            ImportOrExportStatusEnum.HANDLED.getValue(), 20, TimeUnit.MINUTES);
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        redisUtil.setEx(SystemConstant.EXPORT_ID_KEY + SystemConstant.REDIS_EXPLODE + importId,
            ImportOrExportStatusEnum.FAILED.getValue(), 20, TimeUnit.MINUTES);
        ReadListener.super.onException(exception, context);
    }

    /**
     * 保存数据
     */
    protected abstract void saveData();
}
