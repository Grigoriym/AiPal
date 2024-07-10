package com.grappim.aipal.data.uuid

import com.benasher44.uuid.uuid4

class UuidGeneratorImpl : UuidGenerator {
    override fun getUuid4(): String = uuid4().toString()
}
