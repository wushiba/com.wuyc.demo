--[[
    功能：限流脚本。如果返回值为0表示该IP超过限制；非0则没有超过限制
    入参：
            KEYS[1]     IP地址
            ARGV[1]     IP限流的阈值
            ARGV[2]     IP访问记录的有效期
    返回值：
            0   该IP超过限制
            1   该IP没超限制
--]]

-- 这个ip是什么
local ip = tostring(KEYS[1])
-- 这个是限制的次数
local limit = tonumber(ARGV[1])
-- 这个是过期时间
local expire_time = tonumber(ARGV[2])

-- 这个是ip访问次数记录key，例如：visitor_ip_count_127.0.0.1
local visitorCountKey = "visitor_ip_count_"..ip
-- 这个ip触发几次限流的key
local triggerCountKey = "trigger_limit_count_"..ip
-- 这个IP是否是黑名单IP的key
local blackListIpKey = "black_list_ip_"..ip

-- 判断是否是黑名单Ip，黑名单IP直接拒绝服务
if redis.call("EXISTS", blackListIpKey) == 1 then
    return 0
end

-- 判断这个ip是否存在
local is_exists = redis.call("EXISTS", visitorCountKey)

if is_exists == 1 then
   -- 存在且不是黑名单IP，则对次数+1操作，然后判断是否超过限制
   if redis.call("INCR", visitorCountKey) > limit then
       -- 重置过期时间，越刷越长
       expire_time = expire_time + tonumber(redis.call("TTL", visitorCountKey))
       redis.call("EXPIRE", visitorCountKey, expire_time)

       -- 记录这个ip是第几次触发限制
       if redis.call("EXISTS", triggerCountKey) == 0 then
           -- 有效期5分钟
           redis.call("SET", triggerCountKey, 1, "EX", 300)
       else
           -- 如果触发限制大于一定次数加入黑名单
           if  redis.call("INCR", triggerCountKey) > 500 then
               -- 这个ip触发次数太多，加入黑名单，有效期1小时
               redis.call("SET", blackListIpKey, 1, "EX", 3600)
           end
       end

       -- 禁止访问
       return 0
   else
       -- 没有超过限制，可以访问
       return 1
   end
else
    -- 不存在说明这个ip是第一次访问，则保存并设置过期时间
    redis.call("SET", visitorCountKey, 1, "EX", expire_time)
    return 1
end