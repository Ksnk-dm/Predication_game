package com.ksnk.predictions.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Predication(
    @PrimaryKey(autoGenerate = true) val uid: Int=0,
    @ColumnInfo(name = "predication_text") var predicationText: String?
)
{
    constructor() : this(predicationText = "")

    constructor(s: String) : this(predicationText = s)
}