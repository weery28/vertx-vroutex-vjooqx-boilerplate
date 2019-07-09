package me.coweery.boilerplate.dao

import com.github.weery28.exceptions.EmptyResultSet
import io.reactivex.Maybe
import io.reactivex.Single

fun <T> Single<T>.skipEmptyResultSet(): Maybe<T> = toMaybe().onErrorComplete { it is EmptyResultSet }

fun <T> Single<List<T>>.skipEmptyResultSetForList(): Single<List<T>> = onErrorResumeNext {
    if (it is EmptyResultSet) {
        Single.just(emptyList())
    } else {
        Single.error(it)
    }
}
