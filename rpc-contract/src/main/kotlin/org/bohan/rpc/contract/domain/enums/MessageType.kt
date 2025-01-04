package org.bohan.rpc.contract.domain.enums

enum class MessageType(val code: Int) {

    REQUEST(0),

    RESPONSE(1);

    companion object {
        fun getCode(item: MessageType): Int {
            return item.code
        }
    }

}