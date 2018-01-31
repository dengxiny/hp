package com.hp.redis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.ShardedJedisPool;

/**
 * 
 */
@Component
public class RedisCache {
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private RedisConfig redisConfig;
	
	// 数据源
	private ShardedJedisPool shardedJedisPool;
	
	@PostConstruct
	private void initPool(){
		
		JedisPoolConfig config = new JedisPoolConfig();
		//连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
		config.setBlockWhenExhausted(true);
		//设置的逐出策略类名, 默认DefaultEvictionPolicy(当连接超过最大空闲时间,或连接数超过最大空闲连接数)
		config.setEvictionPolicyClassName("org.apache.commons.pool2.impl.DefaultEvictionPolicy");
		//是否启用pool的jmx管理功能, 默认true
		config.setJmxEnabled(true);
		//MBean ObjectName = new ObjectName("org.apache.commons.pool2:type=GenericObjectPool,name=" + "pool" + i); 默 认为"pool", JMX不熟,具体不知道是干啥的...默认就好.
		config.setJmxNamePrefix("pool");
		//是否启用后进先出, 默认true
		config.setLifo(true);
		//最大空闲连接数, 默认8个
		config.setMaxIdle(redisConfig.getMaxIdle());
		//最大连接数, 默认8个
		config.setMaxTotal(1000);
		//获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
		config.setMaxWaitMillis(-1);
		//逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
		config.setMinEvictableIdleTimeMillis(1800000);	 
		//最小空闲连接数, 默认0
		config.setMinIdle(0);
		//每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
		config.setNumTestsPerEvictionRun(3);
		//对象空闲多久后逐出, 当空闲时间>该值 且 空闲连接>最大空闲数 时直接逐出,不再根据MinEvictableIdleTimeMillis判断  (默认逐出策略)   
		config.setSoftMinEvictableIdleTimeMillis(1800000);
		//在获取连接的时候检查有效性, 默认false
		config.setTestOnBorrow(true);
		//在空闲时检查有效性, 默认false
		config.setTestWhileIdle(true);
		//逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
		config.setTimeBetweenEvictionRunsMillis(-1);
		
		JedisShardInfo jedisShardInfo = new JedisShardInfo(redisConfig.getHost(), redisConfig.getPort());
		List<JedisShardInfo> shardInfoList = new ArrayList<JedisShardInfo>();
		shardInfoList.add(jedisShardInfo);
		
		shardedJedisPool = new ShardedJedisPool(config, shardInfoList);
	}
	
	/**
	 * 它保证在执行操作之后释放数据源returnResource(jedis)
	 *
	 */

	/**
	 * 删除模糊匹配的key
	 *
	 * @param likeKey
	 *            模糊匹配的key
	 * @return 删除成功的条数
	 */
	public long delKeysLike(final String likeKey) {
		try {
			return new Executor<Long>(shardedJedisPool) {
				@Override
				Long execute() {
					Collection<Jedis> jedisC = jedis.getAllShards();
					Iterator<Jedis> iter = jedisC.iterator();
					long count = 0;
					while (iter.hasNext()) {
						Jedis _jedis = iter.next();
						Set<String> keys = _jedis.keys(likeKey + "*");
						count += _jedis.del(keys.toArray(new String[keys.size()]));
					}
					return count;
				}
			}.getResult();
		} catch (Exception e) {
			logger.error("", e);
			return -1;
		}

	}

	/**
	 * 删除
	 *
	 * @param key
	 *            匹配的key
	 * @return 删除成功的条数
	 */
	public Long delKey(final String key) {
		try {
			return new Executor<Long>(shardedJedisPool) {
				@Override
				Long execute() {
					return jedis.del(key);
				}
			}.getResult();
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}

	}

