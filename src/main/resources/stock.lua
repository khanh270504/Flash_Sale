-- Check hang is exists

if redis.call('exists', KEYS[1]) == 0 then
    return 0
end

--Lay current quanlity

local current = tonumber(redis.call('get', KEYS[1]))

--Check hang co du khong
--ARGV[1](Tham so phu)
if current <= 0 or current < tonumber(ARGV[1]) then
    return 0
end

--Tru kho va tra ve 1(success)
redis.call('decrby', KEYS[1], ARGV[1])
return 1