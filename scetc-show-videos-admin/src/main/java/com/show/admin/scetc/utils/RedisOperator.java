package com.show.admin.scetc.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @Description: 使用redisTemplate的操作实现类
 */
@Component
public class RedisOperator {

	@Autowired(required = false)
	private StringRedisTemplate redisTemplate;
	
	// 内存存储作为Redis的后备
	private Map<String, String> memoryStore = new ConcurrentHashMap<>();
	private Map<String, List<String>> memoryListStore = new ConcurrentHashMap<>();
	private Map<String, Map<Object, Object>> memoryHashStore = new ConcurrentHashMap<>();

	// Key（键），简单的key-value操作
	/**
	 * 实现命令：TTL key，以秒为单位，返回给定 key的剩余生存时间(TTL, time to live)。
	 * 
	 * @param key
	 * @return
	 */
	public long ttl(String key) {
		try {
			if (redisTemplate != null) {
				return redisTemplate.getExpire(key);
			}
		} catch (Exception e) {
			// Redis连接失败，使用内存存储
		}
		return -1;
	}

	/**
	 * 返回list数组
	 * 
	 * @param key
	 * @return
	 */
	public List<String> range(String key) {
		try {
			if (redisTemplate != null) {
				return redisTemplate.opsForList().range(key, 0, -1);
			}
		} catch (Exception e) {
			// Redis连接失败，使用内存存储
		}
		return memoryListStore.getOrDefault(key, new ArrayList<>());
	}

	/**
	 * 实现命令：expire 设置过期时间，单位秒
	 * 
	 * @param key
	 * @return
	 */
	public void expire(String key, long timeout) {
		try {
			if (redisTemplate != null) {
				redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
			}
		} catch (Exception e) {
			// Redis连接失败，忽略
		}
	}

	/**
	 * 实现命令：INCR key，增加key一次
	 * 
	 * @param key
	 * @return
	 */
	public long incr(String key, long delta) {
		try {
			if (redisTemplate != null) {
				return redisTemplate.opsForValue().increment(key, delta);
			}
		} catch (Exception e) {
			// Redis连接失败，使用内存存储
			String value = memoryStore.get(key);
			long current = value == null ? 0 : Long.parseLong(value);
			current += delta;
			memoryStore.put(key, String.valueOf(current));
			return current;
		}
		return 0;
	}

	/**
	 * 实现命令：KEYS pattern，查找所有符合给定模式 pattern的 key
	 */
	public Set<String> keys(String pattern) {
		try {
			if (redisTemplate != null) {
				return redisTemplate.keys(pattern);
			}
		} catch (Exception e) {
			// Redis连接失败，使用内存存储
		}
		return memoryStore.keySet();
	}

	/**
	 * 实现命令：DEL key，删除一个key
	 * 
	 * @param key
	 */
	public void del(String key) {
		try {
			if (redisTemplate != null) {
				redisTemplate.delete(key);
			}
		} catch (Exception e) {
			// Redis连接失败，使用内存存储
		}
		memoryStore.remove(key);
		memoryListStore.remove(key);
		memoryHashStore.remove(key);
	}

	// String（字符串）

	/**
	 * 实现命令：SET key value，设置一个key-value（将字符串值 value关联到 key）
	 * 
	 * @param key
	 * @param value
	 */
	public void set(String key, String value) {
		try {
			if (redisTemplate != null) {
				redisTemplate.opsForValue().set(key, value);
			}
		} catch (Exception e) {
			// Redis连接失败，使用内存存储
		}
		memoryStore.put(key, value);
	}

	/**
	 * 实现命令：SET key value EX seconds，设置key-value和超时时间（秒）
	 * 
	 * @param key
	 * @param value
	 * @param timeout （以秒为单位）
	 */
	public void set(String key, String value, long timeout) {
		try {
			if (redisTemplate != null) {
				redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
			}
		} catch (Exception e) {
			// Redis连接失败，使用内存存储
		}
		memoryStore.put(key, value);
	}

