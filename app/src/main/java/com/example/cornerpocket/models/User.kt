package com.example.cornerpocket.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Index
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class User: RealmObject {
    @PrimaryKey
    var _id: ObjectId = BsonObjectId()
    @Index // Allows queries to run faster when using this variable
    var name: String = ""
}