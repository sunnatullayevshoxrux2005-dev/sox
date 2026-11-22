package com.example.data.model

data class ParkRegion(
    val id: String,
    val name: String,
    val rate: Long,
    val hub: String
)

enum class SlotStatus {
    FREE,
    OCCUPIED,
    RESERVED
}

data class ParkingSlot(
    val id: String,
    val floor: Int,
    val code: String,
    val status: SlotStatus,
    val plate: String = ""
)

data class ParkingTransaction(
    val id: String,
    val time: String,
    val plate: String,
    val region: String,
    val service: String,
    val amount: Long
)

enum class ActivePanel {
    CLIENT,
    ADMIN
}

data class ToastMessage(
    val id: Long,
    val title: String,
    val message: String
)

val UZBEKISTAN_REGIONS: List<ParkRegion> = listOf(
    ParkRegion("toshkent_sh", "Toshkent shahri", 10_000L, "Toshkent — Amir Temur & Magic City Hub"),
    ParkRegion("toshkent_vil", "Toshkent viloyati", 6_000L, "Chirchiq & Nurafshon Smart Park"),
    ParkRegion("samarqand", "Samarqand viloyati", 7_000L, "Samarqand — Registon & Silk Road Hub"),
    ParkRegion("buxoro", "Buxoro viloyati", 6_500L, "Buxoro — Ark & Markaziy Turargoh"),
    ParkRegion("andijon", "Andijon viloyati", 5_500L, "Andijon — Bobur Shohko‘chasi Hub"),
    ParkRegion("fargona", "Farg‘ona viloyati", 5_500L, "Farg‘ona — Al-Farg‘oniy Smart Park"),
    ParkRegion("namangan", "Namangan viloyati", 5_500L, "Namangan — Afsonalar Vodiysi Hub"),
    ParkRegion("xorazm", "Xorazm viloyati", 6_000L, "Urganch & Xiva Ichan-Qal’a Park"),
    ParkRegion("qashqadaryo", "Qashqadaryo viloyati", 5_000L, "Qarshi — Mustaqillik Smart Hub"),
    ParkRegion("surxondaryo", "Surxondaryo viloyati", 5_000L, "Termiz — Markaziy Avtoturargoh"),
    ParkRegion("navoiy", "Navoiy viloyati", 5_500L, "Navoiy — Yangi Shahar Smart Park"),
    ParkRegion("jizzax", "Jizzax viloyati", 5_000L, "Jizzax — Sharof Rashidov Hub"),
    ParkRegion("sirdaryo", "Sirdaryo viloyati", 4_500L, "Guliston — Markaziy Stansiya Park"),
    ParkRegion("qoraqalpogiston", "Qoraqalpog‘iston Resp.", 5_000L, "Nukus — Savitskiy Smart Hub")
)

fun formatUZS(amount: Long): String {
    val str = amount.toString()
    val sb = StringBuilder()
    var count = 0
    for (i in str.length - 1 downTo 0) {
        sb.append(str[i])
        count++
        if (count % 3 == 0 && i != 0) {
            sb.append(',')
        }
    }
    return sb.reverse().toString() + " so‘m"
}
