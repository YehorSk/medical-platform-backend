local key = KEYS[1]
local max_requests = tonumber(ARGV[1])
local ttl = tonumber(ARGV[2])

local current = redis.call('GET', key)

if current == false then
    redis.call('SET', key, 1, 'EX', ttl)
    return {1, ttl, 1}
end

local count = tonumber(current)
local remaining_ttl = redis.call('TTL', key)

if count < max_requests then
    local new_count = redis.call('INCR', key)
    return {new_count, remaining_ttl, 1}
end

return {count, remaining_ttl, 0}