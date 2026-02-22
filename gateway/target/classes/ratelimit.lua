-- KEYS[1] = rate limit key
-- ARGV[1] = max_tokens
-- ARGV[2] = refill_rate_per_sec
-- ARGV[3] = current_time_millis
-- ARGV[4] = ttl_seconds

local key = KEYS[1]
local max_tokens  = tonumber(ARGV[1])
local refill_rate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])
local ttl = tonumber(ARGV[4])

local data = redis.call("HMGET", key, "tokens", "last_refill_ts")
local tokens = tonumber(data[1])
local last_refill = tonumber(data[2])

if tokens == nil then
    tokens = max_tokens
    last_refill = now
end

local elapsed = (now - last_refill) / 1000
local refill = elapsed * refill_rate
tokens = math.min(max_tokens, tokens + refill)

if tokens < 1 then
    redis.call("HMSET", key, "tokens", tokens, "last_refill_ts", now)
    redis.call("EXPIRE", key, ttl)
    return 0
end

tokens = tokens - 1
redis.call("HMSET", key, "tokens", tokens, "last_refill_ts", now)
redis.call("EXPIRE", key, ttl)

return 1