	/**
	 * 实现命令：GET key，返回 key所关联的字符串值。
	 * 
	 * @param key
	 * @return value
	 */
	public String get(String key) {
		try {
			if (redisTemplate != null) {
				return redisTemplate.opsForValue().get(key);
			}
		} catch (Exception e) {
			// Redis连接失败，使用内存存储
		}
		return memoryStore.get(key);
	}

	// Hash（哈希表）

	/**
	 * 实现命令：HSET key field value，将哈希表 key中的域 field的值设为 value
	 * 
	 * @param key
	 * @param field
	 * @param value
	 */
	public void hset(String key, String field, Object value) {
		try {
			if (redisTemplate != null) {
				redisTemplate.opsForHash().put(key, field, value);
			}
		} catch (Exception e) {
			// Redis连接失败，使用内存存储
		}
		memoryHashStore.computeIfAbsent(key, k -> new HashMap<>()).put(field, value);
	}

	/**
	 * 实现命令：HGET key field，返回哈希表 key中给定域 field的值
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public String hget(String key, String field) {
		try {
			if (redisTemplate != null) {
				return (String) redisTemplate.opsForHash().get(key, field);
			}
		} catch (Exception e) {
			// Redis连接失败，使用内存存储
		}
		Map<Object, Object> hash = memoryHashStore.get(key);
		if (hash != null) {
			Object value = hash.get(field);
			return value != null ? value.toString() : null;
		}
		return null;
	}

	/**
	 * 实现命令：HDEL key field [field ...]，删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。
	 * 
	 * @param key
	 * @param fields
	 */
	public void hdel(String key, Object... fields) {
		try {
			if (redisTemplate != null) {
				redisTemplate.opsForHash().delete(key, fields);
			}
		} catch (Exception e) {
			// Redis连接失败，使用内存存储
		}
		Map<Object, Object> hash = memoryHashStore.get(key);
		if (hash != null) {
			for (Object field : fields) {
				hash.remove(field);
			}
		}
	}

	/**
	 * 实现命令：HGETALL key，返回哈希表 key中，所有的域和值。
	 * 
	 * @param key
	 * @return
	 */
	public Map<Object, Object> hgetall(String key) {
		try {
			if (redisTemplate != null) {
				return redisTemplate.opsForHash().entries(key);
			}
		} catch (Exception e) {
			// Redis连接失败，使用内存存储
		}
		return memoryHashStore.getOrDefault(key, new HashMap<>());
	}

	// List（列表）

	/**
	 * 实现命令：LPUSH key value，将一个值 value插入到列表 key的表头
	 * 
	 * @param key
	 * @param value
	 * @return 执行 LPUSH命令后，列表的长度。
	 */
	public long lpush(String key, String value) {
		try {
			if (redisTemplate != null) {
				return redisTemplate.opsForList().leftPush(key, value);
			}
		} catch (Exception e) {
			// Redis连接失败，使用内存存储
		}
		List<String> list = memoryListStore.computeIfAbsent(key, k -> new ArrayList<>());
		list.add(0, value);
		return list.size();
	}

	/**
	 * 实现命令：LPOP key，移除并返回列表 key的头元素。
	 * 
	 * @param key
	 * @return 列表key的头元素。
	 */
	public String lpop(String key) {
		try {
			if (redisTemplate != null) {
				return redisTemplate.opsForList().leftPop(key);
			}
		} catch (Exception e) {
			// Redis连接失败，使用内存存储
		}
		List<String> list = memoryListStore.get(key);
		if (list != null && !list.isEmpty()) {
			return list.remove(0);
		}
		return null;
	}

	/**
	 * 实现命令：RPUSH key value，将一个值 value插入到列表 key的表尾(最右边)。
	 * 
	 * @param key
	 * @param value
	 * @return 执行 LPUSH命令后，列表的长度。
	 */
	public long rpush(String key, String value) {
		try {
			if (redisTemplate != null) {
				return redisTemplate.opsForList().rightPush(key, value);
			}
		} catch (Exception e) {
			// Redis连接失败，使用内存存储
		}
		List<String> list = memoryListStore.computeIfAbsent(key, k -> new ArrayList<>());
		list.add(value);
		return list.size();
	}

}


