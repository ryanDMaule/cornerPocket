package com.example.cornerpocket.Repositories

import com.example.cornerpocket.models.Opponent
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId

class OpponentRepository(passedRealm : Realm) {
    private val realm = passedRealm

    fun getOpponents() : Flow<MutableList<Opponent>> {
        return realm.query<Opponent>().asFlow().map { it.list.toMutableList() }
    }

    suspend fun insertOpponent(opponent : Opponent){
        realm.write { copyToRealm(opponent) }
    }

    fun getUpdatedOpponent(passedOpponent : Opponent) : Opponent? {
        val queriedOpponent = realm.query<Opponent>("_id == $0", passedOpponent._id).first().find()
        if (queriedOpponent != null){
            return queriedOpponent
        }
        return null
    }

    fun getOpponentViaId(id : ObjectId) : Opponent? {
        val queriedOpponent = realm.query<Opponent>("_id == $0", id).first().find()
        if (queriedOpponent != null){
            return queriedOpponent
        }
        return null
    }


}