package me.coweery.boilerplate.dao

import com.github.weery28.Vjooqx
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.vertx.core.json.JsonObject
import org.jooq.Table

abstract class EntityVjooqxDao<T : Entity>(
    protected val vjooqx: Vjooqx,
    protected val entityClass: Class<T>
) {

    protected abstract val table: Table<*>
    protected val idField by lazy { table.field("id", Long::class.java) }

    fun fetchById(id: Long): Maybe<T> {

        return vjooqx.fetch {
            select(table.asterisk()).from(table)
                .where(idField.eq(id))
        }
            .to(entityClass)
            .skipEmptyResultSet()
    }

    fun fetch(): Single<List<T>> {

        return vjooqx.fetch {
            select(table.asterisk()).from(table)
        }
            .toListOf(entityClass)
            .skipEmptyResultSetForList()
    }

    fun delete(id: Long): Completable {

        return vjooqx.execute {
            delete(table).where(idField.eq(id))
        }.ignoreElement()
    }

    fun insert(entity: T): Single<T> {

        return vjooqx.fetch {
            insertInto(table).set(
                JsonObject.mapFrom(entity).map.apply { remove("id") })
                .returning()
        }.to(entityClass)
    }

    fun update(entity: T): Single<T> {

        val entityMap = JsonObject.mapFrom(entity).map
        val id = entityMap.remove("id")
        return vjooqx.fetch {
            update(table).set(entityMap).returning()
        }.to(entityClass)
    }
}
