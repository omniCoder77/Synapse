package com.ethyllium.authservice.infrastructure.persistence.redis

import com.ethyllium.authservice.domain.service.LockService
import io.lettuce.core.ScriptOutputType
import io.lettuce.core.SetArgs
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands
import org.springframework.stereotype.Component
import java.util.concurrent.CompletionStage
import java.util.function.Function


@Component
class RedisLockAdapter(private val clusterCommands: RedisAdvancedClusterAsyncCommands<String, String>) : LockService {
    override fun lockAcquired(key: String, value: String, ttl: Long): CompletionStage<Boolean?> {
        return clusterCommands.set(key, value, SetArgs.Builder.nx().px(ttl)).thenApply("OK"::equals)
    }

    override fun releaseLock(
        key: String, value: String
    ): CompletionStage<Boolean?> {
        val luaScript =
            "if redis.call('GET', KEYS[1]) == ARGV[1] then " + "    return redis.call('DEL', KEYS[1]) " + "else " + "    return 0 " + "end"

        return clusterCommands.eval<String>(luaScript, ScriptOutputType.INTEGER, arrayOf(key), value)
            .thenApply(Function { result: Any? -> (result as Long?)!! > 0 })
    }
}