	/**
	 * 删除
	 *
	 * @param keys
	 *            匹配的key的集合
	 * @return 删除成功的条数
	 */
	public Long delKeys(final String[] keys) {
		try {
			return new Executor<Long>(shardedJedisPool) {
				@Override
				Long execute() {
					Collection<Jedis> jedisC = jedis.getAllShards();
					Iterator<Jedis> iter = jedisC.iterator();
					long count = 0;
					while (iter.hasNext()) {
						Jedis _jedis = iter.next();
						count += _jedis.del(keys);
					}
					return count;
				}
			}.getResult();
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}

	/**
	 * 为给定 key 设置生存时间，当 key 过期时(生存时间为 0 )，它会被自动删除。 在 Redis 中，带有生存时间的 key
	 * 被称为『可挥发』(volatile)的。
	 *
	 * @param key
	 *            key
	 * @param expire
	 *            生命周期，单位为秒
	 * @return 1: 设置成功 0: 已经超时或key不存在
	 */
	public Long expire(final String key, final int expire) {
		try {
			return new Executor<Long>(shardedJedisPool) {
				@Override
				Long execute() {
					return jedis.expire(key, expire);
				}
			}.getResult();
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}

	/**
	 * 一个跨jvm的id生成器，利用了redis原子性操作的特点
	 *
	 * @param key
	 *            id的key
	 * @return 返回生成的Id
	 */
	public Long makeId(final String key) {
		try {
			return new Executor<Long>(shardedJedisPool) {
				@Override
				Long execute() {
					long id = jedis.incr(key);
					if ((id + 75807) >= Long.MAX_VALUE) {
						// 避免溢出，重置，getSet命令之前允许incr插队，75807就是预留的插队空间
						jedis.getSet(key, "0");
					}
					return id;
				}
			}.getResult();
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}

	}

	/**
	 * 将字符串值 value 关联到 key 。 如果 key 已经持有其他值， setString 就覆写旧值，无视类型。
	 * 对于某个原本带有生存时间（TTL）的键来说， 当 setString 成功在这个键上执行时， 这个键原有的 TTL 将被清除。
	 * 时间复杂度：O(1)
	 *
	 * @param key
	 *            key
	 * @param value
	 *            string value
	 * @return 在设置操作成功完成时，才返回 OK 。
	 */
	public String setString(final String key, final String value) {
		try {
			return new Executor<String>(shardedJedisPool) {
				@Override
				String execute() {
					return jedis.set(key, value);
				}
			}.getResult();
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}

	}

	/*
	 * ======================================Strings============================
	 * ==========
	 */

	/**
	 * 将值 value 关联到 key ，并将 key 的生存时间设为 expire (以秒为单位)。 如果 key 已经存在， 将覆写旧值。
	 * 类似于以下两个命令: SET key value EXPIRE key expire # 设置生存时间
	 * 不同之处是这个方法是一个原子性(atomic)操作，关联值和设置生存时间两个动作会在同一时间内完成，在 Redis 用作缓存时，非常实用。
	 * 时间复杂度：O(1)
	 *
	 * @param key
	 *            key
	 * @param value
	 *            string value
	 * @param expire
	 *            生命周期
	 * @return 设置成功时返回 OK 。当 expire 参数不合法时，返回一个错误。
	 */
	public String setString(final String key, final String value, final int expire) {
		try {
			return new Executor<String>(shardedJedisPool) {
				@Override
				String execute() {
					return jedis.setex(key, expire, value);
				}
			}.getResult();
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}

	}

	/**
	 * 将 key 的值设为 value ，当且仅当 key 不存在。若给定的 key 已经存在，则 setStringIfNotExists
	 * 不做任何动作。 时间复杂度：O(1)
	 *
	 * @param key
	 *            key
	 * @param value
	 *            string value
	 * @return 设置成功，返回 1 。设置失败，返回 0 。
	 */
	public Long setStringIfNotExists(final String key, final String value) {
		try {
			return new Executor<Long>(shardedJedisPool) {
				@Override
				Long execute() {
					return jedis.setnx(key, value);
				}
			}.getResult();
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}

	}

	/**
	 * 返回 key 所关联的字符串值。如果 key 不存在那么返回特殊值 nil 。 假如 key 储存的值不是字符串类型，返回一个错误，因为
	 * getString 只能用于处理字符串值。 时间复杂度: O(1)
	 *
	 * @param key
	 *            key
	 * @return 当 key 不存在时，返回 nil ，否则，返回 key 的值。如果 key 不是字符串类型，那么返回一个错误。
	 */
	public String getString(final String key) {
		try {
			return new Executor<String>(shardedJedisPool) {
				@Override
				String execute() {
					return jedis.get(key);
				}
			}.getResult();
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}

	}

	/**
	 * 批量的 {@link #setString(String, String)}
	 *
	 * @param pairs
	 *            键值对数组{数组第一个元素为key，第二个元素为value}
	 * @return 操作状态的集合
	 */
	public List<Object> batchSetString(final List<Pair<String, String>> pairs) {
		try {
			return new Executor<List<Object>>(shardedJedisPool) {
				@Override
				List<Object> execute() {
					ShardedJedisPipeline pipeline = jedis.pipelined();
					for (Pair<String, String> pair : pairs) {
						pipeline.set(pair.getKey(), pair.getValue());
					}
					return pipeline.syncAndReturnAll();
				}
			}.getResult();
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}

	}

	/**
	 * 批量的 {@link #getString(String)}
	 *
	 * @param keys
	 *            key数组
	 * @return value的集合
	 */
	public List<String> batchGetString(final String[] keys) {
		try {
			return new Executor<List<String>>(shardedJedisPool) {
				@Override
				List<String> execute() {
					ShardedJedisPipeline pipeline = jedis.pipelined();
					List<String> result = new ArrayList<String>(keys.length);
					List<Response<String>> responses = new ArrayList<Response<String>>(keys.length);
					for (String key : keys) {
						responses.add(pipeline.get(key));
					}
					pipeline.sync();
					for (Response<String> resp : responses) {
						result.add(resp.get());
					}
					return result;
				}
			}.getResult();
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}

	}

	public Long addNumRed(final String key, final Long changeNum) {
		return new Executor<Long>(shardedJedisPool) {
			@Override
			Long execute() {
				Long temp = changeNum.longValue();
				return jedis.incrBy(key, temp);
			}
		}.getResult();
	}

	public Long reduceNumRed(final String key, final Long changeNum) {
		return new Executor<Long>(shardedJedisPool) {
			@Override
			Long execute() {
				return jedis.decrBy(key, changeNum.longValue());
			}
		}.getResult();
	}

	/*
	 * ======================================List===============================
	 * =======
	 */

	/**
	 * 将一个或多个值 value 插入到列表 key 的表头
	 *
	 * @param key
	 *            key
	 * @param value
	 *            string value
	 * @return 执行 listPushHead 命令后，列表的长度。
	 */
	public Long listPushHead(final String key, final String value) {
		return new Executor<Long>(shardedJedisPool) {

			@Override
			Long execute() {
				return jedis.lpush(key, value);
			}
		}.getResult();
	}

	/**
	 * 将一个或多个值 value 插入到列表 key 的表头, 当列表大于指定长度是就对列表进行修剪(trim)
	 *
	 * @param key
	 *            key
	 * @param value
	 *            string value
	 * @param size
	 *            链表超过这个长度就修剪元素
	 * @return 执行 listPushHeadAndTrim 命令后，列表的长度。
	 */
	public Long listPushHeadAndTrim(final String key, final String value, final long size) {
		return new Executor<Long>(shardedJedisPool) {

			@Override
			Long execute() {
				Pipeline pipeline = jedis.getShard(key).pipelined();
				Response<Long> result = pipeline.lpush(key, value);
				// 修剪列表元素, 如果 size - 1 比 end 下标还要大，Redis将 size 的值设置为 end 。
				pipeline.ltrim(key, 0, size - 1);
				pipeline.sync();
				return result.get();
			}
		}.getResult();
	}

	abstract class Executor<T> {

		ShardedJedis jedis;
		ShardedJedisPool shardedJedisPool;

		public Executor(ShardedJedisPool shardedJedisPool) {
			this.shardedJedisPool = shardedJedisPool;
			jedis = this.shardedJedisPool.getResource();
		}

		/**
		 * 回调
		 *
		 * @return 执行结果
		 */
		abstract T execute();

		/**
		 * 调用{@link #execute()}并返回执行结果
		 * 它保证在执行{@link #execute()}之后释放数据源returnResource(jedis)
		 *
		 * @return 执行结果
		 */
		public T getResult() {
			T result = null;
			try {
				result = execute();
			} catch (Throwable e) {
				throw new RuntimeException("Redis execute exception", e);
			} finally {
				if (jedis != null) {
					shardedJedisPool.returnResource(jedis);
				}
			}
			return result;
		}
	}

	/**
	 * 键值对
	 *
	 * @param <K>
	 *            key
	 * @param <V>
	 *            value
	 * @author fengjc
	 * @version V1.0
	 */
	public class Pair<K, V> {

		private K key;
		private V value;

		public Pair(K key, V value) {
			this.key = key;
			this.value = value;
		}

		public K getKey() {
			return key;
		}

		public void setKey(K key) {
			this.key = key;
		}

		public V getValue() {
			return value;
		}

		public void setValue(V value) {
			this.value = value;
		}
	}
}
