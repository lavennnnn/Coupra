package cn.hush.Coupra.merchant.admin.dao.sharding;

import lombok.Getter;
import org.apache.shardingsphere.infra.util.exception.ShardingSpherePreconditions;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;
import org.apache.shardingsphere.sharding.exception.algorithm.sharding.ShardingAlgorithmClassImplementationException;
import org.apache.shardingsphere.sharding.exception.algorithm.sharding.ShardingAlgorithmInitializationException;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * 基于 HashMod 方式自定义分库算法
 */
public final class DBHashModShardingAlgorithm implements StandardShardingAlgorithm<Long> {

    //私有成员变量
        //properties
    @Getter
    private Properties props;
        //分片数量
    private int shardingCount;

    private static final String SHARDING_COUNT_KEY = "sharding-count";

    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> shardingValue) {
        // 分片键值，也就是商家店铺编号
        Long id = shardingValue.getValue();
        // 一共有多少个真实的数据库，咱们就两个 ds_0、ds_1
        int dbSize = availableTargetNames.size();
        // 取模
        int mod = (int) hashShardingValue(id) % shardingCount / (shardingCount / dbSize);
        int index = 0;
        for (String targetName : availableTargetNames) {
            if (index == mod) {
                return targetName;
            }
            index++;
        }
        throw new IllegalArgumentException("No target found for value: " + id);
    }

    @Override
    public Collection<String> doSharding(Collection<String> collection, RangeShardingValue<Long> rangeShardingValue) {
        // 暂无范围分片场景，默认返回空
        return List.of();
    }

    @Override
    public void init(Properties props) {
        this.props = props;
        shardingCount = getShardingCount(props);
    }

    private int getShardingCount(final Properties props) {
        ShardingSpherePreconditions.checkState(props.containsKey(SHARDING_COUNT_KEY),
                () -> new ShardingAlgorithmInitializationException(getType(), "Sharding count cannot be null."));
        return Integer.parseInt(props.getProperty(SHARDING_COUNT_KEY));
    }

    private long hashShardingValue(final Comparable<?> shardingValue) {
        return Math.abs((long) shardingValue.hashCode());
    }
}


